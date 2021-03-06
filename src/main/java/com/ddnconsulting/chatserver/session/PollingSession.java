package com.ddnconsulting.chatserver.session;

import com.ddnconsulting.chatserver.model.Message;
import com.ddnconsulting.chatserver.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents a connected session that only polls for changes and doesn't support push notifications.
 *
 * @author Dan Nathanson
 */
public class PollingSession extends AbsractSession {
    private static final Logger LOG = LoggerFactory.getLogger(PollingSession.class);

    @Override
    public boolean pushMessage(Message message) {
        LOG.info("Session [" + getId() + "] " +
                 "received Message [" + message.getContents() + "] " +
                 "sent by user [" + message.getSenderId() + "] " +
                 "to user [" + message.getReceiverId() + "]");
        return true;
    }

    @Override
    public boolean pushUser(User user) {
        LOG.info("Session [" + getId() + "] " +
                 "received User [" + user.getEmailAddress() + "] " +
                 "with status [" + user.getStatus() + "]");
        return true;
    }

}
