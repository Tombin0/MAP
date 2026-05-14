package tree;
import data.*;

// la classe DicreteNode rappresenta un nodo di split per attributi discreti.

public class DiscreteNode extends SplitNode {
    public DiscreteNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute) {
            // Invoca il costruttore di SplitNode
            super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        DiscreteAttribute discreteAttribute = (DiscreteAttribute) attribute;
        int nValues = discreteAttribute.getNumberOfDistinctValues();
        mapSplit = new SplitInfo[nValues];
        int childCount = 0; // Contatore dei rami non vuoti

        int currentBegin = beginExampleIndex;
            for (int i = 0; i < nValues; i++) {
                String val = discreteAttribute.getValue(i);
                int currentEnd = currentBegin;
        
                while (currentEnd <= endExampleIndex && trainingSet.getExplanatoryValue(currentEnd, attribute.getIndex()).equals(val)) {
                    currentEnd++;
                }
        
                if (currentBegin < currentEnd) {
                    mapSplit[i] = new SplitInfo(val, currentBegin, currentEnd - 1, childCount);
                    childCount++; // Incrementa solo se abbiamo trovato esempi
                    currentBegin = currentEnd;
                } else {
                    mapSplit[i] = null;
                }
            }
    }

    // Restituisce l'indice dello split corrispondente al valore dell'esempio
    int testCondition(Object value) {
        for (int i = 0; i < mapSplit.length; i++) {
            if (mapSplit[i].getSplitValue().equals(value)) {
                return i;
            }
        }
    return -1;
    }
}