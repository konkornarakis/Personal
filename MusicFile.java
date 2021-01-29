package MusicFile;

import java.io.Serializable;

public class MusicFile implements Serializable, Comparable<MusicFile> {
    private final String trackName;
    private final String artistName;
    private final String albumInfo;
    private final String genre;
    private final byte[] musicFileExtract;
    private final int id;

    public MusicFile(String trackName,String artistName,String albumInfo, String genre, byte[] musicFileExtract, int id){
        this.trackName = trackName;
        this.artistName = artistName;
        this.albumInfo = albumInfo;
        this.genre = genre;
        this.musicFileExtract = musicFileExtract;
        this.id = id;
    }

    public String getTrackName(){
        return trackName;
    }

    public String getArtistName(){
        return  artistName;
    }

    public String getAlbumInfo(){
        return albumInfo;
    }

    public String getGenre(){
        return  genre;
    }

    public byte[] getMusicFileExtract(){
        return  musicFileExtract;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(MusicFile o) {
        if(this.getId() > o.getId()){
            return this.getId();
        }else{
            return o.getId();
        }
    }
}

