package com.ddnconsulting.chatserver.dao.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.ddnconsulting.chatserver.dao.ContactDao;
import com.ddnconsulting.chatserver.model.User;
import org.junit.Before;
import org.junit.Test;

public class UserDaoImplTest {
    private UserDaoImpl userDao;
    private ContactDao contactDao;

    @Before
    public void setUp() {
        userDao = new UserDaoImpl();

        contactDao = mock(ContactDao.class);
        userDao.setContactDao(contactDao);
    }

    @Test
    public void testUserCrud() throws Exception {
        // Verify no users
        List<User> users = userDao.getUsers();
        assertEquals("Num users", 0, users.size());

        // Add a couple of users
        userDao.addUser("b@company.com", "Mr B");
        userDao.addUser("a@company.com", "Mr A");

        users = userDao.getUsers();

        // Verify 2 users returned alpha order
        assertEquals("Num users", 2, users.size());
        assertEquals("First user name", "Mr A", users.get(0).getName());
        assertEquals("Second user name", "Mr B", users.get(1).getName());

        User mrA = users.get(0);
        User mrB = users.get(1);

        // Get user by ID
        assertEquals(users.get(0), userDao.getUser(mrA.getId()));

        // Find user by emails
        assertEquals(users.get(1), userDao.findUser(users.get(1).getEmailAddress()));


        // Delete a Mr B
        userDao.deleteUser(mrB.getId());

        // Should have called ContactDao to delete connections
        verify(contactDao).removeAllConnections(mrB.getId());

        users = userDao.getUsers();
        assertEquals("Num users", 1, users.size());


        // Deleted user returns null on find()
        assertNull(userDao.findUser(mrB.getEmailAddress()));

        // Try to find by ID that doesn't exist
        try {
            userDao.getUser(mrB.getId());
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException expected) {
            // expected
        }

        // Try to add duplicate email
        try {
            userDao.addUser("a@company.com", "Mr A");
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException expected) {
            // expected
        }

    }

}