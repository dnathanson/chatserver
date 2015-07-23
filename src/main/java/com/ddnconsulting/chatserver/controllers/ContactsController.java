package com.ddnconsulting.chatserver.controllers;

import java.util.List;

import com.ddnconsulting.chatserver.async.ContactProducer;
import com.ddnconsulting.chatserver.dao.ConnectedSessionDao;
import com.ddnconsulting.chatserver.dao.ContactDao;
import com.ddnconsulting.chatserver.dao.UserDao;
import com.ddnconsulting.chatserver.model.ContactChange;
import com.ddnconsulting.chatserver.model.ContactChange.ChangeType;
import com.ddnconsulting.chatserver.model.User;
import com.ddnconsulting.chatserver.model.UserStatus;
import com.ddnconsulting.chatserver.session.ConnectedSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for endpoints supporting user contacts.
 */
@RestController
public class ContactsController
{
    @Autowired
    UserDao userDao;

    @Autowired
    ContactDao contactDao;

    @Autowired
    ConnectedSessionDao sessionDao;

    @Autowired
    ContactProducer contactProducer;

    /**
     * Adds a new contact for the current user.
     *
     * @param contactUserId ID of user to which the current user is go be connected
     * @return the user who was connected
     */
    @RequestMapping(value = "/contacts/user/{contactUserId}", method = RequestMethod.POST)
    public User addContact(@PathVariable(value = "contactUserId") long contactUserId,
                           @RequestParam String sessionId) {
        ConnectedSession connectedSession = sessionDao.getSession(sessionId);
        User contact = userDao.getUser(contactUserId);
        if (contact == null) {
            throw new RuntimeException("No user found with ID [" + contactUserId + "]");
        }
        contactDao.createConnection(connectedSession.getUserId(), contactUserId);

        // Publish creation of new contact so all servers can push contact list changes to clients for both users
        ContactChange newContact = new ContactChange();
        newContact.setType(ChangeType.ADDED);
        newContact.setUserId1(connectedSession.getUserId());
        newContact.setUserId2(contactUserId);
        contactProducer.publish(newContact);

        return contact;
    }

    /**
     * Delete a contact for the current user. This will remove the current user as a contact of the other user, too,
     * since connections between users are bidirectional.
     *
     * @param contactUserId ID of user to which the current user is go be connected
     * @return the user who was connected
     */
    @RequestMapping(value = "/contacts/user/{contactUserId}", method = RequestMethod.DELETE)
    public User removeContact(@PathVariable(value = "contactUserId") long contactUserId,
                           @RequestParam String sessionId) {
        ConnectedSession connectedSession = sessionDao.getSession(sessionId);
        User contact = userDao.getUser(contactUserId);
        contactDao.removeConnection(connectedSession.getUserId(), contactUserId);

        // Publish removal of contact so all servers can push contact list changes to clients for both users
        ContactChange newContact = new ContactChange();
        newContact.setType(ChangeType.REMOVED);
        newContact.setUserId1(connectedSession.getUserId());
        newContact.setUserId2(contactUserId);
        contactProducer.publish(newContact);

        return contact;
    }

    /**
     * Returns all contacts for current user.  Presence for each contact will be merged into the user record.
     *
     * @return list of User objects representing contact list for current user
     */
    @RequestMapping("/contacts")
    public List<User> getContacts(@RequestParam String sessionId)
    {
        ConnectedSession connectedSession = sessionDao.getSession(sessionId);
        List<User> contacts = contactDao.getContacts(connectedSession.getUserId());

        long now = System.currentTimeMillis();

        // TODO: for each contact, get presence of user from Redis.  For now, just set to ACTIVE
        for (User contact : contacts) {
            contact.setStatus(UserStatus.ACTIVE);
            contact.setLastActiveTime(now);
        }

        return contacts;
    }

}