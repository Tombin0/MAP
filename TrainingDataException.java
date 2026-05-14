/*Eccezione personalizzata per gestire errori durante l'acquisizione 
 * del Training Set (es. file inesistente, formato errato, ecc.)
 */


public class TrainingDataException extends Exception {

    // Costruttore senza messaggio
    public TrainingDataException() {
        super("Errore nel file di training.");
    }

    // Costruttore che accetta un messaggio personalizzato
    public TrainingDataException(String message) {
        super(message);
    }