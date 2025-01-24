package org.example.labx.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message extends Entity<Long>{
    private final Utilizator sender;
    private final Utilizator receiver;
    private final LocalDateTime sendAt;
    private final String message;

    public Message(Utilizator sender, Utilizator receiver, LocalDateTime sendAt, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.sendAt = sendAt;
        this.message = message;
    }

    public Utilizator getSender() {
        return sender;
    }

    public Utilizator getReceiver() {
        return receiver;
    }

    public LocalDateTime getSendAt() {
        return sendAt;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message1)) return false;
        return Objects.equals(sender, message1.sender) && Objects.equals(receiver, message1.receiver) && Objects.equals(sendAt, message1.sendAt) && Objects.equals(message, message1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, sendAt, message);
    }

    public boolean isEmpty() {
        return this.message.isEmpty();
    }
}
