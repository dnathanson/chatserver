package com.ddnconsulting.chatserver.controllers;

import com.ddnconsulting.chatserver.dao.UserDao;
import com.ddnconsulting.chatserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for endpoints supporting Users.
 */
@RestController
public class UsersController
{
    @Autowired
    UserDao userDao;

    /**
     * Adds a new user to the system.
     *
     * @param newUser the user to add
     * @return the new User
     */
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public User addUser(@RequestBody User newUser) {
        return userDao.addUser(newUser.getEmailAddress(), newUser.getName());
    }

    /**
     * Looks up a user by email address.  Useful for adding contacts.
     *
     * @param emailAddress email address of user
     * @return User object associated with email address or error if no user found.
     */
    @RequestMapping("/users/{userEmail:.*}")
    public User getUser(@PathVariable(value = "userEmail") String emailAddress)
    {
        User user = userDao.findUser(emailAddress);

        // Lets handle this with generic exception handling.  Returning 404 would also be an option.
        if (user == null) {
            throw new RuntimeException("No user found with email address [" + emailAddress + "]");
        }
        return user;
    }

}