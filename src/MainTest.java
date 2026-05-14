import utility.Keyboard;
import data.Data;
import data.TrainingDataException;
import tree.RegressionTree;
import tree.UnknownValueException;

class MainTest {

    public static void main(String[] args) {
        
        // 1. Chiediamo all'utente quale file caricare
        System.out.println("Training set: ");
        String fileName = Keyboard.readString();
        
        // Il primo grande blocco TRY serve per la fase di caricamento e apprendimento
        try {
            // Fase di acquisizione dati
            System.out.println("Starting data acquisition phase!");
            Data trainingSet = new Data(fileName); 
            
            // Fase di apprendimento (creazione dell'albero)
            System.out.println("Starting learning phase!");
            RegressionTree tree = new RegressionTree(trainingSet);
            
            // Stampa delle regole e della struttura dell'albero
            tree.printRules();
            tree.printTree();
            
            // 2. FASE DI PREDIZIONE (Ciclo interattivo)
            char repeat = 'y';
            do {
                System.out.println("Starting prediction phase!");
                
                // Secondo blocco TRY (interno): serve per gestire errori durante la predizione
                try {
                    // Chiamiamo il metodo che fa le domande all'utente
                    tree.predictClass();
                    
                } catch (UnknownValueException e) {
                        // Se l'utente sbaglia a rispondere alle domande dell'albero
                        System.out.println("tree.UnknownValueException: " + e.getMessage());
                    }

                // Chiediamo se l'utente vuole testare un altro esempio
                System.out.println("Would you repeat? (y/n)");
                repeat = Keyboard.readChar();
                
            }
            while (Character.toLowerCase(repeat) == 'y');
        
        } catch (TrainingDataException e) {
                // Se il file non esiste o ha un formato errato, arriviamo qui
                System.out.println("data.TrainingDataException: " + e.getMessage());
            }
    }
}