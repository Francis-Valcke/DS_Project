package exceptions;

public class AlreadyPresentException extends Exception {

    public AlreadyPresentException() {
        super("User is already present.");
    }

    public AlreadyPresentException(String message) {
        super(message);
    }
}
