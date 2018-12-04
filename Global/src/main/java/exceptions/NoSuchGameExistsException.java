package exceptions;

public class NoSuchGameExistsException extends Exception {

    public NoSuchGameExistsException() {
        super("The selected game is unavailable.");
    }
}
