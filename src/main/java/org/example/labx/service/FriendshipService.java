package org.example.labx.service;

import org.example.labx.domain.Friendship;
import org.example.labx.domain.Utilizator;
import org.example.labx.repository.FriendshipDBRepository;
import org.example.labx.utils.Page;
import org.example.labx.utils.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The FriendshipService class provides services for managing `Friendship` entities,
 * including ID generation, adding and deleting friendships, and retrieving a user's friends.
 */
public class FriendshipService{
    private final FriendshipDBRepository friendshipRepository;

    /**
     * Constructs a FriendshipService with the specified user and friendship repositories.
     *
     * @param friendshipRepository the repository handling friendship data
     */
    public FriendshipService(FriendshipDBRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * @return an Iterable collection of all {@link Friendship} entities from database
     */
    public Collection<Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
    }

    /**
     * Adds a new friendship between two users
     *
     * @param firstUser the first user
     * @param secondUser the second user
     * @return a string message indicating the result of the operation
     */
    public String addFriendships(Utilizator firstUser, Utilizator secondUser) {
        if(firstUser == null) return "Primul utilizator nu exista inregistrat!";
        if(secondUser == null) return "Al doilea utilizator nu exista inregistrat!";

        try{
            var friendship = new Friendship(firstUser, secondUser, LocalDateTime.now());
            var result = this.friendshipRepository.save(friendship);
            return result.isEmpty() ? "Congrats! Now you are friends!" : "You are already friend with him";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Deletes a friendship between two users
     *
     * @param firstUser first user
     * @param secondUser second user
     * @return a string message indicating whether the friendship was successfully deleted
     */
    public String deleteFriendship(Utilizator firstUser, Utilizator secondUser) {
        if(firstUser == null) return "Primul utilizator nu exista!";
        if(secondUser == null) return "Al doilea utilizator nu exista!";

        try {
            var friendship = this.friendshipRepository.findByUsersIDs(firstUser.getId(), secondUser.getId());
            if (friendship.isPresent()) {
                this.friendshipRepository.delete(friendship.get().getId());
                return "Prietenie stearsa cu succes!";
            }
            return "Prietenia nu exista!";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public Page<Utilizator> getAllUsersFriends(Long idUser, Long pageNumber, Long pageSize) {
        var friendships = this.friendshipRepository.getFriendshipsOnPage(idUser, new Pageable(pageSize, pageNumber));
        ArrayList<Utilizator> friends = new ArrayList<>(friendships.getEntitiesOnPage().stream()
                .map(friendship -> {
                    if(friendship.getFirstUser().getId().equals(idUser)) {
                        return friendship.getSecondUser();
                    } else {
                        return friendship.getFirstUser();
                    }
                })
                .toList());

        return new Page<>(friends, friendships.getTotalNumberOfElements());
    }

    public Boolean existFriendship(Utilizator firstUser, Utilizator secondUser) {
        try{
            var friendship = this.friendshipRepository.findByUsersIDs(firstUser.getId(), secondUser.getId());
            return friendship.isPresent();
        }
        catch (Exception e) {
            throw new RuntimeException("Error");
        }
    }
}
