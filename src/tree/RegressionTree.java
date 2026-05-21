package tree;

import data.*;
import utility.Keyboard;
import java.io.*;
import java.util.TreeSet;

/**
 * Classe che modella l'intero albero di regressione.
 * Implementa Serializable per consentire il salvataggio su file dmp.
 */
public class RegressionTree implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Node root;
    private RegressionTree childTree[];

    /**
     * Costruttore di default.
     */
    public RegressionTree() {}

    /**
     * Costruttore che avvia la fase di apprendimento dell'albero a partire da un dataset.
     * @param trainingSet Il dataset di addestramento.
     */
    public RegressionTree(Data trainingSet) {
        int minExamples = trainingSet.getNumberOfExamples() * 10 / 100;
        learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, minExamples);
    }

    /**
     * Verifica se il sotto-insieme corrente di esempi deve costituire un nodo foglia.
     */
    private boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        return (end - begin + 1) <= numberOfExamplesPerLeaf;
    }

    /**
     * Determina il miglior nodo di split candidato per il sotto-insieme di dati.
     * Sfrutta l'RTTI (instanceof) per istanziare nodi discreti o continui,
     * e un TreeSet per selezionare automaticamente lo split ottimale (con varianza minima).
     */
    private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
        // TreeSet ordina automaticamente gli elementi grazie al compareTo implementato in SplitNode
        TreeSet<SplitNode> ts = new TreeSet<SplitNode>();
        double initialVariance = trainingSet.getVariance(begin, end);

        for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
            Attribute attr = trainingSet.getExplanatoryAttribute(i);
            SplitNode currentNode = null;

            // RTTI per discriminare tra attributi discreti e continui
            if (attr instanceof DiscreteAttribute) {
                currentNode = new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) attr);
            } else if (attr instanceof ContinuousAttribute) {
                currentNode = new ContinuousNode(trainingSet, begin, end, (ContinuousAttribute) attr);
            }

            // Lo split viene considerato solo se riduce la varianza iniziale ed è valido
            if (currentNode != null && currentNode.getNumberOfChildren() > 0 && currentNode.getVariance() < initialVariance) {
                ts.add(currentNode);
            }
        }

        // Il primo elemento del TreeSet corrisponde allo split con la minore splitVariance
        if (!ts.isEmpty()) {
            return ts.first();
        }
        return null;
    }

    /**
     * Algoritmo ricorsivo per la generazione della struttura dell'albero.
     */
    public void learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        if (isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {
            root = new LeafNode(trainingSet, begin, end);
        } else {
            root = determineBestSplitNode(trainingSet, begin, end);

            if (root != null && root.getNumberOfChildren() > 1) {
                childTree = new RegressionTree[root.getNumberOfChildren()];
                for (int i = 0; i < root.getNumberOfChildren(); i++) {
                    SplitNode sn = (SplitNode) root;
                    if (sn.getSplitInfo(i) != null) {
                        childTree[i] = new RegressionTree();
                        childTree[i].learnTree(trainingSet, sn.getSplitInfo(i).beginIndex, sn.getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
                    }
                }
            } else {
                root = new LeafNode(trainingSet, begin, end);
            }
        }
    }

    /**
     * Stampa a schermo le regole decisionali dell'albero.
     */
    public void printRules() {
        System.out.println("********* RULES **********");
        printRules("");
    }

    private void printRules(String current) {
        if (root instanceof LeafNode) {
            System.out.println(current + " ==> Class=" + ((LeafNode) root).getPredictedClassValue());
        } else if (root instanceof SplitNode) {
            SplitNode sn = (SplitNode) root;
            for (int i = 0; i < childTree.length; i++) {
                if (childTree[i] != null) {
                    String rule = current + (current.isEmpty() ? "" : " AND ") +
                            sn.getAttribute().getName() + sn.getSplitInfo(i).getComparator() + sn.getSplitInfo(i).getSplitValue();
                    childTree[i].printRules(rule);
                }
            }
        }
    }

    /**
     * Gestione guidata e ricorsiva della predizione interattiva.
     * Include un ciclo robusto per intercettare formati errati o fuori intervallo,
     * impedendo crash in caso di input non conformi (es. inserimento di numeri decimali).
     */
    public Double predictClass() throws UnknownValueException {
        if (root instanceof LeafNode) {
            return ((LeafNode) root).getPredictedClassValue();
        } else {
            int risp = -1;
            int nChildren = root.getNumberOfChildren();
            boolean validInput = false;

            // Disattiva la stampa automatica degli errori interni alla classe Keyboard
            utility.Keyboard.setPrintErrors(false);

            // Ciclo finché l'utente non inserisce una scelta numerica valida ed intera
            while (!validInput) {
                System.out.println(((SplitNode) root).formulateQuery());
                risp = Keyboard.readInt();

                // Se l'utente inserisce stringhe o decimali (es. 10.0), Keyboard restituisce Integer.MIN_VALUE
                if (risp >= 0 && risp < nChildren) {
                    validInput = true;
                } else {
                    System.out.println("Errore di input! Devi inserire obbligatoriamente un numero intero compreso tra 0 e " + (nChildren - 1) + ".\n");
                }
            }

            return childTree[risp].predictClass();
        }
    }

    /**
     * Serializza l'albero di regressione corrente su un file di output.
     * @param nomeFile Nome (o percorso completo) del file .dmp in cui salvare l'oggetto.
     */
    public void salva(String nomeFile) throws FileNotFoundException, IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nomeFile));
        out.writeObject(this);
        out.close();
    }

    /**
     * Deserializza e ricostruisce un albero di regressione salvato precedentemente in un archivio.
     * @param nomeFile Nome (o percorso completo) del file .dmp da cui caricare l'oggetto.
     * @return L'istanza ricostruita di RegressionTree.
     */
    public static RegressionTree carica(String nomeFile) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(nomeFile));
        RegressionTree tree = (RegressionTree) in.readObject();
        in.close();
        return tree;
    }

    /**
     * Restituisce la rappresentazione testuale dell'intera struttura dell'albero.
     */
    @Override
    public String toString() {
        String tree = root.toString() + "\n";
        if (!(root instanceof LeafNode) && childTree != null) {
            for (int i = 0; i < childTree.length; i++) {
                if (childTree[i] != null)
                    tree += childTree[i];
            }
        }
        return tree;
    }
}