package org.example.labx.service;


import org.example.labx.domain.Friendship;
import org.example.labx.domain.Utilizator;
import org.example.labx.repository.FriendshipDBRepository;
import org.example.labx.repository.UserDBRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * The UserService class provides services for managing `Utilizator` (User) entities,
 * including ID generation, adding and deleting users.
 */
public class UserService {
    private final UserDBRepository userRepository;
    private final FriendshipDBRepository friendshipRepository;

    /**
     * Constructs a UserService with the specified user and friendship repositories.
     *
     * @param userRepo the repository handling user data
     * @param friendRepo the repository handling friendship data
     */
    public UserService(UserDBRepository userRepo, FriendshipDBRepository friendRepo) {
        this.userRepository = userRepo;
        this.friendshipRepository = friendRepo;
    }

    /**
     * Retrieves a user by their first and last name from the database.
     *
     * @param firstName the first name of the user to search for; must not be null.
     * @param lastName the last name of the user to search for; must not be null.
     * @return the {@link Utilizator} object representing the user if found.
     * @throws RuntimeException if an error occurs during the database query or if the user is not found.
     */
    public Utilizator getUser(String firstName, String lastName) {
        try{
            var result = this.userRepository.findByNames(firstName, lastName);
            if(result.isPresent())
                return result.get();

            throw new Exception("Utilizatorul nu a fost gasit");
        }
        catch (Exception e){
            throw new RuntimeException("Utilizatorul nu a fost gasit");
        }
    }

    /**
     * Retrieves all users from the repository.
     *
     * @return an Iterable collection of all `Utilizator` entities
     */
    public Collection<Utilizator> getAll(){
        return userRepository.findAll();
    }

    /**
     * Adds a new user to the repository with the specified first and last names.
     * If the user is added successfully, a success message is returned. If the user already exists
     * or an error occurs, the appropriate message is returned.
     *
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @return a string message indicating the result of the operation
     */
    public String addUser(String firstName, String lastName, String email, String password) {
        if(this.userRepository.findByEmail(email).isPresent())
            return "There is already an user with this email, please sign in!";

        var result = this.userRepository.save(new Utilizator(firstName, lastName, email, password));

        if(result.isEmpty())
            return "User created successfully! Login with you credentials";
        return "There is already an user with this credentials, please sign in!";
    }

    /**
     * Checks if a specified user, identified by their ID, is part of a given friendship.
     *
     * @param friendship the {@link Friendship} instance to check
     * @param idUser the ID of the user to verify as part of the friendship
     * @return {@code true} if the user with the specified ID is either the first or second user
     *         in the friendship; {@code false} otherwise
     */
    private boolean checkIfUserInFriendship(Friendship friendship, Long idUser) {
        return friendship.getFirstUser().getId().equals(idUser) || friendship.getSecondUser().getId().equals(idUser);
    }

    /**
     * Deletes a user with the specified first name and last name, along with their friendships.
     * A success or failure message is returned based on the operation's outcome.
     *
     * @param firstName user's first name
     * @param lastName user's last name
     * @return a string message indicating whether the user was successfully deleted
     */
    public String deleteUser(String firstName, String lastName) {
        try {
            Long id = this.getUser(firstName, lastName).getId();

            // sterg mai intai prieteniile cu ceilalti useri
            Iterable<Friendship> friendships = friendshipRepository.findAll();
            List<Long> friendshipsToDelete = StreamSupport.stream(friendships.spliterator(), false)
                    .filter(friendship -> checkIfUserInFriendship(friendship, id))
                    .map(Friendship::getId)
                    .toList();

            friendshipsToDelete.forEach(friendshipRepository::delete);

            // sterg utilizatorul
            var result = this.userRepository.delete(id);
            return result.isPresent() ? "Utilizator sters cu succes!" : "Stergere esuata!";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public Optional<Utilizator> authenticateUser(String email, String password) {
        return this.userRepository.findUserForLogin(email, password);
    }
}
