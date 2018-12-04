package exceptions;

public class NotYourTurnException extends Exception {

    public NotYourTurnException() {
        super("Not your turn.");
    }
}
