/* la classe UnknownValueException rappresenta un'eccezione lanciata quando un valore sconosciuto viene 
* trovato durante l'elaborazione dell'albero decisionale 
*/

package tree;
import data.*;

public class UnknownValueException extends Exception {
    public UnknownValueException(String message) {
        super(message);
    }
}