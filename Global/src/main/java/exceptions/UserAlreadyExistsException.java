package exceptions;

public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException() {
        super("User with that username already exists.");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}

