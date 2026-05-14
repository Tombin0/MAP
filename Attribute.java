     /**
	 * Classe astratta che rappresenta un attributo generico del dataset.
	 * Ogni attributo ha un nome e un indice (posizione nella matrice).
	 */
	
	public abstract class Attribute {
    	protected String name; 
    	protected int index;  

    	public Attribute(String name, int index) { 
        	this.name = name;
        	this.index = index;
    	}

    	public String getName() { return name; } 
    	public int getIndex() { return index; }

    	@Override
    	public String toString() { return name; }
	}
