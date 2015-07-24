package com.ddnconsulting.chatserver.dao;

import java.util.List;

import com.ddnconsulting.chatserver.model.User;

/**
 * Data Access Object for User.
 *
 * @author Dan Nathanson
 */
public interface UserDao {

    /**
     * Returns user by internal ID.
     *
     * @param userId ID of user
     * @return requested user
     * @throws IllegalArgumentException if no user exists for ID
     */
    User getUser(long userId);

    /**
     * Finds user by email address.  Returns null if no user found with that email address.
     *
     * @param emailAddress email address
     * @return requested user or null if no user found with that email address
     */
    User findUser(String emailAddress);


    /**
     * Adds a new user to the system
     * @param emailAddress email address for new user
     * @param displayName display name of new user
     * @return the new Message object
     */
    User addUser(String emailAddress, String displayName);


    /**
     * Deletes a user from persistent storage
     * @param userId ID of user to delete
     * @return deleted user or null if user not found
     */
    User deleteUser(long userId);

    /**
     * Returns all registered users
     */
    List<User> getUsers();
}
