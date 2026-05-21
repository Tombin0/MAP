package tree;

import data.*;
import java.util.List;
import java.util.ArrayList;

public abstract class SplitNode extends Node implements Comparable<SplitNode>, java.io.Serializable {

    // Aggiunto implements java.io.Serializable per permettere il salvataggio su file
    public class SplitInfo implements java.io.Serializable {
        public Object splitValue;
        public int beginIndex;
        public int endIndex;
        public int numberChild;
        public String comparator = "=";

        // Costruisce informazioni di split senza comparatore esplicito.
        public SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
        }

        // Costruisce informazioni di split con comparatore.
        public SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
            this.comparator = comparator;
        }

        // Restituisce l'indice iniziale del sottoinsieme.
        public int getBeginIndex() {
            return beginIndex;
        }

        // Restituisce l'indice finale del sottoinsieme.
        public int getEndIndex() {
            return endIndex;
        }

        // Restituisce il valore usato per lo split.
        public Object getSplitValue() {
            return splitValue;
        }

        // Restituisce la descrizione del ramo di split.
        public String toString() {
            return "child " + numberChild + " split value " + comparator + splitValue + " [Examples:" + beginIndex + "-" + endIndex + "]";
        }

        // Restituisce il comparatore usato nello split.
        public String getComparator() {
            return comparator;
        }
    }

    Attribute attribute;
    List<SplitInfo> mapSplit;
    double splitVariance;

    // Imposta i rami dello split.
    abstract void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute);
    // Valuta il valore di condizione per un ramo.
    abstract int testCondition(Object value);

    // Costruisce il nodo di split e calcola la varianza dello split.
    public SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex);
        this.attribute = attribute;
        trainingSet.sort(attribute, beginExampleIndex, endExampleIndex);

        // Inizializzazione della lista prima di popolarla
        this.mapSplit = new ArrayList<SplitInfo>();
        setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);

        // Calcolo della varianza complessiva dello split
        splitVariance = 0;
        for (SplitInfo si : mapSplit) {
            if (si != null) {
                double localVariance = new LeafNode(trainingSet, si.getBeginIndex(), si.getEndIndex()).getVariance();
                splitVariance += localVariance;
            }
        }
    }

    // Restituisce l'attributo usato per lo split.
    Attribute getAttribute() {
        return attribute;
    }

    // Restituisce la varianza totale dello split.
    public double getVariance() {
        return splitVariance;
    }

    // Conta i rami effettivi dello split.
    int getNumberOfChildren() {
        int count = 0;
        for (int i = 0; i < mapSplit.size(); i++) {
            if (mapSplit.get(i) != null) {
                count++;
            }
        }
        return count;
    }

    // Restituisce le informazioni del ramo selezionato.
    SplitInfo getSplitInfo(int child) {
        return mapSplit.get(child);
    }

    // Crea la domanda per l'utente basata sui valori dello split.
    String formulateQuery() {
        String query = "";
        for (int i = 0; i < mapSplit.size(); i++) {
            if (mapSplit.get(i) != null) {
                query += (i + ":" + attribute + mapSplit.get(i).getComparator() + mapSplit.get(i).getSplitValue()) + "\n";
            }
        }
        return query;
    }

    // Restituisce la descrizione testuale del nodo di split.
    public String toString() {
        String v = "SPLIT : attribute=" + attribute + " " + super.toString() + " Split Variance: " + getVariance() + "\n";
        for (int i = 0; i < mapSplit.size(); i++) {
            v += "\t" + mapSplit.get(i) + "\n";
        }
        return v;
    }

    /* Confronta due split node in base alla varianza dello split. */
    @Override
    public int compareTo(SplitNode o) {
        if (this.splitVariance < o.splitVariance) {
            return -1; // Minore varianza implica guadagno migliore
        } else if (this.splitVariance > o.splitVariance) {
            return 1;
        } else {
            return 0;
        }
    }
}