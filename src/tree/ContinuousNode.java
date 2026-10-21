package tree;

import java.util.ArrayList;
import java.util.List;
import data.Attribute;
import data.ContinuousAttribute;
import data.Data;

public class ContinuousNode extends SplitNode {

    // Costruttore richiesto dall'Esercitazione 5
    public ContinuousNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, ContinuousAttribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    @Override
    void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        Double currentSplitValue = (Double) trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
        double bestInfoVariance = 0;
        List<SplitInfo> bestMapSplit = null;

        for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
            Double value = (Double) trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if (value.doubleValue() != currentSplitValue.doubleValue()) {
                double localVariance = new LeafNode(trainingSet, beginExampleIndex, i - 1).getVariance();
                double candidateSplitVariance = localVariance;
                localVariance = new LeafNode(trainingSet, i, endExampleIndex).getVariance();
                candidateSplitVariance += localVariance;

                if (bestMapSplit == null) {
                    bestMapSplit = new ArrayList<SplitInfo>();
                    bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
                    bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
                    bestInfoVariance = candidateSplitVariance;
                } else {
                    if (candidateSplitVariance < bestInfoVariance) {
                        bestInfoVariance = candidateSplitVariance;
                        bestMapSplit.set(0, new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
                        bestMapSplit.set(1, new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
                    }
                }
                currentSplitValue = value;
            }
        }

        if (bestMapSplit != null) {
            mapSplit = bestMapSplit;
            
            // Rimuovo split inutili se il ramo destro è vuoto o fittizio
            if (mapSplit.size() > 1) {
                if (mapSplit.get(1).beginIndex > mapSplit.get(1).getEndIndex()) {
                    mapSplit.remove(1);
                }
            }
        }
        // Se bestMapSplit è rimasto null, mapSplit rimarrà l'ArrayList vuoto 
        // inizializzato nel costruttore di SplitNode, evitando la NullPointerException.
    }

    /**
     * Implementazione di testCondition per attributi continui.
     * Confronta il valore in input con lo split value memorizzato in mapSplit.
     */
    @Override
    int testCondition(Object value) {
        Double val = (Double) value;
        // Il primo child (indice 0) rappresenta la condizione <=
        Double splitVal = (Double) mapSplit.get(0).getSplitValue();
        if (val.doubleValue() <= splitVal.doubleValue()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "CONTINUOUS SPLIT: attribute=" + attribute.getName() + " " + super.toString() + " Split Variance: " + getVariance() + "\n";
    }
}