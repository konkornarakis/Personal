import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import MusicFile.MusicFile;

public class Broker extends Thread {
    static ArrayList<String> RegisteredPublishers = new ArrayList<String>();
    static ArrayList<String> RegisteredUsers = new ArrayList<String>();
    static ArrayList<String> brokers = new ArrayList<String>();                                                              //initial broker list (will be succeeded by brokerList)
    static ArrayList<String> artists = new ArrayList<String>();                                                          //contains ALL artistnames(both publishers) -> able to verify Artist existence
    static ArrayList<Pair> brokers_complete = new ArrayList<Pair>();                                                       //contains Pair objects -> Pair{BigInteger[] range,ip,port} -> publisher will always know each broker's range,ip and port
    static ArrayList<BigInteger> brokers_hashed = new ArrayList<BigInteger>();                                                   //contains BigIntger hash of every broker -> sorted
    static Socket connection;
    //broker specific
    static int p = 60000;
    //

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        init(p);
        SortBrokerHash();
        calculateKeys();
        if(Integer.toString(p).equals(brokers_complete.get(0).getPort())){
            notifyPublishers();                                                                                         //sends back the complete broker list -> !only one broker sends it! (same information no reason for every broker to send)
        }
        ServerSocket main_port = new ServerSocket(p);
        while(true){
            System.out.println("Awaiting Client Connection...");
            connection = main_port.accept();
            Thread t = new Broker(connection);
            t.start();
            System.out.println("Client Connected");
        }
    }

    public Broker(Socket connection){
        this.connection = connection;
    }

    public void run() {
        try{
            //pull from consumer
            ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
            String name = (String) input.readObject();
            String song = (String) input.readObject();
            boolean flag = true;
            for(String a : artists){
                if(a.equals(name)){
                    flag = false;
                    break;
                }
            }
            //if flag == true -> Artist does not exist
            if(flag){
                ObjectOutputStream o = new ObjectOutputStream(connection.getOutputStream());
                System.out.println("Artist does not exist");
                o.writeObject("Fail");
                o.flush();
                o.writeObject("Artist does not exist");
                o.flush();
                o.close();
                connection.close();
                return;
            }
            connection.shutdownInput();

            //find correct publisher
            System.out.println("Got connection: " + connection.getPort() + " " + name);
            RegisteredUsers.add(connection.getInetAddress().toString());
            String port = hashTopic(name);
            //if broker is correct
            if(port.equals(Integer.toString(p))){
                for(int i = 0;i < RegisteredPublishers.size();i++){
                    String s = RegisteredPublishers.get(i);
                    String tokens[] = s.split(" ");
                    //if (publisher correct) -> artistname between bounds
                    if(name.charAt(0) >=  tokens[2].charAt(0) && (name.charAt(0) <= tokens[2].charAt(tokens[2].length() - 1))){
                        System.out.println("Asking publisher port " + Integer.parseInt(tokens[1]));

                        //connect to right publisher
                        Socket connection1 = new Socket(tokens[0].substring(1),Integer.parseInt(tokens[1]));
                        pushToPublisher(connection1, name,song);
                        ObjectInputStream input1 = new ObjectInputStream(connection1.getInputStream());
                        pushToConsumerSuccess(input1);
                        connection.close();
                        connection1.close();
                    }
                }
            }else{
                String correct_broker_port = hashTopic(name);
                System.out.println(correct_broker_port);
                String correct_broker = "";
                //finds correct broker
                for(int i = 0;i < brokers_complete.size();i++){
                    if(brokers_complete.get(i).getPort().equals(correct_broker_port)){
                        correct_broker = brokers_complete.get(i).getIp() + " " + correct_broker_port;
                    }
                }
                pushToConsumerFail(correct_broker);
                connection.close();
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    //broker initialization
    public static void init(int p) throws IOException, ClassNotFoundException {
        ServerSocket port = new ServerSocket(p);
        while(true){
            System.out.println("Waiting for Publishers to Connect...");
            Socket connection = port.accept();
            ObjectInputStream input_stream = new ObjectInputStream(connection.getInputStream());
            Object input = input_stream.readObject();
            Object l = input_stream.readObject();
            Object Pport = input_stream.readObject();
            Object bro = input_stream.readObject();
            Object art = input_stream.readObject();
            if(input.equals("Publisher")) {
                if(!RegisteredPublishers.contains(connection.getPort())){
                    RegisteredPublishers.add(connection.getInetAddress() + " " + Integer.toString((int) Pport) + " " + (String) l);
                    System.out.println("Publisher with port " + Pport + "  Connected");
                    brokers = (ArrayList<String>) bro;                 //ip & port from all brokers
                    artists.addAll((ArrayList<String>) art);       //artists contains all artistNames from both punlishers
                    if(RegisteredPublishers.size() == 2){
                        System.out.println("Registered 2 Publishers");
                        break;
                    }
                }
            }else{
                System.out.println("Unexpected Object");
            }
        }
        port.close();
    }

    //finds hash for each brokers -> puts them in list -> sort the list
    public static void SortBrokerHash(){
        for(int i = 0;i < brokers.size();i++){
            String s = brokers.get(i);
            String tokens[] = s.split(" ");
            String port = tokens[1];
            String ip = tokens[0];
            String c = ip + port;
            BigInteger bi = getMd5(c);
            brokers_hashed.add(bi);
        }
        Collections.sort(brokers_hashed);
    }

    //finds range of each broker -> saves range,ip,port in Pair class objects -> puts each pair in ArrayList brokers_complete
    //brokers_complete will be send by one broker (currently the first) to all publishers
    public static void calculateKeys(){
        for(int j = 0;j < brokers.size();j++){
            BigInteger[] range = new BigInteger[2];
            String s = brokers.get(j);
            String tokens[] = s.split(" ");
            String port = tokens[1];
            String ip = tokens[0];
            BigInteger myhash = getMd5(ip+port);
            int count = 0;
            for(int i = 0;i < brokers_hashed.size();i++){
                if(myhash.compareTo(brokers_hashed.get(i)) == 0){
                    if(count == 0){
                        range[0] = brokers_hashed.get(brokers_hashed.size()-1);
                        range[1] = myhash;
                    }else{
                        range[0] = brokers_hashed.get(i-1);
                        range[1] = brokers_hashed.get(i);
                    }
                }else{
                    count++;
                }
            }
            Pair pair = new Pair(range,ip,port);
            brokers_complete.add(pair);
        }
    }

    //sends brokers_complete to all Publishers (only one broker
    public static void notifyPublishers() throws IOException, InterruptedException {
        for (int i = 0; i < RegisteredPublishers.size(); i++) {
            while (true) {
                try {
                    String s = RegisteredPublishers.get(i);
                    String tokens[] = s.split(" ");
                    Socket connection = new Socket(tokens[0].substring(1, tokens[0].length()), Integer.parseInt(tokens[1]));
                    ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                    out.writeObject(brokers_complete);
                    out.flush();
                    out.close();
                    connection.close();
                    break;
                } catch (SocketException e) {
                    break;
                }
            }
        }
    }

    //aks correct publisher for requested song
    public static void pushToPublisher(Socket connection, String a, String song) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
        out.writeObject(a);
        out.flush();
        out.writeObject(song);
        connection.shutdownOutput();
    }

    //sends chunks to consumer. this method both receives chunks from publisher AND sends it to consumer
    //counter is used to verify that all chunks arrived (last Object from publisher will be (int) chunks created
    public static void pushToConsumerSuccess(ObjectInputStream input) throws IOException, ClassNotFoundException {
        int count = 0;
        ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
        output.writeObject("Sending Data");
        Object in = input.readObject();
        //if first Object is String -> Song does not exist
        if(in instanceof String){
            System.out.println("Song does not exist");
            output.writeObject("Song does not exist");
            output.flush();
            input.close();
            return;
        }
        //receives and sends chunks (first chunk received at line 218)
        while(in instanceof MusicFile){
            output.writeObject(in);
            output.flush();
            count++;
            in = input.readObject();
        }
        int chunk_count = (int) in;   //gets (int) chunks created
        //verify chunk count
        if(count == chunk_count){
            output.writeObject(chunk_count);
            output.flush();
            System.out.println("All data sent to client");
            output.close();
        }else{
            System.out.println("Chunk lost");
        }
        input.close();
    }

    //sends correct broker information to consumer in case first "random" broker in consumer is not the right one
    public static void pushToConsumerFail(String correct_broker) throws IOException {
        System.out.println("Wrong Broker");
        ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
        output.writeObject("Notme");
        output.flush();
        output.writeObject(correct_broker);
        output.flush();
        output.close();
    }

    public static String hashTopic(String ArtistName) {
        for (int i = 0;i < brokers_complete.size();i++) {
            if (brokers_complete.get(i).getBound1().compareTo(brokers_complete.get(i).getBound2()) > 0) {
                if ((getMd5(ArtistName).compareTo(brokers_complete.get(i).getBound1()) > 0) || ((getMd5(ArtistName).compareTo(brokers_complete.get(i).getBound2()) <= 0))) {
                    return brokers_complete.get(i).getPort();
                }
            } else if ((getMd5(ArtistName).compareTo(brokers_complete.get(i).getBound1()) > 0) && ((getMd5(ArtistName).compareTo(brokers_complete.get(i).getBound2()) <= 0))) {
                return brokers_complete.get(i).getPort();

            }
        }
        System.out.println("NULL");
        return null;
    }

    public static BigInteger getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            /*  // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
            }*/
            return no;
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
