import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import MusicFile.MusicFile;

public class Publisher extends  Thread{

    static ArrayList<String> artistNames = new ArrayList<String>();                                                      //all artists of this publisher
    static ArrayList<File> songs = new ArrayList<File>();                                                                  //all publisher songs
    static ArrayList<String> brokers = new ArrayList<String>();                                                              //initial broker list (will be succeeded by brokerList)
    static ArrayList<Pair> brokers_complete = new ArrayList<Pair>();                                                    //contains Pair objects -> Pair{BigInteger[] range,ip,port} -> publisher will always know each broker's range,ip and port
    static Socket connection;
    //publisher specific
    static int myport = 6000;
    static String bounds = "A-L";
    //also lines 73,74
    //
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException, InvalidDataException, UnsupportedTagException {

        read(bounds);
        notifyBrokers(myport);
        getBrokerBounds();
        ServerSocket Pub_port = new ServerSocket(myport);
        while(true){
            System.out.println("Awaiting Broker Connection");
            Socket connection = Pub_port.accept();
            System.out.println("Broker Connected");
            Thread t = new Publisher(connection);
            t.start();
        }
    }

    public Publisher(Socket connection){
        this.connection = connection;
    }

    public void run(){
        try{
            ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
            String artist = (String) in.readObject();
            String song = (String) in.readObject();
            connection.shutdownInput();

            System.out.println("Sending data...");
            //finds requested file in publisher's files and begins the chunking proccess
            for(int i = 0;i < songs.size();i++){
                Mp3File mp3File = new Mp3File(songs.get(i));
                if((mp3File.getId3v2Tag().getTitle().equals(song)) && (mp3File.getId3v2Tag().getArtist().equals(artist))){
                    sendChunks(songs.get(i), connection);
                    break;
                }
                if(i == songs.size()-1){
                    ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                    out.writeObject("Song does not exist");
                    out.flush();
                    System.out.println("Song does not exist");
                }
            }
            connection.close();
        }catch (IOException | InvalidDataException | UnsupportedTagException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    //reads mp3s and brokers and puts !only correct(between bounds)! artistNames in ArrayList artists and songs in ArrayList songs
    public static void read(String bounds) throws IOException, InvalidDataException, UnsupportedTagException {
        File musicFiles = new File("E:\\Music");
        File broker = new File("C:\\Users\\leo10\\Desktop\\brokers.txt");
        File[] mp3s = musicFiles.listFiles();
        for (int i = 0;i < mp3s.length;i++) {
            if(mp3s[i].toString().contains(".mp3")){
                Mp3File mp3 = new Mp3File(mp3s[i]);
                if((mp3.getId3v2Tag().getArtist().charAt(0) >=  bounds.charAt(0)) && (mp3.getId3v2Tag().getArtist().charAt(0) <= bounds.charAt(bounds.length() - 1))) {
                    if(!artistNames.contains(mp3.getId3v2Tag().getArtist())){
                        artistNames.add(mp3.getId3v2Tag().getArtist());
                    }
                    songs.add(mp3s[i]);
                }
            }
        }
        Scanner scanner2 = new Scanner(broker);
        while(scanner2.hasNext()) {
            brokers.add(scanner2.nextLine());
        }
        System.out.println("Done Reading");
    }

    //sends bounds, port, broker info and publisher specific artistNames
    public static void notifyBrokers(int myport) throws IOException {
        for(int i = 0; i < brokers.size();i++) {
            String broker[] = brokers.get(i).split(" ");
            int port = Integer.valueOf(broker[1]);
            Socket connection = new Socket(broker[0], port);
            ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
            output.writeObject("Publisher");
            output.flush();
            output.writeObject(bounds);
            output.flush();
            output.writeObject(myport);
            output.flush();
            output.writeObject(brokers);
            output.flush();
            output.writeObject(artistNames);
            output.flush();
            output.close();
            System.out.println("Connected to Broker with port " + connection.getPort());
            connection.close();
        }
    }

    //gets complete broker information i.e. range from !one! broker
    public static void getBrokerBounds() throws IOException, ClassNotFoundException {
        System.out.println("Waiting for Broker Notification");
        ServerSocket Pub_port = new ServerSocket(myport);
        Socket connection = Pub_port.accept();
        ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
        brokers_complete = (ArrayList<Pair>) in.readObject();
        System.out.println("Broker Connected");
        in.close();
        connection.close();
        Pub_port.close();
    }

    //calculates AND sends the chunks
    public static void sendChunks(File mp3, Socket connection) throws IOException, InvalidDataException, UnsupportedTagException {
        Mp3File mp3File = new Mp3File(mp3);
        int size_limit = 512 * 1024;
        byte[] chunk_size = new byte[size_limit];
        int bytesAmount = 0;
        ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(mp3));
        int id = 1;
        while((bytesAmount = bis.read(chunk_size)) > 0){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(chunk_size, 0, bytesAmount);
            if(bytesAmount < size_limit){
                byte[] sm = new byte[bytesAmount];
                System.arraycopy(chunk_size, 0, sm, 0, bytesAmount);
                chunk_size = sm;
            }else{
                byte[] sm = new byte[size_limit];
                System.arraycopy(chunk_size, 0, sm, 0, bytesAmount);
                chunk_size = sm;
            }
            //creates musicFile Object. We did not create Value Object out of them since it would have no point
            MusicFile musicFile = new MusicFile(mp3File.getId3v2Tag().getTitle(), mp3File.getId3v2Tag().getArtist(),mp3File.getId3v2Tag().getDate(), mp3File.getId3v2Tag().getGenreDescription(), chunk_size, id);
            output.writeObject(musicFile);
            id++;
            output.flush();
        }
        output.writeObject(--id);
        output.flush();
        output.writeObject("End of mp3");
        output.flush();
        output.close();
        System.out.println("Data send");
    }
}
