package org.example.socialnetwork.repository.db;

import org.example.socialnetwork.domain.Message;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.domain.validators.Validator;
import org.example.socialnetwork.repository.Repository;

import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class RepositoryMesajeDB implements Repository<Long, Message> {
    String url;
    String username;
    String password;
    Validator<Message> validator;

    public RepositoryMesajeDB(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Message> findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM messages WHERE id = ?")) {
            statement.setInt(1, Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Utilizator from = findUtilizatorById(resultSet.getInt("from")).get();
                String mesaj = resultSet.getString("mesaj");
                LocalDateTime data = resultSet.getTimestamp("data").toLocalDateTime();
                Message reply;
                if((Long) resultSet.getObject("reply")!=null) {
                    reply = findOne((Long) resultSet.getObject("reply")).get();
                }else{
                    reply = null;
                }
                List<Utilizator> to = findUtilizatoriForMessage(id);
                Message message = new Message(from, to, mesaj, data, reply);
                message.setId(id);
                return Optional.of(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    private Optional<Utilizator> findUtilizatorById(int id) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");
        ){
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userName = resultSet.getString("username");
                String password1 = resultSet.getString("password");
                Utilizator utilizator = new Utilizator(firstName, lastName,userName,password1);
                return Optional.of(utilizator);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }


        return  Optional.empty();
    }


    private List<Utilizator> findUtilizatoriForMessage(Long messageId) {
        List<Utilizator> utilizatori = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT user_id FROM message_recipients WHERE message_id = ?")) {
            statement.setInt(1, Math.toIntExact(messageId));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long userId = (Long) resultSet.getObject("user_id");
                Utilizator utilizator = findUtilizatorById(Math.toIntExact(userId)).get();
                utilizatori.add(utilizator);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return utilizatori;
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messagesSet = new HashSet<>();

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement("SELECT * FROM messages order by \"data\" limit 5 offset 1");
             ResultSet resultSet = statement.executeQuery();) {
            while (resultSet.next()) {
                Long id = (Long) resultSet.getObject("id");
                Utilizator from = findUtilizatorById((resultSet.getInt("from"))).get();
                String mesaj = resultSet.getString("mesaj");
                LocalDateTime data = resultSet.getTimestamp("data").toLocalDateTime();
                Message reply;
                if(resultSet.getObject("reply")==null){
                    reply = null;
                }else {
                    reply = findOne((Long) resultSet.getObject("reply")).get();
                }
                List<Utilizator> to = findUtilizatoriForMessage(Long.valueOf(id));
                Message message = new Message(from, to, mesaj, data, reply);
                message.setId(id);
                messagesSet.add(message);
            }
            return messagesSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> save(Message entity) {
        Utilizator from = entity.getFrom();
        String mesaj = entity.getMesaj();
        LocalDateTime data = entity.getData();
        Message reply = entity.getReply();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO messages(\"from\", mesaj, data, reply) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1,Math.toIntExact(findUtilizatorId(from)));
            statement.setString(2, mesaj);
            statement.setTimestamp(3, Timestamp.valueOf(data));
            if (reply != null) {
                statement.setObject(4, Math.toIntExact(reply.getId()));
            } else {
                statement.setObject(4, null);
            }


            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating message failed, no rows affected.");
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = (Long) generatedKeys.getObject(1);
                saveUtilizatoriForMessage(id, entity.getTo());
                entity.setId(id);
                return Optional.of(entity);
            } else {
                throw new SQLException("Creating message failed, no ID obtained.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUtilizatoriForMessage(Long messageId, List<Utilizator> utilizatori) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO message_recipients(message_id, user_id) VALUES (?, ?)")) {

            for (Utilizator utilizator : utilizatori) {
                statement.setInt(1,Math.toIntExact(messageId));
               // statement.setInt(2,Math.toIntExact(utilizator.getId()));
                statement.setInt(2,Math.toIntExact(findUtilizatorId(utilizator)));
                statement.addBatch();
            }

            statement.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Long findUtilizatorId(Utilizator utilizator) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM users WHERE first_name = ? AND last_name = ? AND username = ?")) {

            statement.setString(1, utilizator.getFirstName());
            statement.setString(2, utilizator.getLastName());
            statement.setString(3, utilizator.getUserName());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id = (Long) resultSet.getObject("id");
                return id;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void setReply(Message message,Message reply){
        Long id_message = message.getId();
        Long id_reply = reply.getId();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(" UPDATE messages SET reply = ? WHERE id = ?",
                     Statement.RETURN_GENERATED_KEYS)){
            statement.setInt(1, Math.toIntExact(id_reply));
            statement.setInt(2,Math.toIntExact(id_message));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public Optional<Message> delete(Long aLong) {
        // Implement deletion logic if needed
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        // Implement update logic if needed
        return Optional.empty();
    }
}
