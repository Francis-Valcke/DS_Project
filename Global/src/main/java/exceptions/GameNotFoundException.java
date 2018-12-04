package exceptions;

public class GameNotFoundException extends Exception {
    public GameNotFoundException() {
        super("Game not found.");
    }

    public GameNotFoundException(int gameId) {
        super("Game with id " + gameId + " not found.");
    }

    public GameNotFoundException(String message) {
        super(message);
    }
}
