package tree;
import data.*;

public class LeafNode extends Node {
    // Valore dell'attributo di classe predetto nella foglia
    private Double predictedClassValue;

    // Crea una foglia e calcola il valore medio predetto.
    public LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
        super(trainingSet, beginExampleIndex, endExampleIndex);
        double sum = 0;
            for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
                sum += trainingSet.getClassValue(i);
            }
        this.predictedClassValue = sum / (endExampleIndex - beginExampleIndex + 1);
    }

    // Restituisce il valore predetto dalla foglia.
    public Double getPredictedClassValue() {
        return predictedClassValue;
    }

    // Una foglia non ha figli.
    public int getNumberOfChildren() {
        return 0;
    }

    // Restituisce la rappresentazione testuale della foglia.
    public String toString() {
        return "LEAF class=" + predictedClassValue + " " + super.toString();
    }
}