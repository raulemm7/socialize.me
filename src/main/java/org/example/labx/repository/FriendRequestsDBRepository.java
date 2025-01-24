package org.example.labx.repository;

import org.example.labx.domain.FriendRequest;

import java.sql.*;
import java.util.*;

public class FriendRequestsDBRepository implements Repository<Long, FriendRequest> {
    private final String database_url;
    private final String database_user;
    private final String database_password;
    private final UserDBRepository userDBRepository;

    private FriendRequest getFriendRequestFromResult(ResultSet resultSet) throws SQLException {
        var id = resultSet.getLong("firstuserID");
        var aux = this.userDBRepository.findOne(id);
        if(aux.isEmpty())
            throw new RuntimeException("User not found!");
        var user1 = aux.get();

        id = resultSet.getLong("seconduserID");
        aux = this.userDBRepository.findOne(id);
        if(aux.isEmpty())
            throw new RuntimeException("User not found!");
        var user2 = aux.get();

        var date  = resultSet.getTimestamp("requestSentOn").toLocalDateTime();
        var newf = resultSet.getBoolean("newFriendRequest");

        var fr = new FriendRequest(user1, user2, date, newf);
        fr.setId(resultSet.getLong("id"));
        return fr;
    }

    public FriendRequestsDBRepository(final String database_url, final String database_user, final String database_password, final UserDBRepository userDBRepository) {
        this.database_url = database_url;
        this.database_user = database_user;
        this.database_password = database_password;
        this.userDBRepository = userDBRepository;

    }

    public Optional<FriendRequest> findOne(Long id) {
        String sql = "SELECT * FROM \"FriendRequests\" WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(this.database_url, this.database_user, this.database_password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getFriendRequestFromResult(resultSet));
            }
            return Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error");
        }
    }

    public Collection<FriendRequest> findAll() {
        String sql = "SELECT * FROM \"FriendRequests\"";
        Set<FriendRequest> friendRequests = new HashSet<>();

        try(Connection connection = DriverManager.getConnection(database_url, database_user, database_password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                friendRequests.add(getFriendRequestFromResult(resultSet));
            }
            return friendRequests;
        }
        catch (SQLException e) {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        String sql = "INSERT INTO \"FriendRequests\" (\"firstuserID\", \"seconduserID\", \"requestSentOn\", \"newFriendRequest\") VALUES(?, ?, ?, ?)";

        try(Connection connection = DriverManager.getConnection(database_url, database_user, database_password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, entity.getUserFrom().getId());
            statement.setLong(2, entity.getUserTo().getId());
            statement.setObject(3, entity.getDate());
            statement.setBoolean(4, entity.checkIsNew());

            return (statement.executeUpdate() > 0) ? Optional.empty() : Optional.of(entity);
        }
        catch (SQLException e) {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public Optional<FriendRequest> delete(Long id) {
        String sql = "DELETE FROM \"FriendRequests\" WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(this.database_url, this.database_user, this.database_password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            Optional<FriendRequest> friendRequest = this.findOne(id);
            if (friendRequest.isEmpty()) {
                return Optional.empty();
            }

            statement.setLong(1, id);
            return statement.executeUpdate() > 0 ? friendRequest : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la ștergerea prieteniei din baza de date!", e);
        }
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        return Optional.empty();
    }

    public Map<Long, FriendRequest> getFriendRequestsForUser(Long userID) {
        String sql = "SELECT * FROM \"FriendRequests\" WHERE \"seconduserID\" = ?";
        Map<Long, FriendRequest> friendRequests = new HashMap<>();

        try(Connection connection = DriverManager.getConnection(database_url, database_user, database_password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userID);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var fr = getFriendRequestFromResult(resultSet);
                friendRequests.put(fr.getId(), fr);
            }

            return friendRequests;
        }
        catch(SQLException e) {
            throw new RuntimeException("Error");
        }
    }

    public Boolean exists(Long idFrom, Long idTo) {
        String sql = "SELECT * FROM \"FriendRequests\" WHERE \"firstuserID\" = ? and \"seconduserID\" = ?";

        try(Connection connection = DriverManager.getConnection(database_url, database_user, database_password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idFrom);
            statement.setLong(2, idTo);

            return statement.executeQuery().next();
        }
        catch(SQLException e) {
            throw new RuntimeException("Error");
        }
    }

    public Collection<String> getAllNewRequests(Long receiverID) {
        String sql ="SELECT \"firstuserID\", \"seconduserID\", \"newFriendRequest\" " +
                "FROM \"FriendRequests\" " +
                "WHERE \"newFriendRequest\" IS TRUE AND \"seconduserID\" = ?";

        try(Connection connection = DriverManager.getConnection(database_url, database_user, database_password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, receiverID);

            ResultSet resultSet = statement.executeQuery();
            Set<String> friendRequests = new HashSet<>();
            while (resultSet.next()) {
                friendRequests.add(String.valueOf(resultSet.getLong("firstuserID")) + "," +
                        String.valueOf(resultSet.getLong("seconduserID")));
            }

            return friendRequests;
        }
        catch (SQLException e) {
            throw new RuntimeException("Error");
        }
    }

    public boolean setFalse(Long senderID, Long receiverID) {
        String sql = "UPDATE \"FriendRequests\" " +
                "SET \"newFriendRequest\" = false " +
                "WHERE \"firstuserID\" = ? AND \"seconduserID\" = ?";

        try(Connection connection = DriverManager.getConnection(database_url, database_user, database_password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, senderID);
            statement.setLong(2, receiverID);

            return statement.executeUpdate() > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException("Error at setting false friendRequest");
        }
    }
}
