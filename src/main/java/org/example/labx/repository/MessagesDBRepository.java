package org.example.labx.repository;

import org.example.labx.domain.Message;
import org.example.labx.domain.Utilizator;
import org.example.labx.utils.Page;
import org.example.labx.utils.Pageable;

import java.sql.*;
import java.util.*;

public class MessagesDBRepository {
    private final String database_url;
    private final String database_user;
    private final String database_password;
    private final UserDBRepository userDBRepository;

    public MessagesDBRepository(String database_url, String database_user, String database_password, UserDBRepository userDBRepository) {
        this.database_url = database_url;
        this.database_user = database_user;
        this.database_password = database_password;
        this.userDBRepository = userDBRepository;
    }

    private Utilizator getUserFromResult(Long id_user, ResultSet rs) throws SQLException {
        var id = rs.getLong("firstuserID");
        if(id != id_user) {
            var user = this.userDBRepository.findOne(id);
            if(user.isPresent())
                return user.get();
        }

        id = rs.getLong("seconduserID");
        var user = this.userDBRepository.findOne(id);
        if(user.isPresent())
            return user.get();
        else
            throw new SQLException("User not found");
    }

    private Message getMessageFromResult(ResultSet rs) throws SQLException {
        Utilizator sender, receiver;

        var id = rs.getLong("id");

        var aux = this.userDBRepository.findOne(rs.getLong("senderID"));
        if(aux.isPresent())
            sender = aux.get();
        else throw new SQLException("User not found");

        aux = this.userDBRepository.findOne(rs.getLong("receiverID"));
        if(aux.isPresent())
            receiver = aux.get();
        else throw new SQLException("User not found");

        var sendAt = rs.getTimestamp("sentAt").toLocalDateTime();
        var message = rs.getString("message");

        Message ms = new Message(sender, receiver, sendAt, message);
        ms.setId(id);

        return ms;
    }

    public Map<Long, Utilizator> findAllConversations(Long id_user) {
        Map<Long, Utilizator> conversations = new HashMap<>();
        String sql = "SELECT * FROM \"Conversations\" WHERE \"firstuserID\" = ? or \"seconduserID\" = ?";

        try(Connection connection = DriverManager.getConnection(this.database_url, database_user, database_password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id_user);
            statement.setLong(2, id_user);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var otherUser = getUserFromResult(id_user, resultSet);
                conversations.put(otherUser.getId(), otherUser);
            }

            return conversations;
        }
        catch (SQLException e) {
            throw new RuntimeException("Error!");
        }
    }

    public Boolean checkConversation(Long idsender, Long idreceiver) {
        String sql = "SELECT * FROM \"Conversations\" WHERE (\"firstuserID\" = ? AND \"seconduserID\" = ?) OR" +
                "(\"firstuserID\" = ? AND \"seconduserID\" = ?)";

        try(PreparedStatement stmt = DriverManager.getConnection(database_url, database_user, database_password).prepareStatement(sql)) {
            stmt.setLong(1, idsender);
            stmt.setLong(2, idreceiver);
            stmt.setLong(3, idreceiver);
            stmt.setLong(4, idsender);

            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next();
        }
        catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void saveConversation(Long idsender, Long idreceiver) {
        if(this.checkConversation(idsender, idreceiver))
            return;

        String sql = "INSERT INTO \"Conversations\" (\"firstuserID\", \"seconduserID\") VALUES (?, ?)";
        try(PreparedStatement stmt = DriverManager.getConnection(database_url, database_user, database_password).prepareStatement(sql)) {
            stmt.setLong(1, idsender);
            stmt.setLong(2, idreceiver);

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Long getTotalNrOfMessagesBetweenUsers(Long id_user1, Long id_user2) {
        String sql = "SELECT COUNT(\"id\") AS total FROM \"Messages\" WHERE \"senderID\" = ? AND \"receiverID\" = ? " +
                "OR \"senderID\" = ? AND \"receiverID\" = ?";

        try(Connection connection = DriverManager.getConnection(database_url, database_user, database_password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id_user1);
            statement.setLong(2, id_user2);
            statement.setLong(3, id_user2);
            statement.setLong(4, id_user1);

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
                return resultSet.getLong("total");
            return 0L;
        }
        catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Page<Message> getMessagesBetweenUsers(Long id_user1, Long id_user2, Pageable pageable) {
        ArrayList<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM (" +
                "    SELECT * FROM \"Messages\" " +
                "WHERE \"senderID\" = ? and \"receiverID\" = ? OR \"senderID\" = ? and \"receiverID\" = ? " +
                "ORDER BY \"sentAt\" DESC " +
                "LIMIT ? OFFSET ?) AS latest_msg " +
                "ORDER BY \"sentAt\"";

        try(Connection connection = DriverManager.getConnection(database_url, database_user, database_password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id_user1);
            statement.setLong(2, id_user2);
            statement.setLong(3, id_user2);
            statement.setLong(4, id_user1);
            statement.setLong(5, pageable.getPageSize());
            statement.setLong(6, (pageable.getPageNumber() - 1) * pageable.getPageSize());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var mess = getMessageFromResult(resultSet);
                messages.add(mess);
            }

            return new Page<>(messages, getTotalNrOfMessagesBetweenUsers(id_user1, id_user2));
        }
        catch (SQLException e) {
            throw new RuntimeException("Error at extracting messages!");
        }
    }

    public Optional<Message> saveMessage(Message message) {
        String sql = "INSERT INTO \"Messages\" (\"senderID\", \"receiverID\", \"sentAt\", \"message\") VALUES (?, ?, ?, ?)";

        try(Connection connection = DriverManager.getConnection(database_url, database_user, database_password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, message.getSender().getId());
            statement.setLong(2, message.getReceiver().getId());
            statement.setObject(3, message.getSendAt());
            statement.setString(4, message.getMessage());

            return statement.executeUpdate() > 0 ? Optional.empty() : Optional.of(message);
        }
        catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
