/* 
 * La classe RegressionTree rappresenta un albero di regressione.
 */
package tree;
import data.*;
import utility.Keyboard;

public class RegressionTree {
		
    private Node root;
    private RegressionTree childTree[];

    RegressionTree() {}

    public RegressionTree(Data trainingSet) {
        // Calcola il 10% 
        int minExamples = trainingSet.getNumberOfExamples() * 10 / 100;
        learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, minExamples);
    }

    private boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        // Ritorna true se il numero di esempi è sotto la soglia
        return (end - begin + 1) <= numberOfExamplesPerLeaf;
    }

    private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
        SplitNode bestNode = null;
        double minVariance = Double.MAX_VALUE;
        double initialVariance = trainingSet.getVariance(begin, end);

        for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
            Attribute attr = trainingSet.getExplanatoryAttribute(i);
            if (attr instanceof DiscreteAttribute) {
                DiscreteNode currentNode = new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) attr);
                //Lo split è migliore solo se la varianza calcolata è STRETTAMENTE MINORE
                if (currentNode.getVariance() < minVariance && currentNode.getVariance() < initialVariance) {
                    minVariance = currentNode.getVariance();
                    bestNode = currentNode;
                }
            }
        }
        return bestNode;
    }
    
    public void printRules() {
        System.out.println("********* RULES **********");
        printRules("");
    }
    // La funzione printRules è stata modificata per essere ricorsiva modo da costruire le regole in modo più chiaro e leggibile.
    private void printRules(String current) {
        if (root instanceof LeafNode) {
            System.out.println(current + " ==> Class=" + ((LeafNode) root).getPredictedClassValue());
        } else {
                for (int i = 0; i < childTree.length; i++) {
                    if (childTree[i] != null) {
                        SplitNode sn = (SplitNode) root;
                        String rule = current + (current.isEmpty() ? "" : " AND ") + 
                        sn.getAttribute().getName() + "=" + sn.getSplitInfo(i).getSplitValue();
                        childTree[i].printRules(rule);
                    }
                }
            }
    }
    
    // La classe SplitInfo rappresenta le informazioni di split per un nodo di split.
    
    public void learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        if (isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {
            root = new LeafNode(trainingSet, begin, end);
        } else {
            root = determineBestSplitNode(trainingSet, begin, end);

            // Se lo split esiste e ha effettivamente diviso i dati in rami diversi
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
                // Se lo split non è utile, creiamo una foglia con la media dei valori
                root = new LeafNode(trainingSet, begin, end);
                }
        }
}

		
    public void printTree(){
		System.out.println("********* TREE **********\n");
		System.out.println(toString());
		System.out.println("*************************\n");
	}
		
	public String toString(){
		String tree=root.toString()+"\n";
		if( root instanceof LeafNode){

			}
			else {     //split node
				for(int i=0;i<childTree.length;i++){
                    if (childTree[i] != null)
					tree +=childTree[i];
                }
			}
		return tree;
	}
    //la funzione predictClass è utilizzata per prevedere la classe di un esempio. 
    // Se il nodo è una foglia, restituisce il valore previsto. Altrimenti, formula la query e chiede all'utente di inserire la risposta, 
    // quindi procede ricorsivamente nel ramo corrispondente.

    public Double predictClass() throws UnknownValueException {
        if (root instanceof LeafNode) {
            return ((LeafNode) root).getPredictedClassValue();
        } else {
                int risp;
                System.out.println(((SplitNode) root).formulateQuery());
                risp = Keyboard.readInt(); 

                // Usa getNumberOfChildren() che ora conta solo i rami non nulli
                int nChildren = root.getNumberOfChildren();
        
                if (risp < 0 || risp >= nChildren) {
                    throw new UnknownValueException("The answer should be an integer between 0 and " + (nChildren - 1) + "!");
                } else {
                    return childTree[risp].predictClass();
                }
            }
    }
}
		
