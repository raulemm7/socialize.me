package org.example.labx.repository;

import org.example.labx.domain.Friendship;
import org.example.labx.domain.validators.Validator;
import org.example.labx.utils.Page;
import org.example.labx.utils.Pageable;

import java.sql.*;
import java.util.*;


public class FriendshipDBRepository implements FriendshipPagedRepository<Long, Friendship> {
    private final String database_url;
    private final String database_user;
    private final String database_password;
    private final Validator<Friendship> validator;
    private final UserDBRepository userDBRepository;

    public FriendshipDBRepository(String database_url, String database_user, String database_password, Validator<Friendship> validator, UserDBRepository userDBRepository) {
        this.database_url = database_url;
        this.database_user = database_user;
        this.database_password = database_password;
        this.validator = validator;
        this.userDBRepository = userDBRepository;
    }

    /**
     * Constructs a Friendship object from the current row in the provided ResultSet.
     *
     * @param resultSet the ResultSet positioned at the current row to retrieve Friendship data from, must not be null.
     * @return a Friendship object constructed from the data in the current ResultSet row.
     * @throws SQLException if an SQL error occurs while retrieving data from the ResultSet.
     */
    private Friendship getFriendshipFromQueryResult(ResultSet resultSet) throws SQLException {
        var id_friendship = resultSet.getLong("id");

        var idFirstUser = resultSet.getLong("firstUserID");
        var aux = this.userDBRepository.findOne(idFirstUser);
        if(aux.isEmpty())
            throw new RuntimeException("Nu a fost gasit utilizatorul din prietenie!");
        var user1 = aux.get();

        var idSecondUser = resultSet.getLong("secondUserID");
        aux = this.userDBRepository.findOne(idSecondUser);
        if(aux.isEmpty())
            throw new RuntimeException("Nu a fost gasit utilizatorul din prietenie!");
        var user2 = aux.get();

        var dateFriendship = resultSet.getTimestamp("friendsFrom").toLocalDateTime();

        Friendship friendship = new Friendship(user1, user2, dateFriendship);
        friendship.setId(id_friendship);

        return friendship;
    }

    /**
     * Sets parameter values in the provided PreparedStatement based on the Friendship data.
     *
     * @param statement the PreparedStatement to set parameter values in, must not be null.
     * @param friendship the Friendship object whose data is used to set the parameters in the statement.
     * @throws SQLException if an SQL error occurs while setting parameter values in the PreparedStatement.
     */
    private void setDataInStatement(PreparedStatement statement, Friendship friendship) throws SQLException {
        statement.setLong(1, friendship.getFirstUser().getId());
        statement.setLong(2, friendship.getSecondUser().getId());
    }

