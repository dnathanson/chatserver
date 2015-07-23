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
        return false;
    }

    @Override
    public boolean pushUser(User user) {
        return false;
    }

}
