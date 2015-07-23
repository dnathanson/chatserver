package com.ddnconsulting.chatserver.session;

import com.ddnconsulting.chatserver.model.Message;
import com.ddnconsulting.chatserver.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents a connected session that simply logs received message to the console.
 *
 * @author Dan Nathanson
 */
public class ConsoleLoggingSession extends AbsractSession {
    private static final Logger LOG = LoggerFactory.getLogger(ConsoleLoggingSession.class);

    @Override
    public boolean pushMessage(Message message) {
        LOG.info("Session [" + getId() + "] " +
                 "pushed Message [" + message.getContents() + "] " +
                 "sent by user [" + message.getSenderId() + "] " +
                 "to user [" + message.getReceiverId() + "]");
        return true;
    }

    @Override
    public boolean pushUser(User user) {
        LOG.info("Session [" + getId() + "] " +
                 "pushed User [" + user.getEmailAddress() + "] " +
                 "with status [" + user.getStatus() + "]");
        return true;
    }

}
