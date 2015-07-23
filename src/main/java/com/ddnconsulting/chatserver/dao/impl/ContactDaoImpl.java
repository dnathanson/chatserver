package com.ddnconsulting.chatserver.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ddnconsulting.chatserver.dao.ContactDao;
import com.ddnconsulting.chatserver.dao.UserDao;
import com.ddnconsulting.chatserver.model.User;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * This is a quick and dirty in memory implementation using maps. More of a stub really.
 *
 * TODO: replace with JDBC implementation using Spring JPA or Hibernate
 *
 * @author Dan Nathanson
 */
@Component
public class ContactDaoImpl implements ContactDao {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOG = LoggerFactory.getLogger(ContactDaoImpl.class);

    @Autowired
    private UserDao userDao;

    // Set of all user-to-user connections
    private Set<UserPair> connections = Collections.newSetFromMap(new ConcurrentHashMap<UserPair, Boolean>());

    // Maps user to all users to which they have a connection (by ID)
    private ListMultimap<Long, Long> userConnections = ArrayListMultimap.create();


    public synchronized List<User> getContacts(long userId) {
        List<Long> connectionIds = userConnections.get(userId);
        List<User> connections = Lists.transform(connectionIds, new Function<Long, User>() {
            @Override
            public User apply(Long userId) {
                return new User(userDao.getUser(userId));
            }
        });
        return connections;
    }

    /**
     * Creates a connection between two users. Each user will have the other as a contact.
     *
     * @param userId1 ID of a user
     * @param userId2 ID of another user
     */
    @Override
    public synchronized void createConnection(long userId1, long userId2) {
        UserPair userPair = new UserPair(userId1, userId2);
        if (!connections.contains(userPair)) {
            connections.add(userPair);
            userConnections.put(userId1, userId2);
            userConnections.put(userId2, userId1);
        }
    }

    /**
     * Deletes a connection between two users
     *
     * @param userId1 ID of a user
     * @param userId2 ID of another user
     * @return true if connection was deleted
     */
    @Override
    public synchronized boolean removeConnection(long userId1, long userId2) {
        boolean found = connections.remove(new UserPair(userId1, userId2));
        if (found) {
            userConnections.remove(userId1, userId2);
            userConnections.remove(userId2, userId1);
        }
        return found;
    }

    /**
     * Removes all connections in which user participates
     *
     * @param userId ID of user
     */
    @Override
    public synchronized void removeAllConnections(long userId) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns true iff users are connected
     *
     * @param userId1 ID of a user
     * @param userId2 ID of another user
     */
    @Override
    public boolean isConnected(long userId1, long userId2) {
        return connections.contains(new UserPair(userId1, userId2));
    }
}
