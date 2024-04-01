package com.vdsl.cybermart.Message.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;

    private boolean statusSeen;

    public Chat(String sender, String receiver, String message,boolean statusSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.statusSeen = statusSeen;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", isSeen=" + statusSeen +
                '}';
    }

    public boolean isStatusSeen() {
        return statusSeen;
    }

    public void setStatusSeen(boolean statusSeen) {
        this.statusSeen = statusSeen;
    }
}
