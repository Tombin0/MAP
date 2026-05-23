/* 
* La classe astratta Node rappresenta un nodo generico dell'albero
* che può essere un nodo di split o una foglia.
*/
package tree;
import data.*;

public abstract class Node implements java.io.Serializable {
    static int idNodeCount = 0;
    int idNode;
    int beginExampleIndex;
    int endExampleIndex;
    double variance;

    // Inizializza il nodo con l'intervallo di esempi e calcola la varianza.
    public Node(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
        this.idNode = idNodeCount++;
        this.beginExampleIndex = beginExampleIndex;
        this.endExampleIndex = endExampleIndex;
        this.variance = trainingSet.getVariance(beginExampleIndex, endExampleIndex);
    }

    // Restituisce l'ID del nodo.
    int getIdNode() {
        return idNode;
    }

    // Restituisce l'indice iniziale del sottoinsieme di esempio.
    int getBeginExampleIndex() {
        return beginExampleIndex;
    }

    // Restituisce l'indice finale del sottoinsieme di esempio.
    int getEndExampleIndex() {
        return endExampleIndex;
    }

    // Restituisce la varianza del nodo.
    public double getVariance() {
        return variance;
    }

    // Restituisce il numero di figli del nodo.
    abstract int getNumberOfChildren();

    // Restituisce la descrizione testuale del nodo.
    public String toString() {
        return "[Examples:" + beginExampleIndex + "-" + endExampleIndex + "] variance: " + variance;
    }
}