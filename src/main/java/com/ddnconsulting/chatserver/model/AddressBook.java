package com.ddnconsulting.chatserver.model;

import java.util.List;

/**
 * Represents the address book for a single user.
 *
 * @author Dan Nathanson
 */
public class AddressBook {
    private long userId;
    private List<User> contacts;
}
