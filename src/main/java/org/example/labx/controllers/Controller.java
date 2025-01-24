package org.example.labx.controllers;

import org.example.labx.domain.Utilizator;
import org.example.labx.service.FriendRequestsService;
import org.example.labx.service.FriendshipService;
import org.example.labx.service.MessageService;
import org.example.labx.service.UserService;

public class Controller {
    private Utilizator currentUser;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestsService friendRequestsService;
    private MessageService messageService;

    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestsService friendRequestsService, MessageService messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestsService = friendRequestsService;
        this.messageService = messageService;
    }

    public void setCurrentUser(Utilizator currentUser) {
        this.currentUser = currentUser;
    }

    public Utilizator getCurrentUser() {
        return this.currentUser;
    }

    public UserService getUserService() {
        return userService;
    }

    public FriendshipService getFriendshipService() {
        return friendshipService;
    }

    public FriendRequestsService getFriendRequestsService() {
        return friendRequestsService;
    }

    public MessageService getMessageService() {
        return messageService;
    }
}
