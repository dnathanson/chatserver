package com.ddnconsulting.chatserver.dao;

import java.util.List;

import com.ddnconsulting.chatserver.model.User;

/**
 * Data Access Object for Contact.  Contacts are modeled as a connection between two users.  Connections are always
 * bidirectional.
 *
 * @author Dan Nathanson
 */
public interface ContactDao {

    /**
     * Returns all contacts for a user.  If user has no contacts, empty list will be returned.
     *
     * @param userId ID of user
     * @return list of contacts for user
     * @throws IllegalArgumentException if no user exists for ID
     */
    List<User> getContacts(long userId);

    /**
     * Creates a connection between two users. Each user will have the other as a contact.
     *
     * @param userId1 ID of a user
     * @param userId2 ID of another user
     */
    void createConnection(long userId1, long userId2);


    /**
     * Deletes a connection between two users
     * @param userId1 ID of a user
     * @param userId2 ID of another user
     * @return true if connection was deleted
     */
    boolean removeConnection(long userId1, long userId2);

    /**
     * Removes all connections in which user participates
     * @param userId ID of user
     */
    void removeAllConnections(long userId);
}
