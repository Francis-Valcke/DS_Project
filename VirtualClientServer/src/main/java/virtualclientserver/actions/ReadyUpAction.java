package virtualclientserver.actions;

public class ReadyUpAction extends Action {

    boolean ready;

    public ReadyUpAction(boolean ready) {
        super(ActionType.READY_UP);
        this.ready = ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }
}
