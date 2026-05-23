package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Scanner;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

public class Data implements Serializable {
    private static final long serialVersionUID = 1L;

    private Object data[][];
    private int numberOfExamples;
    private List<Attribute> explanatorySet;
    private ContinuousAttribute classAttribute;

    public Data(String fileName) throws TrainingDataException {
        File inFile = new File(inFileLocation(fileName));
        Scanner sc;

        try {
            sc = new Scanner(inFile);
        } catch (FileNotFoundException e) {
            throw new TrainingDataException("Impossibile trovare il file specificato: " + fileName);
        }

        if (!sc.hasNextLine()) {
            sc.close();
            throw new TrainingDataException("Il file e' vuoto!");
        }

        String line = sc.nextLine();
        if (!line.contains("@schema")) {
            sc.close();
            throw new TrainingDataException("Errore nello schema: manca l'intestazione @schema");
        }

        explanatorySet = new LinkedList<Attribute>();
        short iAttribute = 0;

        line = sc.nextLine();
        while (!line.contains("@data")) {
            String s[] = line.split(" ");
            if (s[0].equals("@desc")) {
                if (s.length > 2) {
                    String discreteValues[] = s[2].split(",");
                    Set<String> distinctValues = new TreeSet<>();
                    for (String val : discreteValues) {
                        distinctValues.add(val);
                    }
                    explanatorySet.add(new DiscreteAttribute(s[1], iAttribute, distinctValues));
                } else {
                    explanatorySet.add(new ContinuousAttribute(s[1], iAttribute));
                }
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
            String s[] = line.split(",");

            for (short jColumn = 0; jColumn < s.length - 1; jColumn++) {
                Attribute attr = explanatorySet.get(jColumn);
                if (attr instanceof DiscreteAttribute) {
                    data[iRow][jColumn] = s[jColumn].trim();
                } else {
                    data[iRow][jColumn] = Double.parseDouble(s[jColumn].trim());
                }
            }
            data[iRow][s.length - 1] = Double.parseDouble(s[s.length - 1].trim());
            iRow++;
        }
        sc.close();
    }

    private String inFileLocation(String name) {
        if (!name.endsWith(".dat")) {
            return name + ".dat";
        }
        return name;
    }

    public ContinuousAttribute getClassAttribute() {
        return classAttribute;
    }

    public Double getClassValue(int exampleIndex) {
        return (Double) data[exampleIndex][explanatorySet.size()];
    }

    public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {
        quicksort(attribute, beginExampleIndex, endExampleIndex);
    }

    private void swap(int i, int j) {
        Object temp;
        for (int k = 0; k <= getNumberOfExplanatoryAttributes(); k++) {
            temp = data[i][k];
            data[i][k] = data[j][k];
            data[j][k] = temp;
        }
    }

    private int partition(DiscreteAttribute attribute, int inf, int sup) {
        int i = inf, j = sup;
        int med = (inf + sup) / 2;
        String x = (String) getExplanatoryValue(med, attribute.getIndex());
        swap(inf, med);

        while (true) {
            while (i <= sup && ((String) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) {
                i++;
            }
            while (((String) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
                j--;
            }
            if (i < j) {
                swap(i, j);
            } else break;
        }
        swap(inf, j);
        return j;
    }

    private int partition(ContinuousAttribute attribute, int inf, int sup) {
        int i = inf, j = sup;
        int med = (inf + sup) / 2;
        Double x = (Double) getExplanatoryValue(med, attribute.getIndex());
        swap(inf, med);

        while (true) {
            while (i <= sup && ((Double) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) {
                i++;
            }
            while (((Double) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
                j--;
            }
            if (i < j) {
                swap(i, j);
            } else break;
        }
        swap(inf, j);
        return j;
    }

    private void quicksort(Attribute attribute, int inf, int sup) {
        if (sup >= inf) {
            int pos;
            if (attribute instanceof DiscreteAttribute)
                pos = partition((DiscreteAttribute) attribute, inf, sup);
            else
                pos = partition((ContinuousAttribute) attribute, inf, sup);

            if ((pos - inf) < (sup - pos + 1)) {
                quicksort(attribute, inf, pos - 1);
                quicksort(attribute, pos + 1, sup);
            } else {
                quicksort(attribute, pos + 1, sup);
                quicksort(attribute, inf, pos - 1);
            }
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