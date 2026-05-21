package tree;
import data.*;
import java.util.List;
import java.util.ArrayList;

public abstract class SplitNode extends Node implements Comparable<SplitNode> {

    public class SplitInfo {
        public Object splitValue;
        public int beginIndex;
        public int endIndex;
        public int numberChild;
        public String comparator = "=";

        public SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
        }

        public SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
            this.comparator = comparator;
        }

        public int getBeginIndex() {
            return beginIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public Object getSplitValue() {
            return splitValue;
        }

        public String toString() {
            return "child " + numberChild + " split value" + comparator + splitValue + "[Examples:" + beginIndex + "-" + endIndex + "]";
        }

        public String getComparator() {
            return comparator;
        }
    }

    Attribute attribute;
    
    // Sostituito l'array statico con List ed ArrayList
    List<SplitInfo> mapSplit;
    double splitVariance;

    abstract void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute);
    abstract int testCondition(Object value);

    public SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex);
        this.attribute = attribute;
        trainingSet.sort(attribute, beginExampleIndex, endExampleIndex);

        // Inizializzazione della lista prima di popolarla
        this.mapSplit = new ArrayList<SplitInfo>();
        setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);

        // Calcolo della varianza complessiva dello split
        splitVariance = 0;
        for (SplitInfo si : mapSplit) {
            if (si != null) {
                double localVariance = new LeafNode(trainingSet, si.getBeginIndex(), si.getEndIndex()).getVariance();
                splitVariance += localVariance;
            }
        }
    }

    Attribute getAttribute() {
        return attribute;
    }

    public double getVariance() {
        return splitVariance;
    }

    int getNumberOfChildren() {
        int count = 0;
        for (int i = 0; i < mapSplit.size(); i++) {
            if (mapSplit.get(i) != null) {
                count++;
            }
        }
        return count;
    }

    SplitInfo getSplitInfo(int child) {
        return mapSplit.get(child);
    }

    String formulateQuery() {
        String query = "";
        for (int i = 0; i < mapSplit.size(); i++) {
            if (mapSplit.get(i) != null) {
                query += (i + ":" + attribute + mapSplit.get(i).getComparator() + mapSplit.get(i).getSplitValue()) + "\n";
            }
        }
        return query;
    }

    public String toString() {
        String v = "SPLIT : attribute=" + attribute + " " + super.toString() + " Split Variance: " + getVariance() + "\n";
        for (int i = 0; i < mapSplit.size(); i++) {
            v += "\t" + mapSplit.get(i) + "\n";
        }
        return v;
    }

    /**
     * Confronta due split node in base alla varianza dello split.
     */
    @Override
    public int compareTo(SplitNode o) {
        if (this.splitVariance < o.splitVariance) {
            return -1; // Minore varianza implica guadagno migliore
        } else if (this.splitVariance > o.splitVariance) {
            return 1;
        } else {
            return 0;
        }
    }
}