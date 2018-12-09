package com.kuleuven.ds.actions;

public class YourTurnAction extends Action {

    boolean yourTurn;

    public YourTurnAction(boolean yourTurn) {
        super(ActionType.YOUR_TURN);
        this.yourTurn = yourTurn;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }
}
