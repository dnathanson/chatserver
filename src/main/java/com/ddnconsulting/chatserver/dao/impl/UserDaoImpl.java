package com.ddnconsulting.chatserver.dao.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.ddnconsulting.chatserver.dao.ContactDao;
import com.ddnconsulting.chatserver.dao.UserDao;
import com.ddnconsulting.chatserver.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * This is an in memory implementation using maps.
 *
 * TODO: replace with JDBC implementation using Spring JPA or Hibernate
 *
 * @author Dan Nathanson
 */
@Component
public class UserDaoImpl implements UserDao {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOG = LoggerFactory.getLogger(UserDaoImpl.class);

    @Autowired
    private ContactDao contactDao;
    private Map<Long, User> users = new HashMap<>();
    private Map<String, User> usersByEmail = new HashMap<>();
    private static final AtomicInteger userIds = new AtomicInteger(0);

    /**
     * Returns user by internal ID.
     *
     * @param userId ID of user
     * @return requested user
     *
     * @throws IllegalArgumentException if no user exists for ID
     */
    @Override
    public synchronized User getUser(long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("No user exists with ID [" + userId + "]");
        }
        return new User(user);
    }

    /**
     * Finds user by email address.  Returns null if no user found with that email address.
     *
     * @param emailAddress email address
     * @return requested user or null if no user found with that email address
     */
    @Override
    public synchronized User findUser(String emailAddress) {
        User user = usersByEmail.get(emailAddress);
        if (user == null) {
            return null;
        }
        return new User(user);
    }

    /**
     * Adds a new user to the system
     *
     * @param emailAddress email address for new user
     * @param name display name of new user
     * @return the new Message object
     */
    @Override
    public synchronized User addUser(String emailAddress, String name) {
        if (findUser(emailAddress) != null) {
            throw new IllegalArgumentException("User already exists with email address [" + emailAddress + "]");
        }

        User newUser = new User();
        newUser.setId(userIds.incrementAndGet());
        newUser.setEmailAddress(emailAddress);
        newUser.setName(name);

        users.put(newUser.getId(), newUser);
        usersByEmail.put(emailAddress, newUser);

        return new User(newUser);
    }

    /**
     * Deletes a user from persistent storage
     *
     * @param userId ID of user to delete
     * @return deleted user or null if user not found
     */
    @Override
    public synchronized User deleteUser(long userId) {
        User user = users.remove(userId);
        if (user != null) {
            usersByEmail.remove(user.getEmailAddress());
        }

        contactDao.removeAllConnections(userId);

        return user;
    }
}
