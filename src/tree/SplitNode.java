package tree;

import data.*;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public abstract class SplitNode extends Node implements Comparable<SplitNode>, Serializable {
    private static final long serialVersionUID = 1L;

    public class SplitInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        
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
        public int getBeginIndex() { return beginIndex; }
        public int getEndIndex() { return endIndex; }
        public Object getSplitValue() { return splitValue; }
        public String getComparator() { return comparator; }
        
        public String toString() {
            return "child " + numberChild + " split value" + comparator + splitValue + "[Examples:" + beginIndex + "-" + endIndex + "]";
        }
    }

    Attribute attribute;    
    List<SplitInfo> mapSplit;
    double splitVariance;
        
    abstract void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute);
    abstract int testCondition(Object value);
    
    public SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex);
        this.attribute = attribute;
        trainingSet.sort(attribute, beginExampleIndex, endExampleIndex);
        
        this.mapSplit = new ArrayList<SplitInfo>();
        setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
                    
        splitVariance = 0;
        for (int i = 0; i < mapSplit.size(); i++) {
            if (mapSplit.get(i) != null) {
                double localVariance = new LeafNode(trainingSet, mapSplit.get(i).getBeginIndex(), mapSplit.get(i).getEndIndex()).getVariance();
                splitVariance += localVariance;
            }
        }
    }
    
    Attribute getAttribute() { return attribute; }
    
    @Override
    public double getVariance() { return splitVariance; }
    
    int getNumberOfChildren() {
        int count = 0;
        for (int i = 0; i < mapSplit.size(); i++) {
            if (mapSplit.get(i) != null) {
                count++;
            }
        }
        return count;
    }
    
    SplitInfo getSplitInfo(int child) { return mapSplit.get(child); }

    String formulateQuery() {
        String query = "";
        int realIndex = 0;
        for (int i = 0; i < mapSplit.size(); i++) {
            if (mapSplit.get(i) != null) {
                query += (realIndex + ":" + attribute + mapSplit.get(i).getComparator() + mapSplit.get(i).getSplitValue()) + "\n";
                realIndex++;
            }
        }
        return query;
    }
    
    @Override
    public String toString() {
        // RTTI polimorfica applicata alla stringa descrittiva del nodo
        String label = (this instanceof DiscreteNode) ? "DISCRETE SPLIT: attribute=" : "CONTINUOUS SPLIT: attribute=";
        String v = label + attribute.getName() + " Nodo: " + super.toString() + " Split Variance: " + getVariance() + "\n";
        
        for (int i = 0; i < mapSplit.size(); i++) {
            if (mapSplit.get(i) != null) {
                v += "\t" + mapSplit.get(i) + "\n";
            }
        }
        return v;
    }

    @Override
    public int compareTo(SplitNode o) {
        if (this.splitVariance < o.splitVariance) return -1;
        else if (this.splitVariance > o.splitVariance) return 1;
        return 0;
    }
}