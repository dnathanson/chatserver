package com.ddnconsulting.chatserver.model;

/**
 * Represents a conversation between two users.  A conversation is comprised of a series of messages between two users.
 *
 * @author Dan Nathanson
 */
public class Conversation {
    private long id;
    private long userId1;
    private long userId2;
    private long startTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId1() {
        return userId1;
    }

    public void setUserId1(long userId1) {
        this.userId1 = userId1;
    }

    public long getUserId2() {
        return userId2;
    }

    public void setUserId2(long userId2) {
        this.userId2 = userId2;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Generated  method.
     *
     * @noinspection ALL
     */
    @Override
    public String toString() {
        return "Conversation{" +
               "id=" + id +
               ", userId1=" + userId1 +
               ", userId2=" + userId2 +
               ", startTime=" + startTime +
               '}';
    }
}
