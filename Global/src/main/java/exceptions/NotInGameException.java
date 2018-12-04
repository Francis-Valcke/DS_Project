package exceptions;

public class NotInGameException extends Exception {

    public NotInGameException() {
        super("User is not in a game.");
    }

}
