package org.example.socialnetwork.repository.db;

import org.example.socialnetwork.domain.*;
import org.example.socialnetwork.domain.validators.Validator;
import org.example.socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RepositoryCereriDB implements Repository<Tuple<Utilizator,Utilizator>, FriendRequest> {
    String url;
    String username;
    String password;
    Validator<FriendRequest> validator;
    public RepositoryCereriDB(String url,String username,String password,Validator<FriendRequest> validator){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<Utilizator, Utilizator> utilizatoriTuple) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from cereri where id = ?");
        )
        {
            Utilizator utilizator1 = utilizatoriTuple.getLeft();
            Utilizator utilizator2 = utilizatoriTuple.getRight();

            statement.setString(1, utilizator1.getFirstName());
            statement.setString(2, utilizator1.getLastName());
            statement.setString(3, utilizator1.getUserName());
            statement.setString(4, utilizator2.getFirstName());
            statement.setString(5, utilizator2.getLastName());
            statement.setString(6, utilizator2.getUserName());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                FriendRequestStatus status = FriendRequestStatus.valueOf(resultSet.getString("status"));
                return Optional.of(new FriendRequest(utilizator1, utilizator2, status));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        Set<FriendRequest> cereriSet = new HashSet<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from cereri");
            ResultSet resultSet = statement.executeQuery();)
        {
            while(resultSet.next()){
                String firstName1 = resultSet.getString("utilizator1_firstname");
                String lastName1 = resultSet.getString("utilizator1_lastname");
                String username1 = resultSet.getString("utilizator1_username");
                String firstName2 = resultSet.getString("utilizator2_firstname");
                String lastName2 = resultSet.getString("utilizator2_lastname");
                String username2 = resultSet.getString("utilizator2_username");
                String password1 = resultSet.getString("utilizator1_password");
                String password2 = resultSet.getString("utilizator2_password");
                Utilizator utilizator1 = new Utilizator(firstName1,lastName1,username1,password1);
                Utilizator utilizator2 = new Utilizator(firstName2,lastName2,username2,password2);
                FriendRequestStatus status = FriendRequestStatus.valueOf(resultSet.getString("status"));
                FriendRequest cerere = new FriendRequest(utilizator1,utilizator2,status);
                cereriSet.add(cerere);
            }
            return cereriSet;
        }catch (SQLException e){
            throw new RuntimeException();
        }

    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        Utilizator utilizator1 = entity.getUser1();
        Utilizator utilizator2 = entity.getUser2();
        FriendRequestStatus status = entity.getStatus();
        String utilizator1_firstname = utilizator1.getFirstName();
        String utilizator1_lastname  = utilizator1.getLastName();
        String utilizator1_username  = utilizator1.getUserName();
        String utilizator2_firstname = utilizator2.getFirstName();
        String utilizator2_lastname = utilizator2.getLastName();
        String utilizator2_username = utilizator2.getUserName();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO cereri (utilizator1_firstname,utilizator1_lastname," +
                             "utilizator1_username, utilizator2_firstname,utilizator2_lastname," +
                             "utilizator2_username, status) VALUES (?, ?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1,utilizator1_firstname);
            statement.setString(2,utilizator1_lastname);
            statement.setString(3, utilizator1_username);
            statement.setString(4, utilizator2_firstname );
            statement.setString(5, utilizator2_lastname);
            statement.setString(6, utilizator2_username);
            statement.setString(7, String.valueOf(status));

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating request failed, no rows affected.");
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return Optional.of(entity);
            } else {
                throw new SQLException("Creating request failed, no ID obtained.");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Utilizator, Utilizator> utilizatorUtilizatorTuple) {
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        Tuple<Utilizator, Utilizator> id = entity.getId();
        String status = String.valueOf(entity.getStatus());

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE cereri SET status = ? WHERE (utilizator1_username = ? AND utilizator2_username = ?) OR (utilizator1_username = ? AND utilizator2_username = ?)")) {

            statement.setString(1, status);
            statement.setString(2, id.getLeft().getUserName());
            statement.setString(3, id.getRight().getUserName());
            statement.setString(4, id.getRight().getUserName()); // Swap user1 and user2 for the second condition
            statement.setString(5, id.getLeft().getUserName());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }

            return Optional.of(entity);

        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    @Override
    public void setReply(FriendRequest entity, FriendRequest entity1) {

    }
}
