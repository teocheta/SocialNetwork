package org.example.socialnetwork;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.socialnetwork.domain.*;
import org.example.socialnetwork.domain.validators.*;
import org.example.socialnetwork.repository.Repository;
import org.example.socialnetwork.repository.db.*;
import org.example.socialnetwork.repository.inmemory.InMemoryRepository;
import org.example.socialnetwork.repository.paging.PagingRepository;
import org.example.socialnetwork.service.ServiceRetea;

import java.io.IOException;

public class StartApplication extends Application {

    Validator<Utilizator> utilizatorValidator;

    Validator<Prietenie> prietenieValidator;

    Validator<FriendRequest> friendRequestValidator;

    Validator<Message> messageValidator;

    PagingRepository<Long, Utilizator> utilizatoriRepository;

    PagingRepository<Tuple<Utilizator,Utilizator>, Prietenie> prieteniiRepository;

    PagingRepository<Tuple<Utilizator,Utilizator>,FriendRequest> friendRequestRepository;

    Repository<Long, Message> messageRepository;



    ServiceRetea serviceRetea;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        utilizatorValidator = new UtilizatorValidator();
        prietenieValidator = new PrietenieValidator();
        friendRequestValidator = new FriendRequestValidator();
        messageValidator = new MessageValidator();
        utilizatoriRepository = new RepositoryUtilizatoriDBPaging("jdbc:postgresql://localhost:5433/socialnetwork",
                "postgres", "Tudorrocky12", utilizatorValidator);
        prieteniiRepository = new RepositoryPrieteniiDBPaging("jdbc:postgresql://localhost:5433/socialnetwork",
        "postgres", "Tudorrocky12", prietenieValidator);
       // friendRequestRepository = new InMemoryRepository<>(friendRequestValidator);
        friendRequestRepository = new RepositoryCereriDBPaging("jdbc:postgresql://localhost:5433/socialnetwork",
                "postgres", "Tudorrocky12", friendRequestValidator);
     //   messageRepository = new InMemoryRepository<>((messageValidator));
        messageRepository = new RepositoryMesajeDB("jdbc:postgresql://localhost:5433/socialnetwork",
                "postgres", "Tudorrocky12", messageValidator);

        serviceRetea = new ServiceRetea(utilizatoriRepository,prieteniiRepository,friendRequestRepository,messageRepository,utilizatorValidator,prietenieValidator,friendRequestValidator,messageValidator);

        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.show();

    }

    private void initView(Stage primaryStage) throws IOException {
        /*
        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("user-view.fxml"));
        AnchorPane userLayout = userLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        UserController userController = userLoader.getController();
        userController.setService(serviceRetea);
         */

        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("login-view.fxml"));
        AnchorPane loginLayout = loginLoader.load();
        primaryStage.setScene(new Scene(loginLayout));

        LoginController loginController = loginLoader.getController();
        loginController.setService(serviceRetea);


    }
}
