package exceptions;

public class ThemeNotLargeEnoughException extends Exception {

    public ThemeNotLargeEnoughException() {
        super("Theme is does not contain enough pictures for this boardsize");
    }
}
