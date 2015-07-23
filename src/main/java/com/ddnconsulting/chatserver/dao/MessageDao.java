package com.ddnconsulting.chatserver.dao;

import java.util.Collection;
import java.util.List;

import com.ddnconsulting.chatserver.model.Message;

/**
 * Data Access Object for Messages.
 *
 * @author Dan Nathanson
 */
public interface MessageDao {

    /**
     * Returns all messages between two users.  Returns in reverse order sorted by time.
     *
     * TODO: add paging support since anyone under the age of 20 is likely to send too many messages to be able
     * to support returning all messages in a single request.
     *
     * @param userId1 ID of a user
     * @param userId2 ID of the other user
     * @return full list of messages sent between the specified users
     */
    Collection<Message> getMessages(long userId1, long userId2);

    /**
     * Adds a new message to a conversation.
     * @param senderId ID of user who sent the message
     * @param receiverId ID of user who received the message
     * @param contents contents of the message
     * @return the new Message object
     */
    Message addMessage(long senderId, long receiverId, String contents);


    /**
     * Returns messages where specified user is a participant that were sent after a certain time. Returns messages in
     * reverse order by time (latest first)
     * @param userId ID of user
     * @param since time before which messages are ignored
     * @return list of messages for user
     */
    List<Message> getMessagesSince(long userId, long since);
}
