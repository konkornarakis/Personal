package project_top_k;
import java.util.Comparator;

final class SongComparator implements Comparator<Song> {

	public int compare(Song s1, Song s2) {
		if (s1.getLikes() == s2.getLikes()) {
			return s2.getTitle().compareTo(s1.getTitle());
		} else {
			return (new Integer(s1.getLikes())).compareTo(new Integer(s2.getLikes()));
		}
	}
	
}
