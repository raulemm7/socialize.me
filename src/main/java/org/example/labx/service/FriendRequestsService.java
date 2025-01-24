package org.example.labx.service;

import org.example.labx.domain.FriendRequest;
import org.example.labx.domain.Utilizator;
import org.example.labx.repository.FriendRequestsDBRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public class FriendRequestsService {
    private FriendRequestsDBRepository friendRequestsDBRepository;

    public FriendRequestsService(FriendRequestsDBRepository friendRequestsDBRepository) {
        this.friendRequestsDBRepository = friendRequestsDBRepository;
    }

    public String addFriendRequest(Utilizator userFrom, Utilizator userTo) {
        try{
            if(friendRequestsDBRepository.exists(userFrom.getId(), userTo.getId()))
                return "Already sent friend request!";

            var res = friendRequestsDBRepository.save(new FriendRequest(userFrom, userTo, LocalDateTime.now(), true));
            return res.isEmpty() ? "Sent friend request!" : "Already sent friend request!";
        }
        catch(Exception e){
            return "Error ocurred: " + e.getMessage();
        }
    }

    public Map<Long, FriendRequest> getFriendRequests(Utilizator userFrom) {
        return this.friendRequestsDBRepository.getFriendRequestsForUser(userFrom.getId());
    }

    public void removeFriendRequest(FriendRequest friendRequest) {
        try {
            this.friendRequestsDBRepository.delete(friendRequest.getId());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public boolean setFriendRequestFalse(Long senderID, Long receiverID) {
        this.friendRequestsDBRepository.setFalse(senderID, receiverID);
        return !this.friendRequestsDBRepository.setFalse(senderID, receiverID);
    }

    public Collection<String> getNots(Long receiverID) {
        return this.friendRequestsDBRepository.getAllNewRequests(receiverID);
    }
}
