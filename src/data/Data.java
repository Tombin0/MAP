package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Classe Data: gestisce un dataset letto da file, composto da attributi
 * esplicativi (discreti) e un attributo target (continuo).
 * Supporta l'ordinamento dei dati rispetto a un attributo discreto tramite quicksort.
 */


public class Data {
	
	/** Matrice che contiene tutti gli esempi (righe) e i loro valori (colonne) */
	private Object data [][];

	/** Numero totale di esempi nel dataset */
	private int numberOfExamples;

	/** Array degli attributi esplicativi (descrittivi) */
	private Attribute explanatorySet[];

	/** Attributo target (continuo, ovvero numerico) */
	private ContinuousAttribute classAttribute;
	
	    public Data(String fileName) throws TrainingDataException {
        
            File inFile = new File (fileName);
            Scanner sc;
        
            // Proviamo ad aprire il file. Se non c'è, lanciamo il nostro allarme personalizzato!
            try {
                sc = new Scanner(inFile);
            } catch (FileNotFoundException e) {
                throw new TrainingDataException("Impossibile trovare il file specificato: " + fileName);
            }

            // Controllo se il file è completamente vuoto
            if (!sc.hasNextLine()) {
                sc.close();
                throw new TrainingDataException("Il file è vuoto!");
            }

            // Lettura della prima riga: deve contenere "@schema"
            String line = sc.nextLine();
            if(!line.contains("@schema")) {
                sc.close();
                throw new TrainingDataException("Errore nello schema: manca l'intestazione @schema");
            }
        
        String s[]=line.split(" ");

        // Popolare explanatory Set 
        explanatorySet = new Attribute[new Integer(s[1])];
        short iAttribute=0;

        // Lettura degli attributi (esplicativi e target) fino a "@data"
        line = sc.nextLine();
        while(!line.contains("@data")){
            s=line.split(" ");
                if(s[0].equals("@desc")) { 
                    // Attributo discreto: es. "@desc motor A,B,C,D,E"
                    String discreteValues[]=s[2].split(",");
                    explanatorySet[iAttribute] = new DiscreteAttribute(s[1],iAttribute, discreteValues);
                }
                else if(s[0].equals("@target")) {
                        // Attributo target (continuo): es. "@target class"
                        classAttribute=new ContinuousAttribute(s[1], iAttribute);
                }       
            iAttribute++;
            line = sc.nextLine();
        }
              
        // Avvalorare numero di esempi
        numberOfExamples=new Integer(line.split(" ")[1]);
        
        // Popolare data
        data=new Object[numberOfExamples][explanatorySet.length+1];
        short iRow=0;

        // Lettura di ogni riga di dati
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            // Evita di crashare se ci sono righe vuote alla fine del file
                if (line.trim().isEmpty()) continue; 
            s=line.split(",");

            // Valori degli attributi esplicativi (stringhe)
            for(short jColumn=0;jColumn<s.length-1;jColumn++) {
                data[iRow][jColumn]=s[jColumn];
            }

            // Ultimo valore: attributo target (numerico)
            data[iRow][s.length-1] = new Double(s[s.length-1].trim());
            iRow++;
        }
    sc.close();
    }

/*
 * Restituisce l'oggetto ContinuousAttribute che rappresenta l'attributo target (classe).
 */
public ContinuousAttribute getClassAttribute() {
    return classAttribute;
}

/*
 * Restituisce il valore dell'attributo di classe per un esempio specifico.
 * Poiché l'attributo target è l'ultimo nella riga della matrice data, 
 * l'indice di colonna è pari alla lunghezza dell'explanatorySet.
 */
public Double getClassValue(int exampleIndex) {
    // L'indice di colonna corretto per il target è explanatorySet.length
    return (Double) data[exampleIndex][explanatorySet.length];
}
/*
	 * Rappresenta un attributo continuo (numerico).
	 * Tipicamente usato come attributo target (classe).
	 */
	
public class ContinuousAttribute extends Attribute {
    public ContinuousAttribute(String name, int index) {
        super(name, index); 
    }
}

