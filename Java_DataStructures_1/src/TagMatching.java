import java.io.*;
import java.util.*;

public class TagMatching {

	public static void main(String[] args) {
		if (args.length == 0) { // checks for argument
			System.out.println("Proper Usage is: java TagMatching filename");
			System.exit(-1);
		}

		File file = new File(args[0]); // create file
		FileReader fr = null;
		StringStackImpl stack = new StringStackImpl(); // create stack for tags

		try {

			fr = new FileReader(file); // open file

		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
		}

		try {
			int ch;
			String tag = ""; // stores the tag temporarily
			String tag_check; // stores tag from pop method of stack
			boolean opened = false; // true if '<' found
			boolean closing = false; // true if '/' found
			boolean error = false; // true if wrong format is detected
			ch = (char) fr.read(); // read from file

			while (ch != -1 && ch != 65535) {	//while not reached end of file

				if (ch == '<' && opened) {	//detect two consecutive '<' 
					error = true;
					break;

				} else if (ch == '<' && !opened) {	//detect opening tag
					tag = "";
					opened = true;

				} else if (ch == '>' && opened && closing) {	//detect end of closing tag
					tag += (char) ch;
					opened = false;
					closing = false;
					System.out.println("Found closing tag(" + tag + "): " + tag.substring(2, tag.length() - 1));
					if (!stack.isEmpty()) {	//checks for empty stack
						tag_check = (String) stack.pop();	//store popped item

						if (!(tag.substring(2, tag.length() - 1))
								.equals(tag_check.substring(1, tag_check.length() - 1))) {	//checks if closing and opening tags match
							error = true;

						}

					} else {	//Empty stack with closing tag
						error = true;
						break;
					}

					tag = "";	//reset variable

				} else if (ch == '>' && opened && !closing) {	//detect end of opening tag
					tag += (char) ch;
					opened = false;
					stack.push(tag);	//push tag in the stack
					System.out.println("Found opening tag(" + tag + "): " + tag.substring(1, tag.length() - 1));
					tag = "";	//reset variable

				} else if (ch == '>' && !opened) {	//detect '>' without '<'
					error = true;
					break;

				} else if (ch == '/') {		//detect closing tag
					closing = true;
				}

				tag += (char) ch;
				ch = (char) fr.read();
				
			}

			if (!stack.isEmpty()) {	//check for unclosed tags
				error = true;

			}

			if (error) {	//check if any errors were detected
				System.out.println("Wrong format!");

			} else {
				System.out.println("Correct format!");

			}

		} catch (IOException exception) {
			System.err.print("IOException");
		}

	}

}