package com.kuleuven.ds.actions;

public class UpdateInfoLabelAction extends Action {

    private String label;

    public UpdateInfoLabelAction(String label) {
        super(ActionType.UPDATE_INFO_LABEL);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
