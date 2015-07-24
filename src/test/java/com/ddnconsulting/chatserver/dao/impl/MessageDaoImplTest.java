package com.ddnconsulting.chatserver.dao.impl;

import static org.junit.Assert.*;

import java.util.List;

import com.ddnconsulting.chatserver.model.Message;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test class for MessagesDaoImpl
 */
public class MessageDaoImplTest {

    MessageDaoImpl messageDao;

    @Before
    public void setUp() {
        messageDao = new MessageDaoImpl();
    }

    /**
     * Test all methods here since they are related.  You can't test fetching messages without adding messages and
     * you can't verify adding messages without being able to fetch them to see that they are added.
     */
    @Test
    public void testMessages() throws Exception {
        // No messages added
        List<Message> messagesSince = messageDao.getMessagesSince(1, 0);
        List<Message> messagesBetweenUsers = messageDao.getMessages(1, 2);

        assertEquals("Num message for user", 0, messagesSince.size());
        assertEquals("Num message between users", 0, messagesBetweenUsers.size());

        // add a message 1 -> 2
        Message message1 = messageDao.addMessage(1, 2, "message-1");
        assertEquals("message ID", 1, message1.getId());
        assertEquals("sender", 1, message1.getSenderId());
        assertEquals("receiver", 2, message1.getReceiverId());
        assertEquals("contents", "message-1", message1.getContents());
        assertTrue("timestamp", message1.getTimestamp() > 0);

        long checkpoint = System.currentTimeMillis();
        Thread.sleep(1);

        // add a message 2 -> 1
        Message message2 = messageDao.addMessage(2, 1, "message-2");
        assertEquals("message ID", 2, message2.getId());
        assertEquals("sender", 2, message2.getSenderId());
        assertEquals("receiver", 1, message2.getReceiverId());
        assertEquals("contents", "message-2", message2.getContents());
        assertTrue("timestamp", message2.getTimestamp() > checkpoint);

        // add a message 1 -> 3
        Message message3 = messageDao.addMessage(1, 3, "message-3");
        assertEquals("message ID", 3, message3.getId());
        assertEquals("sender", 1, message3.getSenderId());
        assertEquals("receiver", 3, message3.getReceiverId());
        assertEquals("contents", "message-3", message3.getContents());
        assertTrue("timestamp", message3.getTimestamp() > checkpoint);

        // add another message 1 -> 2
        Message message4 = messageDao.addMessage(1, 2, "message-4");
        assertEquals("message ID", 4, message4.getId());
        assertEquals("sender", 1, message4.getSenderId());
        assertEquals("receiver", 2, message4.getReceiverId());
        assertEquals("contents", "message-4", message4.getContents());
        assertTrue("timestamp", message4.getTimestamp() > 0);

        // Get all messages for user 1 after checkpoint
        messagesSince = messageDao.getMessagesSince(1, checkpoint);
        assertEquals("Num message for user", 3, messagesSince.size());
        assertEquals("first message", message4, messagesSince.get(0));
        assertEquals("second message", message3, messagesSince.get(1));
        assertEquals("third message", message2, messagesSince.get(2));

        // Get all messages for user 2 after checkpoint
        messagesSince = messageDao.getMessagesSince(2, checkpoint);
        assertEquals("Num message for user", 2, messagesSince.size());
        assertEquals("first message", message4, messagesSince.get(0));
        assertEquals("second message", message2, messagesSince.get(1));


        // Get messages between user 1 <--> 2
        messagesBetweenUsers = messageDao.getMessages(1, 2);
        assertEquals("Num message between users", 3, messagesBetweenUsers.size());
        assertEquals("first message", message4, messagesBetweenUsers.get(0));
        assertEquals("second message", message2, messagesBetweenUsers.get(1));
        assertEquals("third message", message1, messagesBetweenUsers.get(2));

        // Get messages between user 2 <--> 1
        messagesBetweenUsers = messageDao.getMessages(2, 1);
        assertEquals("Num message between users", 3, messagesBetweenUsers.size());
        assertEquals("first message", message4, messagesBetweenUsers.get(0));
        assertEquals("second message", message2, messagesBetweenUsers.get(1));
        assertEquals("third message", message1, messagesBetweenUsers.get(2));
    }

}