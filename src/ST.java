import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class ST {

	private class TreeNode {
		WordFreq item;
		TreeNode l;
		TreeNode r;
		int number;

		public TreeNode(WordFreq word) {
			if (word == null)
				throw new IllegalArgumentException();
			item = word;
			l = null;
			r = null;
			number = 1;
		}

	}

	ST(int maxN) {
		head = null;
	}

	private TreeNode head;
	private List stopWords;

	boolean removeNull = false;

	WordFreq search(String w) {
		WordFreq wf = searchR(head, w);
		if (wf != null && getMeanFrequency() < wf.freq()) {
			int n = head.number;
			remove(w);
			head = insertT(head, wf);
			if (head != null)
				head.number = n;
		}

		return wf;
	}

	private WordFreq searchR(TreeNode h, String w) {
		if (h == null)
			return null;
		if (w.compareTo(h.item.key()) == 0)
			return h.item;
		if (w.compareTo(h.item.key()) < 0)
			return searchR(h.l, w);
		else
			return searchR(h.r, w);
	}

	void insert(WordFreq x) {
		head = insertR(head, x);
	}

	private TreeNode insertR(TreeNode h, WordFreq x) {
		if (h == null)
			return new TreeNode(x);
		if ((x.key()).compareTo(h.item.key()) < 0) {
			h.number++;
			h.l = insertR(h.l, x);
		} else {
			h.number++;
			h.r = insertR(h.r, x);
		}
		return h;
	}

	private TreeNode insertT(TreeNode h, WordFreq x) {
		if (h == null)
			return new TreeNode(x);
		if ((x.key()).compareTo(h.item.key()) < 0) {
			h.l = insertT(h.l, x);
			h = rotR(h);
		} else if ((x.key()).compareTo(h.item.key()) > 0) {
			h.r = insertT(h.r, x);
			h = rotL(h);
		}
		return h;
	}

	void update(String w) {
		WordFreq wf = search(w);
		if (wf == null) {
			wf = new WordFreq(w, 1);
			insert(wf);
		} else
			wf.addFreq();
	}

	void remove(String v) {
		removeNull = false;
		removeR(head, v);
	}

	private TreeNode removeR(TreeNode h, String v) {
		if (h == null) {
			removeNull = true;
			return null;
		}
		String w = h.item.key();
		if ((v.compareTo(w)) < 0) {
			h.l = removeR(h.l, v);
			if (!removeNull)
				h.number--;
		}

		if ((v.compareTo(w)) > 0) {
			h.r = removeR(h.r, v);
			if (!removeNull)
				h.number--;
		}
		if ((v.compareTo(w)) == 0) 
			h = joinLR(h.l, h.r);
			
		return h;
	}

	private TreeNode joinLR(TreeNode a, TreeNode b) {
		if (b == null)
			return a;
		b = partR(b, 0);
		b.l = a;
		return b;
	}

	private TreeNode partR(TreeNode h, int k) {

		int t = (h.l == null) ? 0 : h.l.number;
		if (t > k) {
			h.l = partR(h.l, k);
			h = rotR(h);
		}
		if (t < k) {
			h.r = partR(h.r, k - t - 1);
			h = rotL(h);
		}
		return h;
	}

	public void load(String filename) {
		try {
			File f = new File(filename);
			Scanner sc = new Scanner(f);
			String word = "";
			while (sc.hasNext()) {
				word = sc.next();
				word = word.toLowerCase();
				if ((word.charAt(0) >= 33 && word.charAt(0) <= 34) || (word.charAt(0) >= 40 && word.charAt(0) <= 47)
						|| (word.charAt(0) >= 58 && word.charAt(0) <= 63))
					if (word.length() > 1)
						word = word.substring(1, word.length());
				if (!(word.isEmpty()) && (word.charAt(word.length() - 1) >= 33 && word.charAt(word.length() - 1) <= 34)
						|| (word.charAt(word.length() - 1) >= 40 && word.charAt(word.length() - 1) <= 47)
						|| (word.charAt(word.length() - 1) >= 58 && word.charAt(word.length() - 1) <= 63))
						word = word.substring(0, word.length() - 1);
				if (!(word.isEmpty()) && !(word.matches(".*\\d+.*") && !(stopWords.find(word))))
					update(word);

			}
			sc.close();
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}

	int getTotalWords() {
		return traverseR(head);
	}

	int getDistinctWords() {
		if (head == null)
			return 0;
		return head.number;
	}

	int getFrequency(String w) {
		WordFreq wf = search(w);
		if (wf == null)
			return 0;
		return wf.freq();
	}

	WordFreq getMaximumFrequency() {
		return head.item;
	}

	double getMeanFrequency() {
		if (head == null)
			return 0;
		return (double) getTotalWords() / getDistinctWords();
	}

	void addStopWord(String w) {
		w = w.toLowerCase();
		stopWords.add(w);
	}

	void removeStopWord(String w) {
		w = w.toLowerCase();
		stopWords.remove(w);
	}

	void printÔreeAlphabetically(PrintStream stream) {
		printAlphabetically(head, stream);

	}

	private void printAlphabetically(TreeNode h, PrintStream stream) {
		if (h == null)
			return;
		printAlphabetically(h.l, stream);
		stream.print(h.item);
		printAlphabetically(h.r, stream);
	}

	void printÔreeByFrequency(PrintStream stream) {
		printByFrequency(head, stream);

	}

	private void printByFrequency(TreeNode h, PrintStream stream) {
		if (h == null)
			return;
		printByFrequency(h.l, stream);
		printByFrequency(h.r, stream);
		stream.print(h.item);
	}

	private TreeNode rotR(TreeNode h) {
		TreeNode x = h.l;
		h.l = x.r;
		x.r = h;
		return x;
	}

	private TreeNode rotL(TreeNode h) {
		TreeNode x = h.r;
		h.r = x.l;
		x.l = h;
		return x;
	}

	private int traverseR(TreeNode h) { // preorder
		if (h == null)
			return 0;
		int a = h.item.freq();
		int b = traverseR(h.l);
		int c = traverseR(h.r);
		return a + b + c;
	}

	private class List {

		private class Node {
			String sw;
			Node next, prev;

			Node(String stopWord) {
				sw = stopWord;
				next = prev = null;
			}
		}

		Node listHead;

		void add(String stopWord) {
			Node t = new Node(stopWord);
			t.next = listHead.next;
			listHead.next.prev = t;
			listHead.next = t;
			t.prev = listHead;
		}

		void remove(String stopWord) {
			Node x;
			for (x = listHead.next; x != null; x = x.next) {
				if ((x.sw).equals(stopWord))
					break;
			}
			if (x == null)
				return;
			x.next.prev = x.prev;
			x.prev.next = x.next;
		}

		boolean find(String str) {
			Node x;
			for (x = listHead.next; x != null; x = x.next) {
				if ((x.sw).equals(str))
					break;
			}
			return x != null;
		}

	}

}