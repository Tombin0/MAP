import java.io.FileNotFoundException;
import java.io.IOException;

import data.Data;
import data.TrainingDataException;
import tree.RegressionTree;
import tree.UnknownValueException;
import utility.Keyboard;

public class MainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int decision = 0;
		do {
			System.out.println("Learn Regression Tree from data [1]");
			System.out.println("Load Regression Tree from archive [2]");
			decision = Keyboard.readInt();
		} while (!(decision == 1) && !(decision == 2));
		
		String trainingfileName = "";
		// 1. CAMBIATO: Richiesta esplicita del file con estensione
		System.out.println("File name (con estensione):");
		trainingfileName = Keyboard.readString();
		
		RegressionTree tree = null;
		
		if (decision == 1) {
			System.out.println("Starting data acquisition phase!");
			Data trainingSet = null;
			try {
				// Passa direttamente il nome file completo inserito dall'utente (es. provaC.dat)
				trainingSet = new Data(trainingfileName);
			} catch (TrainingDataException e) {
				System.out.println(e);
				return;
			}
		
			System.out.println("Starting learning phase!");
			tree = new RegressionTree(trainingSet);
			
			try {
				// Isola il nome base rimuovendo l'estensione inserita (.dat) per salvare il .dmp cleanly
				String dumpFileName = trainingfileName;
				if (trainingfileName.contains(".")) {
					dumpFileName = trainingfileName.substring(0, trainingfileName.lastIndexOf('.')) + ".dmp";
				} else {
					dumpFileName = trainingfileName + ".dmp";
				}
				tree.salva(dumpFileName);
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		} else {
			try {
				// Opzione [2]: Carica direttamente l'archivio .dmp inserito dall'utente (es. provaC.dmp)
				tree = RegressionTree.carica(trainingfileName);
			} catch (ClassNotFoundException | IOException e) {
				System.out.print(e);
				return;
			}
		}
		
		tree.printRules();
		tree.printTree();
		
		char risp = 'y';
		do {
			System.out.println("Starting prediction phase!");
			try {
				System.out.println(tree.predictClass());
			} catch (UnknownValueException e) {
				System.out.println(e);
			}
			System.out.println("Would you repeat? (y/n)");
			risp = Keyboard.readChar();
			
		} while (Character.toUpperCase(risp) == 'Y');
	}
}