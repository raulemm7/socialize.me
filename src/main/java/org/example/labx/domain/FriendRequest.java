package org.example.labx.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FriendRequest extends Entity<Long> {
    private final Utilizator userFrom;
    private final Utilizator userTo;
    private final LocalDateTime sentAt;
    private final Boolean newFriendRequest;

    public FriendRequest(Utilizator userFrom, Utilizator userTo, LocalDateTime sentAt, Boolean newFriendRequest) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.sentAt = sentAt;
        this.newFriendRequest = newFriendRequest;
    }

    public Utilizator getUserFrom() {
        return userFrom;
    }

    public Utilizator getUserTo() {
        return userTo;
    }

    public LocalDateTime getDate() {
        return sentAt;
    }

    public Boolean checkIsNew() {
        return newFriendRequest;
    }

    public String userFromData() {
        return userFrom.getFirstName() + " " + userFrom.getLastName()
                + ", sent: " + sentAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendRequest that)) return false;
        return Objects.equals(userFrom, that.userFrom) && Objects.equals(userTo, that.userTo) && Objects.equals(sentAt, that.sentAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userFrom, userTo, sentAt);
    }
}
