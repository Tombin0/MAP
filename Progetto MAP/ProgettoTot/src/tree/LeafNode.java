package tree;
import data.*;

public class LeafNode extends Node {
    // Valore dell'attributo di classe predetto nella foglia
    private Double predictedClassValue;

    public LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
        // Invoca il costruttore della superclasse Node 
        super(trainingSet, beginExampleIndex, endExampleIndex);
        
        // Calcola la media dei valori dell'attributo target nella partizione 
        double sum = 0;
            for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
                sum += trainingSet.getClassValue(i);
            }
        this.predictedClassValue = sum / (endExampleIndex - beginExampleIndex + 1);
    }

    public Double getPredictedClassValue() {
        return predictedClassValue;
    }

    public int getNumberOfChildren() {
        return 0;
    }

    public String toString() {
        // Concatena le info del nodo e il valore predetto 
        return "LEAF class=" + predictedClassValue + " " + super.toString();
    }
}