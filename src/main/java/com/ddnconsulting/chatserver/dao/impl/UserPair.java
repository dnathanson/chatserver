package com.ddnconsulting.chatserver.dao.impl;

/**
 * Key for holding pair of users.  Equals and hashCode() written is such a way that it doesn't matter which user is
 * user1 or user2.
 */
final class UserPair {
    long user1;
    long user2;

    public UserPair(long user1, long user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserPair userPair = (UserPair) o;


        return (user1 == userPair.user1 && user2 == userPair.user2) ||
               (user1 == userPair.user2 && user2 == userPair.user1);


    }

    @Override
    public int hashCode() {
        return (int) ((user1+user2) ^ ((user1+user2) >>> 32));
    }
}
