package com.ddnconsulting.chatserver.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ddnconsulting.chatserver.dao.ConnectedSessionDao;
import com.ddnconsulting.chatserver.model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * Interceptor to ensure that sessionId is added as query string arg and that session is active.
 *
 * @author Dan Nathanson
 */
@Component
public class SessionCheckInterceptor extends HandlerInterceptorAdapter implements ApplicationContextAware {
    private static final Logger LOG = LoggerFactory.getLogger(SessionCheckInterceptor.class);

    private ApplicationContext applicationContext;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String sessionId = request.getParameter("sessionId");
        if (sessionId == null) {
            throw new IllegalStateException("sessionId messing from query string");
        }

        ConnectedSession session = getSessionDao().getSession(sessionId);
        if (session == null) {
            throw new IllegalStateException("No session found for session ID [" + sessionId + "]. Login required");
        }

        session.setLastActivityTime(System.currentTimeMillis());
        session.setStatus(UserStatus.ACTIVE);

        // TODO: push presence change to Redis
        return true;
    }

    ConnectedSessionDao getSessionDao() {
        return applicationContext.getBean(ConnectedSessionDao.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
