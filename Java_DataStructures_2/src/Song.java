package project_top_k;

public class Song {

	private int id, likes;
	private String title;
	
	public Song(int newId, String newTitle, int newLikes) {
		id = newId;
		title = newTitle;
		likes = newLikes;
	}
	
	public Song(Song song){
		id=song.getId();
		title=song.getTitle();
		likes=song.getLikes();
	}

	void setId(int newId) {
		id = newId;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}
	
	static int compareTo(Song s1, Song s2) {
		if (s1.getLikes() == s2.getLikes()) {
			return s2.getTitle().compareTo(s1.getTitle());
		} else {
			return (new Integer(s1.getLikes())).compareTo(new Integer(s2.getLikes()));
		}
	}
	
	@Override
	public String toString() {
		return id + " " + title + " " + likes;
	}
	
}
