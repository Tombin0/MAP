/* 
* La classe astratta Node rappresenta un nodo generico dell'albero
* che può essere un nodo di split o una foglia.
*/
package tree;
import data.*;

public abstract class Node {
    static int idNodeCount = 0;
    int idNode;
    int beginExampleIndex;
    int endExampleIndex;
    double variance;

    public Node(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
        this.idNode = idNodeCount++;
        this.beginExampleIndex = beginExampleIndex;
        this.endExampleIndex = endExampleIndex;
        this.variance = trainingSet.getVariance(beginExampleIndex, endExampleIndex);
    }

    int getIdNode() {
        return idNode;
    }

    int getBeginExampleIndex() {
        return beginExampleIndex;
    }

    int getEndExampleIndex() {
        return endExampleIndex;
    }

    public double getVariance() {
        return variance;
    }

    abstract int getNumberOfChildren();

    public String toString() {
        return "[Examples:" + beginExampleIndex + "-" + endExampleIndex + "] variance: " + variance;
    }
}