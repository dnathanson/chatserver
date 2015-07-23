package com.ddnconsulting.chatserver.session;

import com.ddnconsulting.chatserver.dao.ConnectedSessionDao;
import com.ddnconsulting.chatserver.model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Monitors connected sessions for activity.  Changes status of sessions from ACTIVE -> IDLE after 5 minutes
 * of inactivity and from IDLE -> AWAY after 15 minutes.
 *
 * NOTE: an alternative implementation would be to rely on the build-in idle time mechanics of an actual cache.
 * One way to do this would be to have a series of caches, one for each status (ACTIVE, IDLE, AWAY) and listeners
 * that add the entry to the next cache when it is evicted from the prior cache.  Not sure if this would be more
 * efficient that scanning the whole map every N seconds - would depend on how efficient the cache's timeout
 * code is.
 *
 * @author Dan Nathanson
 */
@Component
public class SessionMonitor {
    private static final Logger LOG = LoggerFactory.getLogger(SessionMonitor.class);

    @Autowired
    ConnectedSessionDao sessionDao;

    @Value("${session.idle.period}")
    private long idlePeriod;
    @Value("${session.away.period}")
    private long awayPeriod;
    @Value("${session.timeout.period}")
    private long timeoutPeriod;


    /**
     * Scans all connected sessions and updates status (or removes) sessions that have been inactive for configured
     * thresholds.
     */
    @Scheduled(fixedDelayString = "${session.monitor.scan.interval}")
    public void processSessions() {
        long now = System.currentTimeMillis();
        for (ConnectedSession session : sessionDao.getAllSessions()) {
            String sessionId = session.getId();

            // If currently AWAY, timeout session if expired
            if ((session.getStatus() == UserStatus.AWAY)) {
                if ((now - session.getLastActivityTime() > timeoutPeriod)) {
                    // Remove session from cache
                    sessionDao.removeSession(sessionId);

                    // TODO: Update presence for user in Redis to OFFLINE
                }
            }

            // If currently IDLE, change to AWAY if away threshold reached
            else if (session.getStatus() == UserStatus.IDLE) {
                if ((now - session.getLastActivityTime() > awayPeriod)) {
                    // Session is now AWAY
                    session.setStatus(UserStatus.AWAY);

                    // TODO: Update presence for user in Redis to AWAY
                }
            }
            // Must be active, change to IDLE if idle threshold reached
            else if ((now - session.getLastActivityTime() > idlePeriod)) {
                // Session is now IDLE
                session.setStatus(UserStatus.IDLE);

                // TODO: Update presence for user in Redis to IDLE
            }
        }
    }
}
