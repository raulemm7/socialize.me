package org.example.labx.domain;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a friendship between two users in the system.
 * Extends the `Entity` class with a Long identifier.
 */
public class Friendship extends Entity<Long> {
    private final Utilizator user1;
    private final Utilizator user2;
    private final LocalDateTime friendsFrom;

    /**
     * Constructs a new `Friendship` with the specified users.
     *
     * @param user1 the first user in the friendship
     * @param user2 the second user in the friendship
     */
    public Friendship(Utilizator user1, Utilizator user2, LocalDateTime friendsFrom) {
        this.user1 = user1;
        this.user2 = user2;
        this.friendsFrom = friendsFrom;
    }

    /**
     * Retrieves the first user in the friendship.
     *
     * @return the first user
     */
    public Utilizator getFirstUser() {
        return user1;
    }

    /**
     * Retrieves the second user in the friendship.
     *
     * @return the second user
     */
    public Utilizator getSecondUser() {
        return user2;
    }

    /**
     * Retrieves the date time since the users are friends
     * @return {@link LocalDateTime} instance
     */
    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    @Override
    public String toString() {
        return "ID: " + this.getId() + ", id1: " + user1.getId() + ", id2: " + user2.getId() +
                ", friendsFrom: " + this.friendsFrom.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship that)) return false;
        return Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2);
    }
}
