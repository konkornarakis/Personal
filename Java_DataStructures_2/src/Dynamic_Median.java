package project_top_k;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;

public class Dynamic_Median {

	private PQ maxHeap, minHeap;

	public Dynamic_Median(int k) {
		SongComparator cmp = new SongComparator();
		maxHeap = new PQ(k, cmp);
		SongComparator2 cmp2 = new SongComparator2();
		minHeap = new PQ(k, cmp2);
	}

	private static class SongComparator2 implements Comparator<Song> {

		public int compare(Song s1, Song s2) {
			if (s1.getLikes() == s2.getLikes()) {
				return s2.getTitle().compareTo(s1.getTitle());
			} else {
				return (new Integer(s2.getLikes())).compareTo(new Integer(s1.getLikes()));
			}
		}

	}

	private void rebalance() {

		if (Math.abs(maxHeap.size() - minHeap.size()) > 1) {

			if (maxHeap.size() > minHeap.size()) {
				minHeap.insert(maxHeap.getMax());
			} else {
				maxHeap.insert(minHeap.getMax());
			}
		}
	}

	private boolean isEmpty() {
		return maxHeap.size() == 0 && minHeap.size() == 0;
	}

	public void insert(Song song) {

		if (isEmpty()) {
			minHeap.insert(song);
		} else {
			if (Song.compareTo(song, median()) <= 0) {
				maxHeap.insert(song);
			} else {
				minHeap.insert(song);
			}
		}
		rebalance();
	}

	public Song median() {

		if (maxHeap.size() == minHeap.size()) {
			return (Song) minHeap.max();
		} else if (maxHeap.size() > minHeap.size()) {
			return (Song) maxHeap.max();
		} else {
			return (Song) minHeap.max();
		}

	}

	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("Usage Dynamic_Median path_name");
			System.exit(-1);
		} else {
			File f = new File(args[0]);
			try {
				Scanner sc = new Scanner(f);
				String field = "";
				int line = 0;
				int k = 10;
				Dynamic_Median dm = new Dynamic_Median(k);
				int id, likes;
				String title = "";

				while (sc.hasNext()) {
					line++;
					field = sc.next();
					id = Integer.parseInt(field);

					title = "";
					while (!sc.hasNextInt()) {
						field = sc.next();
						title += " " + field;
					}

					field = sc.next();
					likes = Integer.parseInt(field);

					if ((id < 0 || id > 9999) && title.length() > 80) {
						System.out.println("Error in line " + line);
					} else {
						dm.insert(new Song(id, title, likes));
					}

					if (line % 5 == 0) {

						System.out.println(
								"Median = " + dm.median().getLikes() + " likes, achieved by Song: " + dm.median());
					}

				}
			} catch (IOException ioe) {
				System.err.println("Error reading file");
			}

		}
	}

}
