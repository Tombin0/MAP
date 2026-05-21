package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

public class Data {
    private Object data[][];
    private int numberOfExamples;

    // Sostituito l'array statico con List e LinkedList
    private List<Attribute> explanatorySet;
    private ContinuousAttribute classAttribute;

    public Data(String fileName) throws TrainingDataException {
        File inFile = new File(fileName);
        Scanner sc;

        try {
            sc = new Scanner(inFile);
        } catch (FileNotFoundException e) {
            throw new TrainingDataException("Impossibile trovare il file specificato: " + fileName);
        }

        if (!sc.hasNextLine()) {
            sc.close();
            throw new TrainingDataException("Il file è vuoto!");
        }

        String line = sc.nextLine();
        if (!line.contains("@schema")) {
            sc.close();
            throw new TrainingDataException("Errore nello schema: manca l'intestazione @schema");
        }

        String s[] = line.split(" ");

        // Inizializzazione della LinkedList per gli attributi esplicativi
        explanatorySet = new LinkedList<Attribute>();
        short iAttribute = 0;

        line = sc.nextLine();
        while (!line.contains("@data")) {
            s = line.split(" ");
            if (s[0].equals("@desc")) {
                String discreteValues[] = s[2].split(",");
                // Creazione del Set ordinato richiesto da DiscreteAttribute
                Set<String> distinctValues = new TreeSet<>();
                for (String val : discreteValues) {
                    distinctValues.add(val);
                }
                explanatorySet.add(new DiscreteAttribute(s[1], iAttribute, distinctValues));
            } else if (s[0].equals("@target")) {
                classAttribute = new ContinuousAttribute(s[1], iAttribute);
            }
            iAttribute++;
            line = sc.nextLine();
        }

        numberOfExamples = Integer.parseInt(line.split(" ")[1]);
        data = new Object[numberOfExamples][explanatorySet.size() + 1];
        short iRow = 0;

        while (sc.hasNextLine()) {
            line = sc.nextLine();
            if (line.trim().isEmpty()) continue;
            s = line.split(",");

            for (short jColumn = 0; jColumn < s.length - 1; jColumn++) {
                data[iRow][jColumn] = s[jColumn];
            }

            data[iRow][s.length - 1] = Double.parseDouble(s[s.length - 1].trim());
            iRow++;
        }
        sc.close();
    }

    public ContinuousAttribute getClassAttribute() {
        return classAttribute;
    }

    public Double getClassValue(int exampleIndex) {
        return (Double) data[exampleIndex][explanatorySet.size()];
    }

    public String toString() {
        String value = "";
        for (int i = 0; i < numberOfExamples; i++) {
            for (int j = 0; j < explanatorySet.size(); j++)
                value += data[i][j] + ",";
            value += data[i][explanatorySet.size()] + "\n";
        }
        return value;
    }

    public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {
        quicksort(attribute, beginExampleIndex, endExampleIndex);
    }

    private void quicksort(Attribute attribute, int inf, int sup) {
        if (sup >= inf) {
            int pos;
            pos = partition((DiscreteAttribute) attribute, inf, sup);
            if ((pos - inf) < (sup - pos + 1)) {
                quicksort(attribute, inf, pos - 1);
                quicksort(attribute, pos + 1, sup);
            } else {
                quicksort(attribute, pos + 1, sup);
                quicksort(attribute, inf, pos - 1);
            }
        }
    }

    private void swap(int i, int j) {
        Object temp;
        for (int k = 0; k <= getNumberOfExplanatoryAttributes(); k++) {
            temp = data[i][k];
            data[i][k] = data[j][k];
            data[j][k] = temp;
        }
    }

    public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
        return data[exampleIndex][attributeIndex];
    }

    public Attribute getExplanatoryAttribute(int index) {
        return explanatorySet.get(index);
    }

    public int getNumberOfExplanatoryAttributes() {
        return explanatorySet.size();
    }

    public int getNumberOfExamples() {
        return numberOfExamples;
    }

    private int partition(DiscreteAttribute attribute, int inf, int sup) {
        int i = inf;
        int j = sup;
        int med = (inf + sup) / 2;

        String x = (String) getExplanatoryValue(med, attribute.getIndex());
        swap(inf, med);

        while (i < j) {
            while (i <= sup && ((String) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) {
                i++;
            }
            while (j >= inf && ((String) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
                j--;
            }
            if (i < j) {
                swap(i, j);
            }
        }
        swap(inf, j);
        return j;
    }

    public double getVariance(int beginExampleIndex, int endExampleIndex) {
        double sum = 0;
        for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
            sum += getClassValue(i);
        }
        double avg = sum / (endExampleIndex - beginExampleIndex + 1);

        double sse = 0;
        for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
            sse += Math.pow(getClassValue(i) - avg, 2);
        }
        return sse;
    }
}