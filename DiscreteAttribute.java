/**
 * Classe che rappresenta un attributo discreto del dataset.
 * Oltre al nome e all'indice, contiene un array di stringhe
 * che rappresentano i valori distinti che l'attributo può assumere.
 */

public class DiscreteAttribute extends Attribute {
    private String values[]; 

    public DiscreteAttribute(String name, int index, String values[]) {
        super(name, index);
        this.values = values;
    }

    public int getNumberOfDistinctValues() { return values.length; } 
    
    public String getValue(int i) { return values[i]; }
    
}
