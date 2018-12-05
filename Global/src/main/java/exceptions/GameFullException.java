package exceptions;

public class GameFullException extends Exception {

    public GameFullException() {
        super("Game already full.");
    }

    public GameFullException(int gameId) {
        super("Game " + gameId + " is currently full.");
    }
}
