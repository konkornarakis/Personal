import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MultinomialNaiveBayes {

    // <key = word, value = word's index for the arraylists>
    private HashMap<String, Integer> vocabulary;

    // number of times the word has been part of a spam message
    private ArrayList<Integer> spamWordCounter;

    // number of times the word has been part of a ham message
    private ArrayList<Integer> hamWordCounter;

    // P(word|spam)
    //private ArrayList<Double> spamWordProb;

    // P(word|ham)
    //private ArrayList<Double> hamWordProb;

    private boolean[][] postStats;

    private int spamTrainingMsgCounter;
    private int hamTrainingMsgCounter;

    private int spamTestingMsgCounter;
    private int hamTestingMsgCounter;

    private int spamWordTotalCount;
    private int hamWordTotalCount;

    // for smoothing: if a = 0 laplace, if 0 < a < 1.0 lidstone
    private double a;

    private int vocabularySize;

    private String trainingFolder;

    private String testingFolder;

    public MultinomialNaiveBayes(String trainingFolder, String testingFolder) {

        vocabulary = new HashMap<>();

        spamWordCounter = new ArrayList<>();

        hamWordCounter = new ArrayList<>();

        //spamWordProb = new ArrayList<>();
        //hamWordProb = new ArrayList<>();

        spamTrainingMsgCounter = 0;
        hamTrainingMsgCounter = 0;

        spamTestingMsgCounter = 0;
        hamTestingMsgCounter = 0;

        spamWordTotalCount = 0;
        hamWordTotalCount = 0;

        a = 1;

        vocabularySize = 0;

        this.trainingFolder = trainingFolder;

        this.testingFolder = testingFolder;
    }

    void run() {

        System.out.println("Training");

        Scanner sc = null;

        try {

            File dir = new File(trainingFolder);

            String[] fileList = dir.list();

            String word;

            // true if file is spam
            boolean spam = false;

            int index;

            // index for arraylists
            int globalIndex = 0;

            for (String fileName : fileList) {

                sc = new Scanner(new File(dir + "//" + fileName));

                if (fileName.startsWith("spm") || fileName.endsWith("spam.txt")) {
                    spam = true;
                    ++spamTrainingMsgCounter;
                } else {
                    ++hamTrainingMsgCounter;
                }

                while (sc.hasNext()) {

                    word = sc.next();

                    // convert the word to lower case and replace punctuation
                    word = (word.toLowerCase()).replaceAll("\\p{Punct}", "");

                    // ignore empty strings
                    if (!word.equals("")) {

                        if (vocabulary.containsKey(word)) {

                            index = vocabulary.get(word);

                            if (spam) { // if message is spam, increase by 1 word's frequency in spam messages
                                spamWordCounter.set(index, spamWordCounter.get(index) + 1);
                            } else { // if message is ham, increase by 1 word's frequency in ham messages
                                hamWordCounter.set(index, hamWordCounter.get(index) + 1);
                            }

                        } else {

                            vocabulary.put(word, globalIndex);

                            if (spam) {
                                spamWordCounter.add(globalIndex, 1);
                                hamWordCounter.add(globalIndex, 0);
                            } else {
                                spamWordCounter.add(globalIndex, 0);
                                hamWordCounter.add(globalIndex, 1);
                            }

                            ++globalIndex;
                        }
                    }
                }

                spam = false;
            }

            for (int i = 0; i < spamWordCounter.size(); i++) {
                spamWordTotalCount += spamWordCounter.get(i);
                hamWordTotalCount += hamWordCounter.get(i);
            }

            System.out.println("Training: Finished");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }

        /*
         *
         */

        System.out.println("Testing");

        Scanner sc2 = null;

        try {

            File dir = new File(testingFolder);

            String[] fileList = dir.list();

            String word;

            // first column: true if spam, second column:true if it's predicted to be spam
            boolean[][] postStatsTemp = new boolean[fileList.length][2];

            vocabularySize = vocabulary.size();

            /* double spmProb = (double) spmCount / (spmCount + nspmCount);
             * prob a message is a spam, initialize with P(spam)
             * double nspmProb = (double) nspmCount / (spmCount + nspmCount);
             * prob a message is a ham, initialize with P(ham)
             */

            double[] tmp;

            ArrayList<String> sentence;

            for (int j = 0; j < fileList.length; j++) {

                sc2 = new Scanner(new File(dir + "//" + fileList[j]));

                if (fileList[j].startsWith("spm") || fileList[j].endsWith("spam.txt")) {
                    postStatsTemp[j][0] = true;
                    ++spamTestingMsgCounter;
                } else {
                    postStatsTemp[j][0] = false;
                    ++hamTestingMsgCounter;
                }

                sentence = new ArrayList<>();

                // Create an arraylist with all unique words of a test message
                while (sc2.hasNext()) {
                    word = sc2.next();

                    word = (word.toLowerCase()).replaceAll("\\p{Punct}", "");

                    if (!(word.equals("")))
                        sentence.add(word);
                }

                tmp = calculate(sentence);

                if (tmp[0] >= tmp[1]) {
                    postStatsTemp[j][1] = true;
                } else {
                    postStatsTemp[j][1] = false;
                }

            }
            postStats = postStatsTemp;
            System.out.println("Testing: Finished");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (sc2 != null)
                sc2.close();
        }

        /*
         *
         */

        System.out.println("Statistics");

        System.out.println("	Training data: " + spamTrainingMsgCounter + " spams. " + hamTrainingMsgCounter
                + " hams. Total: " + (spamTrainingMsgCounter + hamTrainingMsgCounter) + ".");
        System.out.println("	Testing data: " + spamTestingMsgCounter + " spams. " + hamTestingMsgCounter
                + " hams. Total: " + (spamTestingMsgCounter + hamTestingMsgCounter) + ".");

        int truePositives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;
        int accurate = 0;
        for (int i = 0; i < postStats.length; i++) {
            if (postStats[i][0] == false && postStats[i][1] == true) {
                ++falsePositives;
            }
            if (postStats[i][0] == true && postStats[i][1] == false) {
                ++falseNegatives;
            }
            if (postStats[i][0] == postStats[i][1]) {
                ++accurate;
            }
            if (postStats[i][0] == postStats[i][1] && postStats[i][0] == true) {
            	++truePositives;
            }
        }

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        System.out.println("	Filtering results: False positives: " + falsePositives + ", False negatives: "
                + falseNegatives
                + ", \n\tAccuracy: " + accurate + "/" + (spamTestingMsgCounter + hamTestingMsgCounter)
                + " (" + df.format((double) accurate * 100 / (spamTestingMsgCounter + hamTestingMsgCounter)) + "%),"
                + "\n\tPrecision: " + ( (double) (truePositives)/(double)(truePositives + falsePositives))*100 + "%,"
                + "\n\tRecall: " + ( (double) (truePositives)/(double)(truePositives + falseNegatives))*100 + "%,"
                + "\n\tF1: " + ( (double) 2*(truePositives)/(double)(2*truePositives + falsePositives + falseNegatives))*100 + "%.");
        System.out.println("Statistics: Finished");

    }

    public double[] calculate(ArrayList<String> sentence) {

        double spmProb = Math.log((double) spamTrainingMsgCounter / (spamTrainingMsgCounter + hamTrainingMsgCounter));

        double nspmProb = Math.log((double) hamTrainingMsgCounter / (spamTrainingMsgCounter + hamTrainingMsgCounter));

        double denominator = (spamWordTotalCount + a * vocabularySize);

        for (int i = 0; i < sentence.size(); i++) {

            if (!(vocabulary.containsKey(sentence.get(i)))) {

                spmProb *= a / denominator;
                nspmProb *= a / denominator;

            } else {

                spmProb += Math.log((((double) spamWordCounter.get(vocabulary.get(sentence.get(i)))) + a) / denominator);
                nspmProb += Math.log((((double) hamWordCounter.get(vocabulary.get(sentence.get(i)))) + a) / denominator);

                if (nspmProb == 0 || spmProb == 0) {
                    System.out.println();
                    System.exit(-2);
                }

            }
        }

        double[] stats = {spmProb, nspmProb};
        return stats;
    }

}