class DiscreteNode extends SplitNode {

    public DiscreteNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        int start = beginExampleIndex;
        int count = 0;
        
        for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
            if (i == endExampleIndex || !trainingSet.getExampleValue(i, attribute.getIndex()).equals(trainingSet.getExampleValue(i + 1, attribute.getIndex()))) {
                count++;
            }
        }

        mapSplit = new SplitInfo[count];
        int splitIdx = 0;
        for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
            if (i == endExampleIndex || !trainingSet.getExampleValue(i, attribute.getIndex()).equals(trainingSet.getExampleValue(i + 1, attribute.getIndex()))) {
                Object val = trainingSet.getExampleValue(i, attribute.getIndex());
                mapSplit[splitIdx] = new SplitInfo(val, start, i, splitIdx);
                start = i + 1;
                splitIdx++;
            }
        }
    }

    int testCondition(Object value) {
        for (int i = 0; i < mapSplit.length; i++) {
            if (mapSplit[i].getSplitValue().equals(value)) {
                return i;
            }
        }
        return -1;
    }
}


class LeafNode extends Node {
    Double predictedClassValue;

    LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
        super(trainingSet, beginExampleIndex, endExampleIndex);
        this.predictedClassValue = trainingSet.getAverage(beginExampleIndex, endExampleIndex);
    }

    Double getPredictedClassValue() {
        return predictedClassValue;
    }

    int getNumberOfChildren() {
        return 0;
    }

    public String toString() {
        return "LEAF class=" + predictedClassValue + " " + super.toString();
    }
}

boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
    return (end - begin + 1) <= numberOfExamplesPerLeaf;
}

SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
    SplitNode bestNode = null;
    double minVariance = Double.MAX_VALUE;

    for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
        Attribute attr = trainingSet.getExplanatoryAttribute(i);
        if (attr instanceof DiscreteAttribute) {
            DiscreteNode node = new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) attr);
            if (node.getVariance() < minVariance) {
                minVariance = node.getVariance();
                bestNode = node;
            }
        }
    }
    
    if (bestNode != null) {
        trainingSet.sort(bestNode.getAttribute(), begin, end);
    }
    
    return bestNode;
}