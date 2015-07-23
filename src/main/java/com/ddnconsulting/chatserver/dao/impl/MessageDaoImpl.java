package com.ddnconsulting.chatserver.dao.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.ddnconsulting.chatserver.dao.MessageDao;
import com.ddnconsulting.chatserver.model.Message;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * This is a quick and dirty in memory implementation using maps. More of a stub really.
 *
 * TODO: replace with JDBC implementation using Spring JPA or Hibernate
 *
 * @author Dan Nathanson
 */
@Component
public class MessageDaoImpl implements MessageDao {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOG = LoggerFactory.getLogger(MessageDaoImpl.class);

    // Maps user to all messages in which they took part (regardless of other user)
    private ListMultimap<Long, Message> userMessages = ArrayListMultimap.create();

    // Maps user/user pairs to the list of messages sent between them
    private ListMultimap<UserPair, Message> messages = ArrayListMultimap.create();

    private static final AtomicInteger messageId = new AtomicInteger(0);

    @Override
    public synchronized List<Message> getMessages(long userId1, long userId2) {
        List<Message> allMessages = messages.get(new UserPair(userId1, userId2));
        return Lists.reverse(allMessages);
    }

    @Override
    public synchronized Message addMessage(long senderId, long receiverId, String contents) {
        Message newMessage = new Message();
        newMessage.setId(messageId.incrementAndGet());
        newMessage.setSenderId(senderId);
        newMessage.setReceiverId(receiverId);
        newMessage.setContents(contents);
        newMessage.setTimestamp(System.currentTimeMillis());

        userMessages.put(senderId, newMessage);
        userMessages.put(receiverId, newMessage);
        messages.put(new UserPair(senderId, receiverId), newMessage);

        return newMessage;
    }

    @Override
    public synchronized List<Message> getMessagesSince(long userId, final long since) {
        List<Message> messagesSince = userMessages.get(userId);
        return Lists.newArrayList(Collections2.filter(messagesSince, new Predicate<Message>() {
            @Override
            public boolean apply(Message message) {
                return message.getTimestamp() > since;
            }
        }));
    }


}
