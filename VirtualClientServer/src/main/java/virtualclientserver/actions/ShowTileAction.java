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

}
