package com.ddnconsulting.chatserver.controllers;

import java.util.Collection;
import java.util.List;

import com.ddnconsulting.chatserver.async.NewMessageProducer;
import com.ddnconsulting.chatserver.dao.ConnectedSessionDao;
import com.ddnconsulting.chatserver.dao.MessageDao;
import com.ddnconsulting.chatserver.model.Message;
import com.ddnconsulting.chatserver.session.ConnectedSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for endpoints supporting messages.
 *
 * NOTE: All endpoints contain request parameter for session ID.  Normally, there would be some
 * sort of security implemented protecting these endpoints (cookie?) from which the active user
 * would be identified rather than just trusting a query string argument.
 */
@RestController
public class MessagesController
{

    @Autowired
    MessageDao messageDao;

    @Autowired
    ConnectedSessionDao sessionDao;

    @Autowired
    NewMessageProducer messageProducer;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * Adds a new message between two users.  Message is persisted and a new message event is published so any
     * other chat servers which may have sessions for the affected users can push those messages to the connected
     * clients.
     * @param newMessage the message to add
     * @return the new Message
     */
    @RequestMapping(value = "/messages", method = RequestMethod.POST)
    public Message addMessage(@RequestBody Message newMessage, @RequestParam String sessionId)
    {
        // TODO: ensure that receiver is valid for this user (in contacts list)

        // TODO: ensure receiver is online (for now, not supporting sending messages to offline users).

        // Note: persisting the message could be done asynchronously also in the NewMessageReceiver but would
        // require additional code to ensure that the message was not persisted twice when there are more than
        // one server running.  Not that difficult to do, but is probably a premature optimization since persisting
        // single messages should be very fast if the DB schema is done correctly.
        Message message = messageDao.addMessage(newMessage.getSenderId(), newMessage.getReceiverId(),
                                                newMessage.getContents());

        // Publish the message to be handled asynchronously by all chat servers
        messageProducer.publish(message);

        return message;
    }

    /**
     * Returns messages between current user and another user.  Useful when user wants to scroll back through
     * message history.
     *
     * TODO: add support for paging of messages.  should probably support negative indexing since users likely
     * to be scrolling backwards through time.
     *
     * @param otherUserId ID of the conversation
     * @param sessionId ID of current session.
     * @return list of messages between active user and specified user
     */
    @RequestMapping("/users/{otherUserId}/messages")
    public Collection<Message> getMessages(@PathVariable(value = "otherUserId") long otherUserId,
                                           @RequestParam String sessionId)
    {
        ConnectedSession session = sessionDao.getSession(sessionId);
        long currentUserId = session.getUserId();

        return messageDao.getMessages(currentUserId, otherUserId);
    }

    /**
     * Returns all messages that involve the current user, either as sender or receiver) that were created
     * after a specified date.  This endpoint can be used by a polling client or by a client that may have been
     * offline for a while and needs to refresh.  It is up to the client to organize the messages
     * into chat panels by user.
     *
     * TODO: add support for paging of messages.  should probably support negative indexing since users likely
     * to be scrolling backwards through time.
     *
     * @param since timestamp indicating how far back in time to look
     * @param sessionId ID of current session.
     * @return list of messages
     */
    @RequestMapping("/messages")
    public List<Message> getMessagesSince(@RequestParam(required = false, defaultValue = "0") long since,
                                          @RequestParam String sessionId) {
        ConnectedSession session = sessionDao.getSession(sessionId);
        long currentUserId = session.getUserId();

        return messageDao.getMessagesSince(currentUserId, since);
    }
}