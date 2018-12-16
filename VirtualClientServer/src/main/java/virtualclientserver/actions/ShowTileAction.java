package virtualclientserver.actions;

import classes.Coordinate;

public class ShowTileAction extends Action {

    Coordinate coordinate;
    int value;

    public ShowTileAction(Coordinate coordinate, int value) {
        super(ActionType.SHOW_TILE);
        this.coordinate = coordinate;
        this.value = value;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
