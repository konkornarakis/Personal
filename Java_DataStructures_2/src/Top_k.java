package project_top_k;

import java.util.Scanner;
import java.io.*;

public class Top_k {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("Usage Top_k k path_name");
			System.exit(-1);
		} else {
			File f = new File(args[1]);
			try {
				Scanner sc = new Scanner(f);
				String field = "";
				int line = 0;
				int k = Integer.parseInt(args[0]);

				while (sc.hasNextLine()) {
					line++;
					field = sc.nextLine();
				}

				if (k > line) {
					System.out.println("File does not contain " + k + " songs.");
					System.exit(-2);
				}

				sc.close();
				sc = new Scanner(f);

				Song[] list = new Song[line];
				line = 0;

				int id, likes;
				int listPointer = 0;
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

						list[listPointer] = new Song(id, title, likes);
						listPointer++;
					}

				}

				Quicksort qs = new Quicksort();

				qs.sort(list);

				System.out.println("\nThe top " + k + " songs are:");
				for (int i = list.length - 1; i >= list.length - k; i--) {
					System.out.println(list[i]);
				}

			} catch (IOException ioe) {
				System.err.println("Error reading file");
			}

		}
	}
}
