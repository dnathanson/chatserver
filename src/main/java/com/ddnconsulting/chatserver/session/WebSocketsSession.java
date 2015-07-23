package com.ddnconsulting.chatserver.session;

import com.ddnconsulting.chatserver.model.Message;
import com.ddnconsulting.chatserver.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents a connected session that uses Web Sockets for bidirectional communication between client/server
 *
 * @author Dan Nathanson
 */
public class WebSocketsSession extends AbsractSession {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketsSession.class);

    @Override
    public boolean pushMessage(Message message) {
        // TODO: implement
        return true;
    }

    @Override
    public boolean pushUser(User user) {
        // TODO: implement
        return true;
    }
}
