package org.example.socialnetwork.repository.db;

import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.domain.validators.Validator;
import org.example.socialnetwork.repository.paging.Page;
import org.example.socialnetwork.repository.paging.PageImplementation;
import org.example.socialnetwork.repository.paging.Pageable;
import org.example.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class RepositoryUtilizatoriDBPaging extends RepositoryUtilizatoriDB implements PagingRepository
<Long, Utilizator>{

    public RepositoryUtilizatoriDBPaging(String url, String username, String password, Validator<Utilizator> validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<Utilizator> findAll(Pageable pageable) {

        Set<Utilizator> utilizatori = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ?");

        ) {
            statement.setInt(1,pageable.getPageSize());
            statement.setInt(2,pageable.getPageSize()* (pageable.getPageNumber()-1));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String userName = resultSet.getString("username");
                String password1 = resultSet.getString("password");
                Utilizator utilizator=new Utilizator(firstName,lastName,userName,password1);
                utilizator.setId(id);
                utilizatori.add(utilizator);

            }
            return new PageImplementation<Utilizator>(pageable,utilizatori.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
