package com.ddnconsulting.chatserver.session;

import com.ddnconsulting.chatserver.model.Message;
import com.ddnconsulting.chatserver.model.User;
import com.ddnconsulting.chatserver.model.UserStatus;

/**
 * Represents a user session connected to this server.  Encapsulates connection/channel operations like "push"
 *
 * @author Dan Nathanson
 */
public interface ConnectedSession {

    String getId();

    void setId(String id);

    SessionType getType();

    void setType(SessionType type);

    long getUserId();

    void setUserId(long userId);

    /**
     * Returns status for this session.
     */
    UserStatus getStatus();

    /**
     * Sets status for this session.
     */
    void setStatus(UserStatus status);

    /**
     * Returns last time user was active on this connection
     */
    long getLastActivityTime();

    /**
     * Sets last time user was active on this connection
     */
    void setLastActivityTime(long lastActivityTime);

    /**
     * Push new message to client.
     *
     * @param message the new message
     * @return true if message successfully pushed
     */
    boolean pushMessage(Message message);

    /**
     * Push user to client.  Can be used to add new users to address book or to change presence/status of existing
     * users.
     *
     * @param user new or changed user
     * @return true if message successfully pushed
     */
    boolean pushUser(User user);


    public static enum SessionType {
        POLLING,
        SOCKET_IO,
        WEB_SOCKETS
    }
}