/**
	 * Restituisce una rappresentazione testuale dell'intero dataset.
	 * Ogni riga contiene i valori separati da virgola.
	 */

public String toString(){
		String value="";
		    for(int i=0;i<numberOfExamples;i++){
			    for(int j=0;j<explanatorySet.length;j++)
				    value+=data[i][j]+",";
			value+=data[i][explanatorySet.length]+"\n";
		    }
		return value;
	}

/* Ordina gli esempi del dataset rispetto a un attributo,
	 * nell'intervallo [beginExampleIndex, endExampleIndex].
	 */

	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex){	
			quicksort(attribute, beginExampleIndex, endExampleIndex);
	}
	

/* l'algoritmo quicksort per l'ordinamento di un array di interi A
* usando come relazione d'ordine totale "<="
*/

private void quicksort(Attribute attribute, int inf, int sup){
		if(sup>=inf){	
			int pos;
			// Partiziona l'array e ottiene la posizione del pivot
			pos=partition((DiscreteAttribute)attribute, inf, sup);
			// Ottimizzazione: ordina prima la partizione più piccola
			    if ((pos-inf) < (sup-pos+1)) {
			    	quicksort(attribute, inf, pos-1); 
				    quicksort(attribute, pos+1,sup);
			    }
			    else{
				    quicksort(attribute, pos+1, sup); 
				    quicksort(attribute, inf, pos-1);
			    }
		}
	}
	

	/*swap cambio esempio i con esempi j*/
private void swap(int i, int j) {
    Object temp;
    // Il ciclo deve includere l'ultima colonna (il target)
    // Usiamo <= getNumberOfExplanatoryAttributes() per includere l'indice explanatorySet.length
        for (int k = 0; k <= getNumberOfExplanatoryAttributes(); k++) {
            temp = data[i][k];
            data[i][k] = data[j][k];
            data[j][k] = temp;
        }
}

// Restituisce il valore di un attributo esplicativo per un esempio specifico.
public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
    return data[exampleIndex][attributeIndex];
}	

//Restituisce l'attributo esplicativo alla posizione indicata.
public Attribute getExplanatoryAttribute(int index) {
    return explanatorySet[index];
	}

// torna il numero di attributi esplicativi 
public int getNumberOfExplanatoryAttributes() {
    return explanatorySet.length;
}

//torna il numero totale di esempi nel dataset 
public int getNumberOfExamples() {
	return numberOfExamples;
}

//Partiziona il vettore rispetto all'elemento x e restiutisce il punto di separazione
private int partition(DiscreteAttribute attribute, int inf, int sup){
	int i = inf;
    int j = sup;
    int med = (inf + sup) / 2;

    // Estrae il valore del pivot
    String x = (String) getExplanatoryValue(med, attribute.getIndex());
    
    // Scambia l'elemento centrale con il primo (mette il pivot in 'inf')
    swap(inf, med);

    while (i < j) {
        // Avanza i finché l'elemento è <= pivot
            while (i <= sup && ((String) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) {
                i++;
            }

        // Retrocede j finché l'elemento è > pivot
             while (j >= inf && ((String) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
                j--;
            }

        // Se i e j non si sono incrociati, scambia gli elementi fuori posto
            if (i < j) {
               swap(i, j);
            }
    }
    
    // Riposiziona il pivot nella sua posizione finale (j)
    swap(inf, j);
    return j;
}

	/*
	 * Calcola la varianza degli esempi nell'intervallo [beginExampleIndex, endExampleIndex].
	 * La varianza è calcolata come la somma degli scarti al quadrato (SSE) rispetto alla media.
	 * Questo è un criterio comune per valutare la purezza di un nodo in un albero di regressione.
	*/

public double getVariance(int beginExampleIndex, int endExampleIndex) {
    double sum = 0;
    // Calcola la media (centroide)
    for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
        sum += getClassValue(i);
    }
    double avg = sum / (endExampleIndex - beginExampleIndex + 1);

    // Calcola lo SSE (somma degli scarti al quadrato)
    double sse = 0;
    for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
        sse += Math.pow(getClassValue(i) - avg, 2);
    }
    return sse;
}

	

	
	


	
	
	
	
	


	
}
