package com.ddnconsulting.chatserver.model;


import java.io.Serializable;

/**
 * Represents a single message in a Conversation.  A message has a sender, receiver, contents and timestamp;
 *
 * @author Dan Nathanson
 */
public class Message implements Serializable {
    // ID of this message
    private long id;

    // The contents of the message (what the user typed).
    private String contents;

    // ID of user who sent message
    private long senderId;

    // ID of user who sent message
    private long receiverId;

    // Time when message was sent
    private long timestamp;

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Generated  method.
     *
     * @noinspection ALL
     */
    @Override public String toString() {
        return "Message{" +
               "contents='" + contents + '\'' +
               ", id=" + id +
               ", senderId=" + senderId +
               ", receiverId=" + receiverId +
               ", timestamp=" + timestamp +
               '}';
    }
}
