package com.ddnconsulting.chatserver.async;

import java.io.IOException;
import java.util.Set;

import com.ddnconsulting.chatserver.dao.ConnectedSessionDao;
import com.ddnconsulting.chatserver.dao.UserDao;
import com.ddnconsulting.chatserver.model.ContactChange;
import com.ddnconsulting.chatserver.model.ContactChange.ChangeType;
import com.ddnconsulting.chatserver.model.User;
import com.ddnconsulting.chatserver.model.UserStatus;
import com.ddnconsulting.chatserver.session.ConnectedSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Asynchronously handles changes in user contacts.  For each message received, for both affected users, pushes
 * change to each connected client for both users.
 *
 * @author Dan Nathanson
 */
public class ContactConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(ContactConsumer.class);
    private UserDao userDao;
    private ObjectMapper objectMapper;
    private ConnectedSessionDao sessionDao;

    @Autowired
    public ContactConsumer(ObjectMapper objectMapper,
                           ConnectedSessionDao sessionDao,
                           UserDao userDao) {
        this.objectMapper = objectMapper;
        this.sessionDao = sessionDao;
        this.userDao = userDao;
    }

    /**
     * Handle changes to contact lists.
     *
     * Note: Implementation is not ideal. Should be able to declare method with 'ContactChange' as argument and
     * Jackson should automatically deserialize String into Message object, but I can't get it working at the
     * moment and it's not really what this exercise is about.
     *
     * @param message JSON representation of ContactChange object (yuck)
     */
    public void handleMessage(String message)
    {
        try {
            ContactChange contactChange = objectMapper.readValue(message, ContactChange.class);
            LOG.info("Received message: " + contactChange);

            long userId1 = contactChange.getUserId1();
            long userId2 = contactChange.getUserId2();


            pushUser(contactChange.getType(), userId1, userId1);
            pushUser(contactChange.getType(), userId2, userId1);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to deserialize string [" + message + "] in Message", e);
        }
    }

    /**
     * Push a User object to clients. If contact is being deleted, user status will be set to DELETE, otherwise
     * user status comes from presence is Redis.
     *
     * @param changeType message to push
     * @param userId ID of user which is having change of contact
     * @param contactId ID of new (or deleted) contact
     */
    private void pushUser(ChangeType changeType, long userId, long contactId) {
        User user = userDao.getUser(userId);
        User contact = userDao.getUser(contactId);


        if (changeType == ChangeType.REMOVED) {
            contact.setStatus(UserStatus.DELETE);
        }
        else {
            // TODO: set contact status based on presence in Redis
            contact.setStatus(UserStatus.ACTIVE);
        }

        Set<ConnectedSession> senderSessions = sessionDao.getSessionsForUser(userId);
        for (ConnectedSession senderSession : senderSessions) {
            senderSession.pushUser(contact);
        }
    }
}
