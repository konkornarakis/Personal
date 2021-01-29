package project_top_k;
import java.io.*;
import java.util.*;

public class Top_k_withPQ {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage Top_k_withPQ k path_name");
			System.exit(-1);
		} else {
			File f = new File(args[1]);
			try {
				Scanner sc = new Scanner(f);
				String field = "";
				int line = 0;
				int k = Integer.parseInt(args[0]);
				SongComparator cmp = new SongComparator();
				PQ pq = new PQ(k,cmp);
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
						pq.insert(new Song(id,title,likes));
						if(pq.size() > k)
							pq.getMin();
					}
				}
				
				System.out.println("The top " + k + " songs are:");
				
				
				pq.print();
				
			}catch(IOException ioe){
				System.err.println("Error reading file");
			}
			
			
		}
	}
}
