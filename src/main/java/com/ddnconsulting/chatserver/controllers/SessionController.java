package com.ddnconsulting.chatserver.controllers;

import java.util.HashMap;
import java.util.Map;

import com.ddnconsulting.chatserver.dao.ConnectedSessionDao;
import com.ddnconsulting.chatserver.model.UserStatus;
import com.ddnconsulting.chatserver.session.ConnectedSession;
import com.ddnconsulting.chatserver.session.ConnectedSession.SessionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for endpoints supporting user sessions.  Login, logout, ping.
 *
 * NOTE: For real server, this probably wouldn't be a REST controller.  It would be form-based since it needs
 * to get username/password.  For this exercise, it will be REST.
 *
 * @author Dan Nathanson
 */
@RestController
public class SessionController {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOG = LoggerFactory.getLogger(SessionController.class);

    @Autowired
    ConnectedSessionDao sessionDao;

    /**
     * Handles notification from endpoint that user session is still alive.  If session still exists (hasn't been
     * timed out on the server, last active time for session is updated.  If session has timed out, response is sent
     * back indicating re-authentication required.
     *
     * @param sessionId the ID of the session
     * @return ping status
     */
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping(@RequestParam String sessionId) {
        ConnectedSession connectedSession = sessionDao.getSession(sessionId);
        if (connectedSession != null)
        {
            connectedSession.setLastActivityTime(System.currentTimeMillis());
            connectedSession.setStatus(UserStatus.ACTIVE);

            // TODO: update user in Redis

            return "OK";
        }
        else {
            return "LOGIN_REQUIRED";
        }
    }

    /**
     * Handles starting a new session for a user.  A session represents an authenticated connection between the
     * server and a user's chat client.  Returns ID of new session.
     *
     * TODO: this should be done with standard HTTP POST of form data (username / password).  This is just a quick
     * REST hack for establishing a session.
     *
     * @param currentUserId the ID of the user
     * @return ID of new session
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public Map login(@RequestParam long currentUserId) {
        ConnectedSession connectedSession = sessionDao.addSession(currentUserId, SessionType.POLLING);
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("sessionId", connectedSession.getId());
        return response;
    }

    /**
     * Handles ending an existing session for a user.
     *
     * TODO: this should be done with standard HTTP POST. This is just a quick REST hack for killing a session.
     *
     * @param sessionId the ID of the session to terminate
     * @return logoff status
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Map logoff(@RequestParam String sessionId) {
        ConnectedSession connectedSession = sessionDao.removeSession(sessionId);
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        return response;
    }

}
