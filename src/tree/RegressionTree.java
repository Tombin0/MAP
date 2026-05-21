package tree;

import java.io.*;
import java.util.TreeSet;
import data.*;
import utility.*;

public class RegressionTree implements Serializable {
    private static final long serialVersionUID = 1L;

    private Node root;
    private RegressionTree childTree[];

    public RegressionTree() {}

    public RegressionTree(Data trainingSet) {
        int minExamples = trainingSet.getNumberOfExamples() * 10 / 100;
        learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, minExamples);
    }

    private boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        return (end - begin + 1) <= numberOfExamplesPerLeaf;
    }

    private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
        TreeSet<SplitNode> ts = new TreeSet<SplitNode>();
        double initialVariance = trainingSet.getVariance(begin, end);

        for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
            Attribute attr = trainingSet.getExplanatoryAttribute(i);
            SplitNode currentNode = null;

            // RTTI per discriminare dinamicamente il corretto tipo di nodo (Specifica Pagina 1)
            if (attr instanceof DiscreteAttribute) {
                currentNode = new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) attr);
            } else if (attr instanceof ContinuousAttribute) {
                currentNode = new ContinuousNode(trainingSet, begin, end, (ContinuousAttribute) attr);
            }

            if (currentNode != null && currentNode.getVariance() < initialVariance) {
                ts.add(currentNode);
            }
        }

        if (!ts.isEmpty()) {
            return ts.first();
        }
        return null;
    }

    public void learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        if (isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {
            root = new LeafNode(trainingSet, begin, end);
        } else {
            root = determineBestSplitNode(trainingSet, begin, end);

            if (root != null && root.getNumberOfChildren() > 1) {
                childTree = new RegressionTree[root.getNumberOfChildren()];
                for (int i = 0; i < root.getNumberOfChildren(); i++) {
                    SplitNode sn = (SplitNode) root;
                    if (sn.getSplitInfo(i) != null) {
                        childTree[i] = new RegressionTree();
                        childTree[i].learnTree(trainingSet, sn.getSplitInfo(i).getBeginIndex(), sn.getSplitInfo(i).getEndIndex(), numberOfExamplesPerLeaf);
                    }
                }
            } else {
                root = new LeafNode(trainingSet, begin, end);
            }
        }
    }

    public void printRules() {
        System.out.println("********* RULES **********");
        printRules("");
    }

    private void printRules(String current) {
        if (root instanceof LeafNode) {
            System.out.println(current + " ==> Class=" + ((LeafNode) root).getPredictedClassValue());
        } else if (root instanceof SplitNode) {
            SplitNode sn = (SplitNode) root;
            for (int i = 0; i < childTree.length; i++) {
                if (childTree[i] != null && sn.getSplitInfo(i) != null) {
                    String rule = current + (current.isEmpty() ? "" : " AND ") +
                            sn.getAttribute().getName() + sn.getSplitInfo(i).getComparator() + sn.getSplitInfo(i).getSplitValue();
                    childTree[i].printRules(rule);
                }
            }
        }
    }

    public void printTree() {
        System.out.println("********* TREE **********");
        System.out.print(this.toString());
        System.out.println("*************************\n");
    }

    @Override
    public String toString() {
        String tree = root.toString() + "\n";
        if (!(root instanceof LeafNode) && childTree != null) {
            for (int i = 0; i < childTree.length; i++) {
                if (childTree[i] != null) {
                    tree += childTree[i].toString();
                }
            }
        }
        return tree;
    }

    public Double predictClass() throws UnknownValueException {
        if (root instanceof LeafNode) {
            return ((LeafNode) root).getPredictedClassValue();
        } else {
            int risp;
            System.out.println(((SplitNode) root).formulateQuery());
            risp = Keyboard.readInt();
            int nChildren = root.getNumberOfChildren();

            if (risp < 0 || risp >= nChildren) {
                throw new UnknownValueException("The answer should be an integer between 0 and " + (nChildren - 1) + "!");
            } else {
                return childTree[risp].predictClass();
            }
        }
    }

    public void salva(String nomeFile) throws FileNotFoundException, IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nomeFile));
        out.writeObject(this);
        out.close();
    }

    public static RegressionTree carica(String nomeFile) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(nomeFile));
        RegressionTree tree = (RegressionTree) in.readObject();
        in.close();
        return tree;
    }
}