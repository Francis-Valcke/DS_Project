package virtualclientserver.actions;

public class UpdateInfoLabelAction extends Action {

    private String label;

    public UpdateInfoLabelAction(String label) {
        super(ActionType.UPDATE_INFO_LABEL);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
