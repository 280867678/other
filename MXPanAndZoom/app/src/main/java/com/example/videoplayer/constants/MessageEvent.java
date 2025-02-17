package com.example.videoplayer.constants;



public class MessageEvent {
    private String type;
    private String message;

    public MessageEvent(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public MessageEvent(String type) {
        this(type, "");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

