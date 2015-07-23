package com.ddnconsulting.chatserver.dao;

import java.util.Collection;
import java.util.Set;

import com.ddnconsulting.chatserver.session.ConnectedSession;
import com.ddnconsulting.chatserver.session.ConnectedSession.SessionType;

/**
 * Abstraction around management of user sessions.
 *
 * @author Dan Nathanson
 */
public interface ConnectedSessionDao {

    /**
     * Returns all connected session IDs for the specified user. Will return empty set if the user has not
     * sessions connected to this server.
     * @param userId ID of user
     * @return IDs of the users connected sessions
     */
    Set<ConnectedSession> getSessionsForUser(long userId);

    /**
     * Returns the session with the specified ID or null if no session exists for that ID
     * @param sessionId ID of session
     * @return the session or null if no session exists
     */
    ConnectedSession getSession(String sessionId);

    /**
     * Removes the specified session
     * @param sessionId ID of session to remove
     * @return the removes session or null if no session for specified ID
     */
    ConnectedSession removeSession(String sessionId);

    /**
     * Adds a new session.
     * @param userId ID of user for this session
     * @param type type of session
     * @return newly added session.  sessionId and time will be set.
     */
    ConnectedSession addSession(long userId, SessionType type);

    /**
     * Returns all connected sessions.
     */
    Collection<ConnectedSession> getAllSessions();
}
