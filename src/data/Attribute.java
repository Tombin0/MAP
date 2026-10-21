     /**
	 * Classe astratta che rappresenta un attributo generico del dataset.
	 * Ogni attributo ha un nome e un indice (posizione nella matrice).
	 */

	package data;
	
	public abstract class Attribute implements java.io.Serializable {
    	protected String name; 
    	protected int index;  

    	// Crea un attributo con nome e indice.
    	public Attribute(String name, int index) { 
        	this.name = name;
        	this.index = index;
    	}

    	// Restituisce il nome dell'attributo.
    	public String getName() { return name; } 

    	// Restituisce l'indice dell'attributo.
    	public int getIndex() { return index; }

    	// Converte l'attributo in stringa.
    	@Override
    	public String toString() { return name; }
	}
