package com.ddnconsulting.chatserver.async;

import java.io.IOException;
import java.util.Set;

import com.ddnconsulting.chatserver.dao.ConnectedSessionDao;
import com.ddnconsulting.chatserver.model.Message;
import com.ddnconsulting.chatserver.session.ConnectedSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Asynchronously handles new messages.  For each message received, if there is a user session connected to this server
 * who participates in the message as either a sender or received, push (via whatever channel that user session is
 * using) to the client.
 *
 * @author Dan Nathanson
 */
public class NewMessageConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(NewMessageConsumer.class);
    private ObjectMapper objectMapper;
    private ConnectedSessionDao sessionDao;

    @Autowired
    public NewMessageConsumer(ObjectMapper objectMapper, ConnectedSessionDao sessionDao) {
        this.objectMapper = objectMapper;
        this.sessionDao = sessionDao;
    }

    /**
     * Handle new messages that have been sent.
     *
     * Note: Implementation is not ideal. Should be able to declare method with 'Message' as argument and
     * Jackson should automatically deserialize String into Message object, but I can't get it working at the
     * moment and it's not really what this exercise is about.
     *
     * @param message JSON representation of Message object (yuck)
     */
    public void handleMessage(String message)
    {
        try {
            Message receivedMessage = objectMapper.readValue(message, Message.class);
            LOG.info("Received message: " + receivedMessage);

            long senderId = receivedMessage.getSenderId();
            long receiverId = receivedMessage.getReceiverId();

            pushMessageToUser(receivedMessage, senderId);
            pushMessageToUser(receivedMessage, receiverId);

            // TODO:
            // Update last activity time for sender in Redis
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to deserialize string [" + message + "] in Message", e);
        }
    }

    /**
     * Push new message to all user's session
     * @param message message to push
     * @param userId ID of user
     */
    private void pushMessageToUser(Message message, long userId) {
        Set<ConnectedSession> senderSessions = sessionDao.getSessionsForUser(userId);
        for (ConnectedSession senderSession : senderSessions) {
            senderSession.pushMessage(message);
        }
    }
}
