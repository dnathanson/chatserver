package com.ddnconsulting.chatserver.model;

/**
 * Represents a user of the chat application.
 *
 * This model is used both for persistence to DB and for user presence in Redis.  Only name, emailAddress and
 * ID are stored in the DB.
 *
 * TODO: this should probably be split into a User object and a UserPresence object.
 *
 * @author Dan Nathanson
 */
public class User {
    // Internal ID
    private long id;

    // Display name
    private String name;

    // Email address & login ID
    private String emailAddress;

    // Status (presence)
    private UserStatus status;

    // Time user last interacted with server
    private long lastActiveTime;

    public User(User user) {
        this.name = user.getName();
        this.id = user.getId();
        this.emailAddress = user.getEmailAddress();
    }

    public User() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }
}
