package org.example.socialnetwork.repository.db;

import org.example.socialnetwork.domain.FriendRequest;
import org.example.socialnetwork.domain.FriendRequestStatus;
import org.example.socialnetwork.domain.Tuple;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.domain.validators.Validator;
import org.example.socialnetwork.repository.paging.Page;
import org.example.socialnetwork.repository.paging.PageImplementation;
import org.example.socialnetwork.repository.paging.Pageable;
import org.example.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class RepositoryCereriDBPaging extends RepositoryCereriDB
        implements PagingRepository<Tuple<Utilizator,Utilizator>, FriendRequest> {
    public RepositoryCereriDBPaging(String url, String username, String password, Validator<FriendRequest> validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<FriendRequest> findAll(Pageable pageable) {
        Set<FriendRequest> cereriSet = new HashSet<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from cereri limit ? offset ?");
            )
        {
            statement.setInt(1,pageable.getPageSize());
            statement.setInt(2,pageable.getPageSize()* (pageable.getPageNumber()-1));
            ResultSet resultSet = statement.executeQuery();
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
            return new PageImplementation<FriendRequest>(pageable,cereriSet.stream());
        }catch (SQLException e){
            throw new RuntimeException();
        }
    }
}
