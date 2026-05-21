/*Eccezione personalizzata per gestire errori durante l'acquisizione 
 * del Training Set (es. file inesistente, formato errato, ecc.)
 */

package data;

public class TrainingDataException extends Exception {

    public TrainingDataException() {
        super("Errore nel file di training.");
    }

    public TrainingDataException(String message) {
        super(message);
    }
}