// la classe SplitNode rappresenta un nodo di split in un albero decisionale. 
// Contiene informazioni sull'attributo utilizzato per lo split, i valori di split e la varianza associata allo split. 
// La classe è astratta e richiede l'implementazione dei metodi per impostare le informazioni 
// di split e testare le condizioni di split.

package tree;
import data.*;

public abstract class SplitNode extends Node {
	// Classe che collezione informazioni descrittive dello split
	public class SplitInfo{
		public Object splitValue;
		public int beginIndex;
		public int endIndex;
		public int numberChild;
		public String comparator="=";
		
		public SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
		}
		public SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild, String comparator){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
			this.comparator=comparator;
		}
		public int getBeginIndex(){
			return beginIndex;			
		}
		public int getEndIndex(){
			return endIndex;
		}
		public Object getSplitValue(){
			return splitValue;
		}
		public String toString(){
			return "child " + numberChild +" split value"+comparator+splitValue + "[Examples:"+beginIndex+"-"+endIndex+"]";
		}
		public String getComparator(){
			return comparator;
		}	
	}

	Attribute attribute;	

	SplitInfo mapSplit[];
	
	double splitVariance;
		
	abstract void setSplitInfo(Data trainingSet,int beginExampelIndex, int endExampleIndex, Attribute attribute);
	
	abstract int testCondition (Object value);
	
	public SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute){
			super(trainingSet, beginExampleIndex,endExampleIndex);
			this.attribute=attribute;
			trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
			setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
						
			//compute variance
			splitVariance=0;
			for(int i=0;i<mapSplit.length;i++){
				if (mapSplit[i] != null) {
					double localVariance=new LeafNode(trainingSet, mapSplit[i].getBeginIndex(),mapSplit[i].getEndIndex()).getVariance();
					splitVariance+=(localVariance);
				}
			}
	}
	
	Attribute getAttribute(){
		return attribute;
	}
	
	public double getVariance(){
		return splitVariance;
	}
	
	int getNumberOfChildren(){
		int count = 0;
    	for (int i = 0; i < mapSplit.length; i++) {
      		if (mapSplit[i] != null) {
            	count++;
        	}
    	}
    	return count;
	}
	
	SplitInfo getSplitInfo(int child){
		return mapSplit[child];
	}

	String formulateQuery() {
    	String query = "";
    	for (int i = 0; i < mapSplit.length; i++) {
        	// Mostra l'opzione solo se lo split esiste davvero
        	if (mapSplit[i] != null) {
           		query += (i + ":" + attribute + mapSplit[i].getComparator() + mapSplit[i].getSplitValue()) + "\n";
        	}
    	}
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
