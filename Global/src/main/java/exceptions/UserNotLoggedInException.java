package exceptions;

public class UserNotLoggedInException extends Exception {

    public UserNotLoggedInException() {
        super("User not logged in.");
    }

    public UserNotLoggedInException(String message) {
        super(message);
    }
}
