import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BernoulliNaiveBayes implements MachineLearning{

    private ArrayList<HashMap<String, Double>> probabilities;
    private int totalExamples;
    private int class0;
    private int class1;

    BernoulliNaiveBayes(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1) {

        probabilities = new ArrayList<>(2);
        probabilities.add(0, new HashMap<>());
        probabilities.add(1, new HashMap<>());
        totalExamples = class0 = class1 = 0;

        train(list0, list1);
    }

    private void train(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1) {

        class0 = list0.size();
        class1 = list1.size();

        totalExamples = class0 + class1;
        double a;
        ArrayList<HashMap<String, Double>> prob;
        prob = new ArrayList<>(2);
        prob.add(0, new HashMap<>());
        prob.add(1, new HashMap<>());

        for (int j = 0; j < class0; j++) {
            for (String s : list0.get(j)) {
                if (prob.get(0).containsKey(s)) {
                    a = prob.get(0).get(s) + 1;
                    prob.get(0).replace(s, a);
                }else
                    prob.get(0).put(s,1.0);
            }
        }

        for (int j = 0; j < class1; j++) {
            for (String s : list1.get(j)) {
                if (prob.get(1).containsKey(s)) {
                    a = prob.get(1).get(s) + 1;
                    prob.get(1).replace(s, a);
                }else
                    prob.get(1).put(s,1.0);
            }
        }

        int size1=prob.get(0).size();
        int size2=prob.get(1).size();
        int limit = 200;
        int n1 = size1>limit?limit:size1;
        int n2 = size2>limit?limit:size2;

        for(int i=0;i<n1;i++){

            double max = 0;
            String str=null;
            for(String s:prob.get(0).keySet()){
                if(s.length()<=3) continue;
                double b =prob.get(0).get(s);
                if(b>max){
                    max=b;
                    str=s;
                }
            }
            probabilities.get(0).put(str,max);
            prob.get(0).remove(str);
        }

        for(int i=0;i<n2;i++){

            double max = 0;
            String str=null;
            for(String s:prob.get(1).keySet()){

                if(s.length()<=3) continue;
                double b =prob.get(1).get(s);
                if(b>max){
                    max=b;
                    str=s;
                }
            }
            probabilities.get(1).put(str,max);
            prob.get(1).remove(str);
        }

        for (String s : probabilities.get(0).keySet()) {
            a = (probabilities.get(0).get(s)+1) / (class0+2);
            probabilities.get(0).replace(s, a);
        }

        for (String s : probabilities.get(1).keySet()) {
            a = (probabilities.get(1).get(s)+1) / (class1+2);
            probabilities.get(1).replace(s, a);
        }

    }

    public int classify(HashSet<String> set) {

        double Prob0 = (double) class0 / totalExamples;
        double Prob1 = (double) class1 / totalExamples;
        double maxProb;
        double Prob;
        int possibleClass = 0;

        try {

            Prob = Prob0;

           for(String s:probabilities.get(0).keySet()){

               if(set.contains(s))
                   Prob *= probabilities.get(0).get(s);
               else
                   Prob *= 1 / (2.0 + class0);
           }

            maxProb = Prob;

            Prob = Prob1;

            for(String s:probabilities.get(1).keySet()){

                if(set.contains(s))
                    Prob *= probabilities.get(1).get(s);
                else
                    Prob *= (+1) / (2.0 + class1);
            }

            if (Prob > maxProb) {
                possibleClass = 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return possibleClass;
    }
}
