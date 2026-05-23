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
        // Sort iniziale per X su tutto il dataset
        trainingSet.sort(trainingSet.getExplanatoryAttribute(0), 0, trainingSet.getNumberOfExamples() - 1);
        learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, minExamples);
    }

    private boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        return (end - begin + 1) <= numberOfExamplesPerLeaf;
    }

    private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
        TreeSet<SplitNode> ts = new TreeSet<SplitNode>();
        double initialVariance = trainingSet.getVariance(begin, end);

        // Valuta attributi discreti (dati già ordinati per X)
        for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
            Attribute attr = trainingSet.getExplanatoryAttribute(i);
            if (attr instanceof DiscreteAttribute) {
                SplitNode currentNode = new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) attr);
                if (currentNode.getNumberOfChildren() > 1 && currentNode.getVariance() < initialVariance) {
                    ts.add(currentNode);
                }
            }
        }

        // Valuta attributi continui
        for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
            Attribute attr = trainingSet.getExplanatoryAttribute(i);
            if (attr instanceof ContinuousAttribute) {
                trainingSet.sort(attr, begin, end);
                SplitNode currentNode = new ContinuousNode(trainingSet, begin, end, (ContinuousAttribute) attr);
                // Ripristina ordine discreto per confronto corretto
                trainingSet.sort(trainingSet.getExplanatoryAttribute(0), begin, end);
                if (currentNode.getNumberOfChildren() > 1 && currentNode.getVariance() < initialVariance) {
                    ts.add(currentNode);
                }
            }
        }

        return ts.isEmpty() ? null : ts.first();
    }

    public void learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        if (isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {
            root = new LeafNode(trainingSet, begin, end);
            return;
        }

        root = determineBestSplitNode(trainingSet, begin, end);

        if (root == null || root.getNumberOfChildren() <= 1) {
            root = new LeafNode(trainingSet, begin, end);
            return;
        }

        SplitNode sn = (SplitNode) root;
        childTree = new RegressionTree[sn.getNumberOfChildren()];

        // Se il best split è continuo, ri-sorta per Y prima di ricorrere
        // così i sottoalberi trovano i dati nell'ordine corretto
        if (sn instanceof ContinuousNode) {
            trainingSet.sort(sn.getAttribute(), begin, end);
        }

        for (int i = 0; i < sn.getNumberOfChildren(); i++) {
            if (sn.getSplitInfo(i) != null) {
                int childBegin = sn.getSplitInfo(i).getBeginIndex();
                int childEnd = sn.getSplitInfo(i).getEndIndex();
                childTree[i] = new RegressionTree();
                childTree[i].learnTree(trainingSet, childBegin, childEnd, numberOfExamplesPerLeaf);
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
                            sn.getAttribute().getName() + sn.getSplitInfo(i).getComparator()
                            + sn.getSplitInfo(i).getSplitValue();
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
                throw new UnknownValueException(
                        "The answer should be an integer between 0 and " + (nChildren - 1) + "!");
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

    public static RegressionTree carica(String nomeFile)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(nomeFile));
        RegressionTree tree = (RegressionTree) in.readObject();
        in.close();
        return tree;
    }
}