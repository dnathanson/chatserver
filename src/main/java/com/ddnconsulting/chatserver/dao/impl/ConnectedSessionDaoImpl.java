package com.ddnconsulting.chatserver.dao.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ddnconsulting.chatserver.dao.ConnectedSessionDao;
import com.ddnconsulting.chatserver.model.UserStatus;
import com.ddnconsulting.chatserver.session.ConnectedSession;
import com.ddnconsulting.chatserver.session.ConnectedSession.SessionType;
import com.ddnconsulting.chatserver.session.ConsoleLoggingSession;
import com.ddnconsulting.chatserver.session.PollingSession;
import com.ddnconsulting.chatserver.session.SocketIoSession;
import com.ddnconsulting.chatserver.session.WebSocketsSession;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Implementation of DAO which uses a couple of ConcurrentMaps to manage users and session.  No data is persisted.
 * <p/>
 * NOTE: considered using Redis hash for this with a compound key (userId:sessionId) but decided that keeping this info
 * locally would be faster, even with the synchronization required in order to keep the two maps consistent.
 *
 * @author Dan Nathanson
 */
@Component
public class ConnectedSessionDaoImpl implements ConnectedSessionDao {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOG = LoggerFactory.getLogger(ConnectedSessionDaoImpl.class);

    // Maps session ID to ConnectedSession
    ConcurrentMap<String, ConnectedSession> connectedSessions = new ConcurrentHashMap<>();

    // Maps user ID to set of connected session IDs
    ConcurrentMap<Long, Set<String>> connectedUsers = new ConcurrentHashMap<>();


    @Override
    public synchronized Set<ConnectedSession> getSessionsForUser(long userId) {
        Set<ConnectedSession> sessions = new HashSet<>();
        Set<String> sessionIds = connectedUsers.get(userId);
        if (sessionIds != null) {
            for (String sessionId : sessionIds) {
                sessions.add(connectedSessions.get(sessionId));
            }
        }
        return sessions;
    }

    @Override
    public synchronized ConnectedSession getSession(String sessionId) {
        return connectedSessions.get(sessionId);
    }

    @Override
    public synchronized ConnectedSession removeSession(String sessionId) {
        ConnectedSession session = connectedSessions.remove(sessionId);
        if (session != null) {
            long userId = session.getUserId();
            Set<String> userSessions = connectedUsers.get(userId);
            if (userSessions != null) {
                userSessions.remove(sessionId);
                if (userSessions.size() == 0) {
                    connectedUsers.remove(userId);
                }
            }
        }
        return session;
    }

    @Override
    public synchronized ConnectedSession addSession(long userId, SessionType type) {

        ConnectedSession newSession;
        switch (type) {
            case CONSOLE_LOGGING:
                newSession = new ConsoleLoggingSession();
                newSession.setType(SessionType.CONSOLE_LOGGING);
                break;
            case POLLING:
                newSession = new PollingSession();
                newSession.setType(SessionType.POLLING);
                break;
            case SOCKET_IO:
                newSession = new SocketIoSession();
                newSession.setType(SessionType.SOCKET_IO);
                break;
            case WEB_SOCKETS:
                newSession = new WebSocketsSession();
                newSession.setType(SessionType.WEB_SOCKETS);
                break;
            default:
                throw new IllegalArgumentException("Unsupported session type [" + type + "]");
        }

        newSession.setId(RandomStringUtils.randomAlphanumeric(32));
        newSession.setStatus(UserStatus.ACTIVE);
        newSession.setUserId(userId);
        newSession.setLastActivityTime(System.currentTimeMillis());

        // Add to connected sessions map
        connectedSessions.put(newSession.getId(), newSession);

        // Add to connected users map
        Set<String> userSessions = connectedUsers.get(userId);
        if (userSessions == null) {
            userSessions = new HashSet<>();
            connectedUsers.put(userId, userSessions);
        }
        userSessions.add(newSession.getId());

        return newSession;
    }

    @Override
    public Collection<ConnectedSession> getAllSessions() {
        return connectedSessions.values();
    }
}
