package tree;

import data.*;

public class DiscreteNode extends SplitNode {
    public DiscreteNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    @Override
    void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        DiscreteAttribute discreteAttribute = (DiscreteAttribute) attribute;
        int childCount = 0;
        int currentBegin = beginExampleIndex;

        // Utilizzo del ciclo for-each grazie all'interfaccia Iterable
        for (String val : discreteAttribute) {
            int currentEnd = currentBegin;

            while (currentEnd <= endExampleIndex && trainingSet.getExplanatoryValue(currentEnd, attribute.getIndex()).equals(val)) {
                currentEnd++;
            }

            if (currentBegin < currentEnd) {
                // mapSplit è una List, si inserisce l'elemento con .add()
                mapSplit.add(new SplitInfo(val, currentBegin, currentEnd - 1, childCount));
                childCount++;
                currentBegin = currentEnd;
            } else {
                mapSplit.add(null);
            }
        }
    }

    @Override
    int testCondition(Object value) {
        for (int i = 0; i < mapSplit.size(); i++) {
            if (mapSplit.get(i) != null && mapSplit.get(i).getSplitValue().equals(value)) {
                return i;
            }
        }
        return -1;
    }
}