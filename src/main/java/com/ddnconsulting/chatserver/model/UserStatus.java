package com.ddnconsulting.chatserver.model;

/**
 * Represents status (presence) of a user
 *
 * @author Dan Nathanson
 */
public enum UserStatus {
    ACTIVE,
    IDLE,
    AWAY,
    OFFLINE,
    DELETE       // Used to remove user from client contact list
}
