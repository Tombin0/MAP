package tree;

import data.Data;
import data.Attribute;
import data.DiscreteAttribute;

public class DiscreteNode extends SplitNode {

    public DiscreteNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    @Override
    void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        DiscreteAttribute discreteAttribute = (DiscreteAttribute) attribute;
        int childCount = 0;
        int currentBegin = beginExampleIndex;

        for (String val : discreteAttribute) {
            int currentEnd = currentBegin;

            while (currentEnd <= endExampleIndex && trainingSet.getExplanatoryValue(currentEnd, attribute.getIndex()).equals(val)) {
                currentEnd++;
            }

            if (currentBegin < currentEnd) {
                mapSplit.add(new SplitInfo(val, currentBegin, currentEnd - 1, childCount));
                childCount++;
                currentBegin = currentEnd;
            } else {
                mapSplit.add(null);
            }
        }

        // Se l'intervallo contiene un unico valore discreto (es. solo "A")
        // svuotiamo mapSplit per forzare RegressionTree a creare una foglia ed evitare il loop
        if (childCount <= 1) {
            mapSplit.clear();
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