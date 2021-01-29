import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Scanner;
import java.text.DecimalFormat;

public class SpamFilterApp {

	private static ArrayList<HashMap<HashSet<String>, String>> trainMessages;
	private static ArrayList<HashMap<HashSet<String>, String>> testMessages;
	private static String trainPath = "C:\\Users\\niki\\Desktop\\cs\\ai\\lingspam_public\\lingspam_public\\bare\\part1";
	private static String testPath = "C:\\Users\\niki\\Desktop\\cs\\ai\\lingspam_public\\lingspam_public\\bare\\part2";

	private static ArrayList<HashSet<String>>[] readExamples(int hamCount, int spamCount, String path) {

		HashSet<String> set = new HashSet<>();
		trainMessages = new ArrayList<>();
		ArrayList<HashSet<String>>[] list = (ArrayList<HashSet<String>>[]) new ArrayList[2];
		list[0] = new ArrayList<>();
		list[1] = new ArrayList<>();
		Scanner sc;

		try {

			File directory = new File(path);
			String[] filename = directory.list();
			String word;

			if (filename != null) {
				for (int i = 0; i < filename.length; i++) {

					sc = new Scanner(new File(path + "\\" + filename[i]));

					while (sc.hasNext()) {

						word = sc.next().toLowerCase();
						set.add(word);
					}

					if (filename[i].startsWith("spms") && list[1].size() < spamCount)
						list[1].add(new HashSet<>(set));
					else if (list[0].size() < hamCount)
						list[0].add(new HashSet<>(set));

					HashMap<HashSet<String>, String> map = new HashMap<>();
					map.put(set, filename[i]);
					trainMessages.add(map);
					set = new HashSet<>();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

	private static ArrayList<HashSet<String>> readTestData(String path) {

		testMessages = new ArrayList<>();
		HashSet<String> set = new HashSet<>();
		ArrayList<HashSet<String>> list = new ArrayList<>();
		Scanner sc;

		try {

			File directory = new File(path);
			String[] filename = directory.list();
			String word;

			if (filename != null) {
				for (int i = 0; i < filename.length; i++) {

					sc = new Scanner(new File(path + "\\" + filename[i]));

					while (sc.hasNext()) {

						word = sc.next();
						set.add(word);
					}

					list.add(new HashSet<>(set));

					HashMap<HashSet<String>, String> map = new HashMap<>();
					map.put(set, filename[i]);
					testMessages.add(map);
					set = new HashSet<>();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

	private static void printResults(String name, MachineLearning ml, ArrayList<HashSet<String>>[] examples,
			ArrayList<HashSet<String>> test) {

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		int cls;
		int n0 = 0;
		int n1 = 0;
		double accuracy;

		for (int i = 0; i < 2; i++)
			for (HashSet<String> set : examples[i]) {
				cls = ml.classify(set);
				for (HashMap<HashSet<String>, String> map : trainMessages) {
					if (map.containsKey(set)) {
						if (map.get(set).startsWith("spm"))
							n1 += cls;
						if (!map.get(set).startsWith("spm"))
							n0 += cls;
					}
				}
			}

		accuracy = 1 - (n0 + 48 - n1) / 289.0;
		accuracy *= 100;

		System.out.println("\t" + name + "\n");
		System.out.println("Test on training data from " + trainPath);
		System.out.println(n0 + " ham messages were considered spam");
		System.out.println((48 - n1) + " spam messages were considered ham");
		System.out.println("\tAccuracy: " + df.format(accuracy) + " %");
		System.out.println("\tPrecision: " + ((double) n1 / (n1 + n0)) * 100 + "%," + "\n\tRecall: "
				+ ((double) n1 / (n1 + 48 - n1)

				) * 100 + "%," + "\n\tF1: " + ((double) 2 * n1 / (2 * n1 + n0 +

						48 - n1))
				+ "%.");
		System.out.println('\n');

		
		
		n0 = 0;
		n1 = 0;

		/*
		 * Test data
		 */
		for (HashSet<String> set : test) {
			cls = ml.classify(set);
			for (HashMap<HashSet<String>, String> map : testMessages) {
				if (map.containsKey(set)) {
					if (map.get(set).startsWith("spm"))
						n1 += cls;
					if (!map.get(set).startsWith("spm"))
						n0 += cls;
				}
			}
		}

		accuracy = 1 - (n0 + 48 - n1) / 289.0;
		accuracy *= 100;

		System.out.println("Test data from " + testPath);
		System.out.println(n0 + " ham messages were considered spam");
		System.out.println((48 - n1) + " spam messages were considered ham");
		System.out.println("\tAccuracy: " + df.format(accuracy) + " %");

		System.out.println("\tPrecision: " + ((double) n1 / (n1 + n0)) * 100 + "%," + "\n\tRecall: "
				+ ((double) n1 / (n1 + 48 - n1)

				) * 100 + "%," + "\n\tF1: " + ((double) 2 * n1 / (2 * n1 + n0 +

						48 - n1))
				+ "%.");
		System.out.println('\n');
	}

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		System.out.println("Input training folder path: ");
		trainPath = sc.nextLine();
		System.out.println("Input testing folder path: ");
		testPath = sc.nextLine();
		System.out.println();
		
		ArrayList<HashSet<String>> test = readTestData(testPath);

		/*
		 * Naive Bayes (Bernoulli)
		 */
		ArrayList<HashSet<String>>[] examples = readExamples(45, 48, trainPath);
		BernoulliNaiveBayes b = new BernoulliNaiveBayes(examples[0], examples[1]);
		printResults("Naive Bayes(Bernoulli)", b, examples, test);

		/*
		 * Naive Bayes (Multinomial)
		 */
		System.out.println("\t Naive Bayes(Multinomial) \n");
		System.out.println("Test on training data from " + trainPath);
		MultinomialNaiveBayes mb = new MultinomialNaiveBayes(trainPath, trainPath);
		mb.run();
		System.out.println("\nTest data from " + testPath);
		MultinomialNaiveBayes mb2 = new MultinomialNaiveBayes(trainPath, testPath);
		mb2.run();
		System.out.println();

		/*
		 * ID3
		 */
		examples = readExamples(80, 48, trainPath);
		ID3 id3 = new ID3(examples[0], examples[1]);
		printResults("ID3", id3, examples, test);

		/*
		 * Adaboost (with stumps)
		 */
		examples = readExamples(40, 48, trainPath);
		Adaboost a = new Adaboost(examples[0], examples[1], 15);
		printResults("Adaboost", a, examples, test);
		
		System.out.println("Press any key to exit...");
		sc.nextLine();
		System.out.println("App terminated");
	}
}
