import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Adaboost implements MachineLearning{

    private static final double LOG2 = Math.log(2.0);
    private int N;
    private int M;
    private ArrayList<HashSet<String>> examples0;
    private ArrayList<HashSet<String>> examples1;
    private String bestAttr;

    Adaboost(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1, int M) {

        N = list0.size() + list1.size();
        this.M = M;
        train(list0, list1);

    }

    public void train(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1) {

        ArrayList<Result>[] h = new ArrayList[M];
        double[] z = new double[M];
        double[] w = new double[N];
        HashSet<String> attributes = new HashSet<>();

        double initialWeight = 1.0 / N;
        for (int i = 0; i < N; i++)
            w[i] = initialWeight;

        for (HashSet<String> example : list0)
            attributes.addAll(example);
        for (HashSet<String> example : list1)
            attributes.addAll(example);

        for (int m = 0; m < M; m++) {

            String best = selectAttribute(list0, list1, attributes);
            attributes.remove(best);
            h[m] = classifyByBestAttr(list0, list1, w, best);
            double error = 0;
            double amountOfSay;

            for (int j = 0; j < N; j++) {
                if (!h[m].get(j).match()) error += w[j];

            }

            z[m] = error==1?0:error==0?1:log2((1 - error) / error);
            amountOfSay = 1 / 2.0 * z[m];

            for (int j = 0; j < N; j++) {
                if (h[m].get(j).match()) w[j] *= Math.exp(amountOfSay);

            }

            w = normalize(w);

        }

        weightedMajority(h, z);
    }

    private double[] normalize(double[] w) {

        double sum = 0;
        for (int i = 0; i < N; i++)
            sum += w[i];

        for (int i = 0; i < N; i++)
            w[i] /= sum;

        return w;
    }

    private ArrayList<Result> classifyByBestAttr(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1, double[] w, String attr) {

        ArrayList<Result> h = new ArrayList<>();
        double sum = 0;
        int N0 = list0.size();
        Random r = new Random();

        for (int j = 0; j < N; j++) {

            double random=r.nextDouble();
            int k;
            for (k = 0; k < N; k++) {
                sum += w[k];
                if (random < sum) break;
            }

            if (k < N0) {
                HashSet<String> set = list0.get(k);
                int classifiedcls = set.contains(attr) ? 1 : 0;
                Result result = new Result(classifiedcls, 0, set);
                h.add(result);
            } else {
                HashSet<String> set = list1.get(k - N0);
                int classifiedcls = set.contains(attr) ? 1 : 0;
                Result result = new Result(classifiedcls, 1, set);
                h.add(result);
            }
        }

        return h;
    }

    private void weightedMajority(ArrayList<Result>[] h, double[] z) {

        examples0 = new ArrayList<>();
        examples1 = new ArrayList<>();

        HashSet<Result> set = new HashSet<>();
        for (int i = 0; i < M; i++)
            set.addAll(h[i]);

        for (Result result : set) {

            double sum = 0;

            for (int i = 0; i < M; i++)
                if (h[i].contains(result)) {
                    int a = h[i].get(h[i].indexOf(result)).classifiedCls;
                    sum += (z[i] * a);
                }

            if (sum > 0.5) examples1.add(result.set);
            else examples0.add(result.set);

        }

        HashSet<String> attr = new HashSet<>();
        for (HashSet<String> example : examples0)
            attr.addAll(example);
        for (HashSet<String> example : examples1)
            attr.addAll(example);

        bestAttr = selectAttribute(examples0, examples1, attr);

    }

    public int classify(HashSet<String> set) {

        return set.contains(bestAttr) ? 1 : 0;
    }

    class Result {

        int classifiedCls;
        int cls;
        HashSet<String> set;

        Result(int classifiedCls, int cls, HashSet<String> set) {

            this.classifiedCls = classifiedCls;
            this.cls = cls;
            this.set = new HashSet<>(set);
        }

        boolean match() {
            return cls == classifiedCls;
        }

        @Override
        public int hashCode() {
            return set.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return hashCode() == obj.hashCode();
        }
    }

    private String selectAttribute(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1, HashSet<String> attributes) {

        double maxIG = 0;
        String bestAttribute = null;

        for (String attr : attributes) {

            double ig = IG(list0, list1, attr);
            if (ig > maxIG) {
                maxIG = ig;
                bestAttribute = attr;
            }
        }

        return bestAttribute;
    }

    private double IG(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1, String s) {

        double[] Px = Px(list0, list1, s);
        double[] PCX = H(list0, list1, s);
        return H(list0, list1) - Px[0] * PCX[0] - Px[1] * PCX[1];
    }

    private double[] Px(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1, String s) {

        double result = 0;
        int size = 0;

        for (HashSet<String> set : list0) {
            if (set.contains(s))
                result++;
            size++;
        }
        for (HashSet<String> set : list1) {
            if (set.contains(s))
                result++;
            size++;
        }

        double[] Px = new double[2];
        Px[1] = result / size;
        Px[0] = 1 - Px[1];
        return Px;
    }

    private double[] PCX(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1, String s, int x) {

        int result = 0;
        double class0, class1;

        if (x == 1) {
            for (HashSet<String> set : list0) {
                if (set.contains(s))
                    result++;
            }
            class0 = result;

            for (HashSet<String> set : list1) {
                if (set.contains(s))
                    result++;
            }
            class1 = result - class0;
        } else {
            for (HashSet<String> set : list0) {
                if (!set.contains(s))
                    result++;
            }
            class0 = result;

            for (HashSet<String> set : list1) {
                if (!set.contains(s))
                    result++;

            }
            class1 = result - class0;
        }

        double[] PCX = new double[2];
        PCX[0] = class0 / result;
        PCX[1] = class1 / result;

        return PCX;
    }

    private double[] H(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1, String s) {

        double[] P0 = PCX(list0, list1, s, 0);
        double[] P1 = PCX(list0, list1, s, 1);
        double entropy0 = -(xlogx(P0[0]) + xlogx(P0[1]));
        double entropy1 = -(xlogx(P1[0]) + xlogx(P1[1]));
        double[] H = new double[2];
        H[0] = entropy0;
        H[1] = entropy1;
        return H;
    }

    private double H(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1) {

        double P0 = P(list0, list1, 0);
        double P1 = P(list0, list1, 1);
        double entropy = -(xlogx(P0) + xlogx(P1));
        return entropy;
    }

    private double P(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1, int x) {

        double result;
        if (x == 1)
            result = list0.size();
        else
            result = list1.size();

        return result / (list0.size() + list1.size());
    }

    private double xlogx(double x) {

        if (x == 0) return 0;
        else return x * log2(x);
    }

    private double log2(double x) {
        return Math.log(x) / LOG2;
    }

}
