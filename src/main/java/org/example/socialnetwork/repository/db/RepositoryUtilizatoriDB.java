package org.example.socialnetwork.repository.db;

import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.domain.validators.Validator;
import org.example.socialnetwork.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class RepositoryUtilizatoriDB implements Repository<Long, Utilizator> {

    String url;
    String username;
    String password;
    Validator<Utilizator> validator;

    public RepositoryUtilizatoriDB(String url, String username, String password, Validator<Utilizator>validator){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    @Override
    public Optional<Utilizator> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");
        ){
            statement.setInt(1, Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userName = resultSet.getString("username");
                String password1 = resultSet.getString("password");
                Utilizator utilizator = new Utilizator(firstName, lastName,userName,password1);
                utilizator.setId(id);
                return Optional.of(utilizator);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }


        return  Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> utilizatoriSet = new HashSet<>();

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery();) {
            while (resultSet.next()) {
                Long id  = (Long) resultSet.getObject("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userName = resultSet.getString("username");
                String password1 = resultSet.getString("password");
                Utilizator utilizator = new Utilizator(firstName, lastName,userName,password1);
                utilizator.setId(id);
                utilizatoriSet.add(utilizator);

            }
            return utilizatoriSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();
        String userName = entity.getUserName();
        String password1 = entity.getPassword();
        try(Connection connection = DriverManager.getConnection(url,username,password);

            PreparedStatement statement = connection.prepareStatement
                    ("insert into users(first_name,last_name,username,password)values(?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, firstName);
            statement.setString(2,lastName);
            statement.setString(3,userName);
            statement.setString(4,password1);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getLong(1);
                entity.setId(id);
                return Optional.of(entity);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }

        }

        catch(SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Utilizator> delete(Long id) {
        Utilizator utilizator = findOne(id).get();

        try(Connection connection = DriverManager.getConnection(url,username,password)) {
            try (PreparedStatement deleteFriendshipsStatement =
                         connection.prepareStatement("DELETE FROM prietenii WHERE utilizator1_username = ? OR utilizator2_username = ?")) {
                deleteFriendshipsStatement.setString(1, utilizator.getUserName());
                deleteFriendshipsStatement.setString(2, utilizator.getUserName());
                deleteFriendshipsStatement.executeUpdate();
            }



       try( PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
           statement.setInt(1, Math.toIntExact(id));
           int rowsAffected = statement.executeUpdate();
           if (rowsAffected > 0) {
               return Optional.of(utilizator);
           } else {
               return Optional.empty();
           }

       }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    public Optional<Utilizator> update(Utilizator entity) {

        Long id = entity.getId();
        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();
        String userName = entity.getUserName();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE users SET first_name = ?, last_name = ?, username = ? WHERE id = ?",
                     Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, userName);
            statement.setInt(4, Math.toIntExact(id));

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }

            return findOne(id);



        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    @Override
    public void setReply(Utilizator entity, Utilizator entity2) {

    }


}

