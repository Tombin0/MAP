import java.io.FileNotFoundException;


 // classi della prof


class MainTest {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws FileNotFoundException{
		Data trainingSet= new Data("servo.dat");
		
		RegressionTree tree=new RegressionTree(trainingSet);
		
		tree.printRules();
		
		tree.printTree();
		
	}

}



class RegressionTree {
		
		RegressionTree(Data trainingSet){
			
			learnTree(trainingSet,0,trainingSet.getNumberOfExamples()-1,trainingSet.getNumberOfExamples()*10/100);
		}
		
		void learnTree(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf){
			if( isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)){
				//determina la classe che compare pi� frequentemente nella partizione corrente
				root=new LeafNode(trainingSet,begin,end);
			}
			else //split node
			{
				root=determineBestSplitNode(trainingSet, begin, end);
			
				if(root.getNumberOfChildren()>1){
					childTree=new RegressionTree[root.getNumberOfChildren()];
					for(int i=0;i<root.getNumberOfChildren();i++){
						childTree[i]=new RegressionTree();
						childTree[i].learnTree(trainingSet, ((SplitNode)root).getSplitInfo(i).beginIndex, ((SplitNode)root).getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
					}
				}
				else
					root=new LeafNode(trainingSet,begin,end);
				
			}
		}
			

		
		void printTree(){
			System.out.println("********* TREE **********\n");
			System.out.println(toString());
			System.out.println("*************************\n");
		}
		
		public String toString(){
			String tree=root.toString()+"\n";
			
			if( root instanceof LeafNode){
			
			}
			else //split node
			{
				for(int i=0;i<childTree.length;i++)
					tree +=childTree[i];
			}
			return tree;
		}
		
}
		
abstract class SplitNode extends Node {
	// Classe che colelzione informazioni descrittive dello split
	class SplitInfo{
		Object splitValue;
		int beginIndex;
		int endIndex;
		int numberChild;
		String comparator="=";
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
		}
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild, String comparator){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
			this.comparator=comparator;
		}
		int getBeginindex(){
			return beginIndex;			
		}
		int getEndIndex(){
			return endIndex;
		}
		 Object getSplitValue(){
			return splitValue;
		}
		public String toString(){
			return "child " + numberChild +" split value"+comparator+splitValue + "[Examples:"+beginIndex+"-"+endIndex+"]";
		}
		 String getComparator(){
			return comparator;
		}
	
		
	}

	Attribute attribute;	

	SplitInfo mapSplit[];
	
	double splitVariance;
		
	abstract void setSplitInfo(Data trainingSet,int beginExampelIndex, int endExampleIndex, Attribute attribute);
	
	abstract int testCondition (Object value);
	
	SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute){
			super(trainingSet, beginExampleIndex,endExampleIndex);
			this.attribute=attribute;
			trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
			setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
						
			//compute variance
			splitVariance=0;
			for(int i=0;i<mapSplit.length;i++){
					double localVariance=new LeafNode(trainingSet, mapSplit[i].getBeginindex(),mapSplit[i].getEndIndex()).getVariance();
					splitVariance+=(localVariance);
			}
	}
	
	Attribute getAttribute(){
		return attribute;
	}
	
	double getVariance(){
		return splitVariance;
	}
	
	int getNumberOfChildren(){
		return mapSplit.length;
	}
	
	SplitInfo getSplitInfo(int child){
		return mapSplit[child];
	}

	
	String formulateQuery(){
		String query = "";
		for(int i=0;i<mapSplit.length;i++)
			query+= (i + ":" + attribute + mapSplit[i].getComparator() +mapSplit[i].getSplitValue())+"\n";
		return query;
	}
	
	public String toString(){
		String v= "SPLIT : attribute=" +attribute +" "+ super.toString()+  " Split Variance: " + getVariance()+ "\n" ;
		
		for(int i=0;i<mapSplit.length;i++){
			v+= "\t"+mapSplit[i]+"\n";
		}
		
		return v;
	}
}

// classi nostre 

abstract class Node {
    static int idNodeCount = 0;
    int idNode;
    int beginExampleIndex;
    int endExampleIndex;
    double variance;

    Node(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
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

    double getVariance() {
        return variance;
    }

    abstract int getNumberOfChildren();

    public String toString() {
        return "[Examples:" + beginExampleIndex + "-" + endExampleIndex + "] variance: " + variance;
    }
}

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
