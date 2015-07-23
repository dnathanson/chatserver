package com.ddnconsulting.chatserver.model;

/**
 * Models changes to connections between users
 *
 * @author Dan Nathanson
 */
public class ContactChange {
    private long userId1;
    private long userId2;
    private ChangeType type;

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

    public ChangeType getType() {
        return type;
    }

    public void setType(ChangeType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ContactChange{" +
               "userId1=" + userId1 +
               ", userId2=" + userId2 +
               ", type=" + type +
               '}';
    }

    public static enum ChangeType {
        ADDED,
        REMOVED
    }
}
