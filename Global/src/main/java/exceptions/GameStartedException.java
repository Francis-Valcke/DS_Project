package exceptions;

public class GameStartedException extends Exception {
    public GameStartedException() {
        super("Game already in progress.");
    }

    public GameStartedException(int gameId) {
        super("Game " + gameId + " already in progress.");
    }
}
