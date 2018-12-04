package exceptions;

public class AlreadyPresentException extends Exception {
    public AlreadyPresentException() {
        super("User is already present.");
    }
}
