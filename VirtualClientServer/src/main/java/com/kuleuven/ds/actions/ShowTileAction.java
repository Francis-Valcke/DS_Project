package com.kuleuven.ds.actions;

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

    public int getValue() {
        return value;
    }
}