    /**
     * Finds a friendship by its unique ID in the database.
     *
     * @param id the ID of the friendship to find; must not be null.
     * @return an `Optional<Friendship>` containing the found friendship if present; otherwise, an empty `Optional`.
     * @throws RuntimeException if an SQL exception occurs while attempting to connect or execute the query.
     */
    public Optional<Friendship> findOne(Long id) {
        String sql = "SELECT * FROM \"Friendships\" WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(this.database_url, this.database_user, this.database_password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(this.getFriendshipFromQueryResult(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Eroare la gasirea prieteniei in baza de date!");
        } catch (RuntimeException e) {
            throw new RuntimeException("Eroare la gasirea utilizatorului in prietenie!");
        }
    }

    /**
     * Retrieves all friendships from the database.
     *
     * @return a `Collection<Friendship>` containing all friendships; may be empty if no friendships are found.
     * @throws RuntimeException if an SQL exception occurs while attempting to connect or execute the query.
     */
    public Collection<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        String sql = "SELECT * FROM \"Friendships\"";

        try (Connection connection = DriverManager.getConnection(this.database_url, this.database_user, this.database_password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                friendships.add(this.getFriendshipFromQueryResult(resultSet));
            }
            return friendships;

        } catch (SQLException e) {
            throw new RuntimeException("Eroare la preluarea prieteniilor din baza de date!", e);
        } catch (RuntimeException e) {
            throw new RuntimeException("Eroare la gasirea utilizatorului in prietenie!");
        }
    }

    public Long getNumberOfFriends(Long id) {
        String sql = "SELECT * FROM \"Friendships\" where firstUserID = ? or secondUserID = ?";
        long number = 0L;

        try(Connection connection = DriverManager.getConnection(database_url, database_user, database_password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, id);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                number += 1L;
            }
            return number;
        }
        catch (SQLException e) {
            throw new RuntimeException("Error");
        }
    }

    /**
     * Retrieves a friendship by the IDs of the two users involved.
     *
     * @param idFirstUser the ID of the first user; must not be null.
     * @param idSecondUser the ID of the second user; must not be null.
     * @return an `Optional<Friendship>` containing the found friendship if present; otherwise, an empty `Optional`.
     * @throws RuntimeException if an SQL exception occurs during the query execution.
     */
    public Optional<Friendship> findByUsersIDs(Long idFirstUser, Long idSecondUser) {
        String sql = "SELECT * FROM \"Friendships\" WHERE (firstUserID = ? AND secondUserID = ?) OR (firstUserID = ? AND secondUserID = ?)";

        try(Connection connection = DriverManager.getConnection(this.database_url, this.database_user, this.database_password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idFirstUser);
            statement.setLong(2, idSecondUser);
            statement.setLong(3, idSecondUser);
            statement.setLong(4, idFirstUser);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.of(this.getFriendshipFromQueryResult(resultSet)) : Optional.empty();

        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la preluarea datelor din baza de date!");
        }
    }

    /**
     * Saves a friendship to the database.
     *
     * @param friendship the `Friendship` object to save; must not be null.
     * @return an `Optional<Friendship>` containing the saved friendship if the operation fails; otherwise, an empty `Optional`.
     * @throws RuntimeException if an SQL exception occurs while attempting to connect or execute the query.
     */
    public Optional<Friendship> save(Friendship friendship) {
        this.validator.validate(friendship);

        String sql = "INSERT INTO \"Friendships\" (firstUserID, secondUserID, friendsFrom) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(this.database_url, this.database_user, this.database_password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            this.setDataInStatement(statement, friendship);
            statement.setObject(3, friendship.getFriendsFrom());

            return (statement.executeUpdate() > 0) ? Optional.empty() : Optional.of(friendship);

        } catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea prieteniei in baza de date!");
        }
    }

    /**
     * Deletes a friendship from the database by its unique ID.
     *
     * @param id the unique ID of the friendship to be deleted, must not be null.
     * @return an `Optional<Friendship>` containing the deleted friendship if it was found and deleted successfully;
     *         otherwise, an empty `Optional`.
     * @throws RuntimeException if an SQL exception occurs while attempting to connect, execute the query, or delete the friendship.
     */

    public Optional<Friendship> delete(Long id) {
        String sql = "DELETE FROM \"Friendships\" WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(this.database_url, this.database_user, this.database_password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            Optional<Friendship> friendship = this.findOne(id);
            if (friendship.isEmpty()) {
                return Optional.empty();
            }

            statement.setLong(1, id);
            return statement.executeUpdate() > 0 ? friendship : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la ștergerea prieteniei din baza de date!", e);
        }
    }

    /**
     * Updates the details of an existing friendship in the database.
     *
     * @param friendship the `Friendship` object containing updated details, must not be null.
     * @return an `Optional<Friendship>` containing the updated friendship if the update was successful;
     *         otherwise, an empty `Optional` if the friendship was not found.
     * @throws RuntimeException if an SQL exception occurs while attempting to connect, execute the query, or update the friendship.
     */

    public Optional<Friendship> update(Friendship friendship) {
        if(this.findOne(friendship.getId()).isEmpty()) {
            return Optional.empty();
        }

        String sql = "UPDATE \"Friendships\" SET " +
                "firstUserID = ?, secondUserID = ?" +
                "WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(this.database_url, this.database_user, this.database_password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            this.setDataInStatement(statement, friendship);
            statement.setLong(3, friendship.getId());

            return statement.executeUpdate() > 0 ? Optional.of(friendship) : Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea prieteniei in baza de date!");
        }
    }

    public Page<Friendship> getFriendshipsOnPage(Long id, Pageable pageable) {
        ArrayList<Friendship> friendships = new ArrayList<>();
        String sql = "SELECT * FROM \"Friendships\" where firstUserID = ? or secondUserID = ? LIMIT ? OFFSET ?";

        try(Connection connection = DriverManager.getConnection(database_url, database_user, database_password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setLong(3, pageable.getPageSize());
            statement.setLong(4, (pageable.getPageNumber() - 1) * pageable.getPageSize());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                friendships.add(getFriendshipFromQueryResult(resultSet));
            }
            return new Page<>(friendships, getNumberOfFriends(id));
        }
        catch (SQLException e) {
            throw new RuntimeException("Error");
        }
    }
}
