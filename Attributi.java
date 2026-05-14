public abstract class Totali {
    protected String name; 
    protected int index;  

    public Totali(String name, int index) { 
        this.name = name;
        this.index = index;
    }

    public String getName() { return name; } 
    public int getIndex() { return index; }

    @Override
    public String toString() { return name; }
}

public class ContinuousAttribute extends Totali {
    public ContinuousAttribute(String name, int index) {
        super(name, index);
    }
}


public class DiscreteAttribute extends Totali {
    private String values[]; 

    public DiscreteAttribute(String name, int index, String values[]) {
        super(name, index); 
        this.values = values;
    }

    public int getNumberOfDistinctValues() { return values.length; } //
    public String getValue(int i) { return values[i]; }
}
