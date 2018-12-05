package com.kuleuven.distributedsystems.applicationserver.rest.actions;

import java.io.Serializable;

enum ActionType {
    INITIALISE, SHOW_TILE, HIDE_TILE, START_GAME, UPDATE_INFO_LABEL, UPDATE_PLAYER_INFO, YOUR_TURN, READY_UP, LEAVE_GAME
}

/*
 * An action is something that happens that must be made aware to the mobile client
 * */
public abstract class Action implements Serializable {

    /*
     * An action has a owner, timestamp and an actual action
     * */

    private long timestamp;
    private ActionType type;

    public Action(ActionType type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ActionType getType() {
        return type;
    }
}