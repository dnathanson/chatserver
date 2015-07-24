package com.ddnconsulting.chatserver.dao.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * JUnit test case for UserPair
 */
public class UserPairTest {

    /**
     * Test equals method.  Ensure that order of users doesn't matter.
     */
    @Test
    public void testEquals() throws Exception {
        assertEquals(new UserPair(5, 10), new UserPair(10, 5));
    }

    /**
     * Test hashCode method.  Ensure that order of users doesn't matter.
     */
    @Test
    public void testHashCode() throws Exception {
        assertEquals(new UserPair(5, 10).hashCode(), new UserPair(10, 5).hashCode());
    }
}