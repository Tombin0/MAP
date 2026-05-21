import java.io.IOException;
import data.Data;
import data.TrainingDataException;
import tree.RegressionTree;
import tree.UnknownValueException;
import utility.Keyboard;

public class MainTest {

    public static void main(String[] args) {
        int decision = 0;
        
        // Menù di scelta richiesto dall'Esercitazione 5
        do {
            System.out.println("Learn Regression Tree from data [1]");
            System.out.println("Load Regression Tree from archive [2]");
            decision = Keyboard.readInt();
        } while (decision != 1 && decision != 2);

        String trainingfileName = "";
        System.out.println("File name (senza estensione):");
        trainingfileName = Keyboard.readString();

        RegressionTree tree = null;
        
        if (decision == 1) {
            System.out.println("Starting data acquisition phase!");
            Data trainingSet = null;
            try {
                // Caricamento del dataset dal file .dat
                trainingSet = new Data(trainingfileName + ".dat");
            } catch (TrainingDataException e) {
                System.out.println(e);
                return;
            }

            System.out.println("Starting learning phase!");
            tree = new RegressionTree(trainingSet);
            
            try {
                // Serializzazione dell'albero su file dmp
                tree.salva(trainingfileName + ".dmp");
                System.out.println("Albero salvato correttamente in " + trainingfileName + ".dmp");
            } catch (IOException e) {
                System.out.println("Errore nel salvataggio: " + e.toString());
            }
        } else {
            try {
                // Deserializzazione dell'albero dall'archivio dmp
                tree = RegressionTree.carica(trainingfileName + ".dmp");
                System.out.println("Albero caricato correttamente da " + trainingfileName + ".dmp");
            } catch (ClassNotFoundException | IOException e) {
                System.out.print("Errore nel caricamento del file: " + e);
                return;
            }
        }

        // Stampa delle regole decisionali generate o caricate
        tree.printRules();

        char risp = 'y';
        do {
            System.out.println("Starting prediction phase!");
            try {
                // Avvio della fase interattiva di classificazione
                System.out.println("Valore predetto: " + tree.predictClass());
            } catch (UnknownValueException e) {
                System.out.println(e);
            }
            System.out.println("Would you repeat ? (y/n)");
            risp = Keyboard.readChar();

        } while (Character.toUpperCase(risp) == 'Y');
    }
}