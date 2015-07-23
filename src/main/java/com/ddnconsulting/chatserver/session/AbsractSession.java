package com.ddnconsulting.chatserver.session;

import com.ddnconsulting.chatserver.model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Some methods common to all ConnectedSession implementations
 *
 * @author Dan Nathanson
 */
public abstract class AbsractSession implements ConnectedSession {
    private static final Logger LOG = LoggerFactory.getLogger(AbsractSession.class);
    private String id;
    private SessionType type;
    private long userId;
    private UserStatus status;
    private long lastActivityTime;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public SessionType getType() {
        return type;
    }

    @Override
    public void setType(SessionType type) {
        this.type = type;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public UserStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public long getLastActivityTime() {
        return this.lastActivityTime;
    }

    @Override
    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }
}
