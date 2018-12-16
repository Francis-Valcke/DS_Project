package classes;

import java.io.Serializable;

public class ResponseMessage implements Serializable {

    private ResponseType status;
    private String message;
    private Object payload;

    public ResponseMessage(ResponseType status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseMessage(ResponseType status, String message, Object payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public ResponseType getStatus() {
        return status;
    }

    public void setStatus(ResponseType status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
