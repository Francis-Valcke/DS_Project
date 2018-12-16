package virtualclientserver.actions;

import classes.Coordinate;

public class HideTileAction extends Action {

    private Coordinate coordinate;
    private int delay;

    public HideTileAction(Coordinate coordinate, int delay) {
        super(ActionType.HIDE_TILE);
        this.coordinate = coordinate;
        this.delay = delay;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public int getDelay() {
        return delay;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
