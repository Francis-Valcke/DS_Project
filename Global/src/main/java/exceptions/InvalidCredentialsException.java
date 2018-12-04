package exceptions;

public class InvalidCredentialsException extends Exception{
    public InvalidCredentialsException() {
        super("Wrong credentials.");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
