import java.util.ArrayList;
import java.util.HashSet;

public class ID3 implements MachineLearning {

    private Tree decisionTree;

    private static final double LOG2 = Math.log(2.0);

    ID3(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1) {

        HashSet<String> attributes = new HashSet<>();

        for (HashSet<String> example : list0)
            attributes.addAll(example);
        for (HashSet<String> example : list1)
            attributes.addAll(example);

        decisionTree = train(list0, list1, attributes, 0);
    }

    class Tree {

        String root;
        Tree[] children;
        int value;

        Tree(String s) {
            root = s;
            children = new Tree[2];
        }

        Tree(int cls) {
            root=null;
            children=null;
            value=cls;
        }

        void setChild(Tree child, int x) {

            children[x] = child;
        }

    }

    private Tree train(ArrayList<HashSet<String>> list0, ArrayList<HashSet<String>> list1, HashSet<String> attributes, int defaultCategory) {

        if (list0.isEmpty() && list1.isEmpty()) return new Tree(defaultCategory);
        if (list0.isEmpty()) return new Tree(1);
        if (list1.isEmpty()) return new Tree(0);
        int m = list0.size() >= list1.size() ? 0 : 1;
        if (attributes.isEmpty()) return new Tree(m);

        String best = selectAttribute(list0, list1, attributes);
        Tree tree = new Tree(best);

        HashSet<String> newAttributes = new HashSet<>(attributes);
        newAttributes.remove(best);
        ArrayList<HashSet<String>> newExamples0 = new ArrayList<>();
        ArrayList<HashSet<String>> newExamples1 = new ArrayList<>();

        for (HashSet<String> example0 : list0)
            if (!example0.contains(best))
                newExamples0.add(example0);

        for (HashSet<String> example1 : list1)
            if (!example1.contains(best))
                newExamples1.add(example1);

        Tree subtree = train(newExamples0, newExamples1, newAttributes, m);
        tree.setChild(subtree, 0);

        newExamples0 = new ArrayList<>();
        newExamples1 = new ArrayList<>();

        for (HashSet<String> example0 : list0)
            if (example0.contains(best))
                newExamples0.add(example0);

        for (HashSet<String> example1 : list1)
            if (example1.contains(best))
                newExamples1.add(example1);

        subtree = train(newExamples0, newExamples1, newAttributes, m);
        tree.setChild(subtree, 1);

        return tree;
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

    private double log2(double x) {
        return Math.log(x) / LOG2;
    }

    private double xlogx(double x) {

        if (x == 0) return 0;
        else return x * log2(x);
    }

    public int classify(HashSet<String> set) {

        if (decisionTree == null)
            System.out.println("Null Tree");

        return classify(set, decisionTree);
    }

    private int classify(HashSet<String> set, Tree tree) {

        if (tree.children == null) return tree.value;

        if(set.contains(tree.root)) return classify(set,tree.children[1]);
        else return classify(set,tree.children[0]);

    }
}
