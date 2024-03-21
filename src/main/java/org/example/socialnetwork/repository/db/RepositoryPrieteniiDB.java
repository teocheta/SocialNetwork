package org.example.socialnetwork.repository.db;

import org.example.socialnetwork.domain.Prietenie;
import org.example.socialnetwork.domain.Tuple;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.domain.validators.Validator;
import org.example.socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RepositoryPrieteniiDB implements Repository<Tuple<Utilizator,Utilizator>, Prietenie> {
    String url;
    String username;
    String password;
    Validator<Prietenie> validator;

    public RepositoryPrieteniiDB(String url,String username,String password,Validator<Prietenie> validator){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    @Override
    public Optional<Prietenie> findOne(Tuple<Utilizator, Utilizator> utilizatoriTuple) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from prietenii where id = ?");
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
            statement.setString(7, utilizator1.getPassword());
            statement.setString(8, utilizator2.getPassword());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                return Optional.of(new Prietenie(utilizator1, utilizator2, friendsFrom));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> prietenieSet = new HashSet<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement statement = connection.prepareStatement("select * from prietenii");
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
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                Prietenie prietenie = new Prietenie(utilizator1,utilizator2,friendsFrom);
                prietenieSet.add(prietenie);
            }
            return prietenieSet;
        }catch (SQLException e){
            throw new RuntimeException();
        }

    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
            Utilizator utilizator1 = entity.getUser1();
            Utilizator utilizator2 = entity.getUser2();
            LocalDateTime friends_from = entity.getFriendsSince();
            String utilizator1_firstname = utilizator1.getFirstName();
            String utilizator1_lastname  = utilizator1.getLastName();
            String utilizator1_username  = utilizator1.getUserName();
            String utilizator2_firstname = utilizator2.getFirstName();
            String utilizator2_lastname = utilizator2.getLastName();
            String utilizator2_username = utilizator2.getUserName();

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO prietenii (utilizator1_firstname,utilizator1_lastname," +
                                 "utilizator1_username, utilizator2_firstname,utilizator2_lastname," +
                                 "utilizator2_username, friends_from) VALUES (?, ?, ?, ?, ?, ?, ?)",
                         Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1,utilizator1_firstname);
                statement.setString(2,utilizator1_lastname);
                statement.setString(3, utilizator1_username);
                statement.setString(4, utilizator2_firstname );
                statement.setString(5, utilizator2_lastname);
                statement.setString(6, utilizator2_username);
                statement.setTimestamp(7, Timestamp.valueOf(friends_from));

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating friendship failed, no rows affected.");
                }

                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return Optional.of(entity);
                } else {
                    throw new SQLException("Creating friendship failed, no ID obtained.");
                }


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

    }

    @Override
    public Optional<Prietenie> delete(Tuple<Utilizator, Utilizator> utilizatoriTuple) {

        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement statement = connection.prepareStatement("DELETE FROM Prietenii where utilizator1_firstname = ? AND utilizator1_lastname = ? AND utilizator1_username = ? AND utilizator2_firstname = ? AND utilizator2_lastname = ? AND utilizator2_username = ? ");
        ){
            Utilizator utilizator1 = utilizatoriTuple.getLeft();
            Utilizator utilizator2 = utilizatoriTuple.getRight();
            statement.setString(1, utilizator1.getFirstName());
            statement.setString(2, utilizator1.getLastName());
            statement.setString(3, utilizator1.getUserName());
            statement.setString(4, utilizator2.getFirstName());
            statement.setString(5, utilizator2.getLastName());
            statement.setString(6, utilizator2.getUserName());
            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0 ){
                return Optional.of(new Prietenie(utilizator1, utilizator2, LocalDateTime.now()));
            }


        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        return Optional.empty();
    }

    @Override
    public void setReply(Prietenie entity, Prietenie entity2) {

    }


}
