package org.example.labx.service;

import org.example.labx.domain.Message;
import org.example.labx.domain.Utilizator;
import org.example.labx.repository.MessagesDBRepository;
import org.example.labx.utils.Page;
import org.example.labx.utils.Pageable;

import java.time.LocalDateTime;
import java.util.Map;

public class MessageService {
    private MessagesDBRepository repository;

    public MessageService(MessagesDBRepository repository) {
        this.repository = repository;
    }

    public Map<Long, Utilizator> getConversations(Utilizator currentUser) {
        return this.repository.findAllConversations(currentUser.getId());
    }

    public Page<Message> getMessagesForUsers(Utilizator user1, Utilizator user2, Pageable pageable) {
        return this.repository.getMessagesBetweenUsers(user1.getId(), user2.getId(), pageable);
    }

    public Message sendMessage(Utilizator sender, Utilizator receiver, String message) {
        Message newMessage = new Message(sender, receiver, LocalDateTime.now(), message);
        var res = this.repository.saveMessage(newMessage);

        if (res.isPresent())
            throw new RuntimeException("Error at sending message!");

        return newMessage;
    }

    public void beginConversation(Utilizator sender, Utilizator receiver) {
        this.repository.saveConversation(sender.getId(), receiver.getId());
    }

    public Long getNrOfMessageB2Users(Utilizator currentUser, Utilizator selectedUserForMessage) {
        return this.repository.getTotalNrOfMessagesBetweenUsers(currentUser.getId(), selectedUserForMessage.getId());
    }
}
