package exceptions;

public class InvalidMoveException extends Exception {
    public InvalidMoveException() {
        super("That move is not valid.");
    }

    public InvalidMoveException(String message){
        super(message);
    }
}
