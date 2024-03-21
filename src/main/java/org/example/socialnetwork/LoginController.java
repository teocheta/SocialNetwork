package org.example.socialnetwork;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.socialnetwork.controller.MessageAlert;
import org.example.socialnetwork.controller.PageController;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.service.Service;
import org.example.socialnetwork.service.ServiceException;
import org.example.socialnetwork.service.ServiceRetea;
import org.example.socialnetwork.service.StringHash;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldLastName;

    @FXML
    private TextField textFieldUsername;

    @FXML
    private TextField textFieldPassword;

    @FXML
    private TextField textFieldpageSize;

    @FXML
    private ComboBox<String> entityComboBox;

    private ServiceRetea serviceRetea;


    @FXML
    public void initialize() {

        entityComboBox.getItems().addAll("utilizatori", "prietenii", "cereri", "mesaje");
        entityComboBox.setValue("utilizatori");
    }

    public void setService(ServiceRetea serviceRetea){

        this.serviceRetea = serviceRetea;
    }

    public void handleLoginUtilizator(ActionEvent actionEvent) throws NoSuchAlgorithmException {

        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();
        Utilizator utilizator = new Utilizator(firstName, lastName, username, password);
        boolean exista = serviceRetea.exista(utilizator);
        if(!exista){
            MessageAlert.showErrorMessage(null,"Nu exista acest utilizator in retea!");
            return;
        }
        else {
            Utilizator u = serviceRetea.getUserByUsername(username).get();
            String encryptedPassword = StringHash.toHexString(StringHash.getSHA(password));
            if(encryptedPassword.equals(u.getPassword())) {
                try {

                    FXMLLoader userLoader = new FXMLLoader();
                    userLoader.setLocation(getClass().getResource("user-view.fxml"));
                    AnchorPane userLayout = userLoader.load();

                    Stage UtilizatoriStage = new Stage();
                    UtilizatoriStage.setTitle("Utilizatori");
                    UtilizatoriStage.initModality(Modality.WINDOW_MODAL);

                    Scene scene = new Scene(userLayout);
                    UtilizatoriStage.setScene(scene);

                    UserController userController = userLoader.getController();
                    userController.setService(serviceRetea, utilizator);
                    UtilizatoriStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                MessageAlert.showErrorMessage(null,"Parola incorecta!");

            }

        }

    }

    public void handleSignupUtilizator(ActionEvent actionEvent) {
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();
        try {
            Optional<Utilizator> utilizator = serviceRetea.addUser(firstName,lastName,username,password);
            if(utilizator.isEmpty()){
                MessageAlert.showErrorMessage(null,"Eroare la sign up!");
            }
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Sign up utilizator","Utilizatorul a fost adaugat!");


        }catch (ServiceException | NoSuchAlgorithmException e){
            MessageAlert.showErrorMessage(null,e.getMessage());

        }
    }

    @FXML
    private void handlePaging(ActionEvent actionEvent){

        int pageSize = Integer.parseInt(textFieldpageSize.getText());
        String entity = entityComboBox.getValue();

        try {

            FXMLLoader pagingLoader = new FXMLLoader();
            pagingLoader.setLocation(getClass().getResource("pagecontroller-view.fxml"));
            AnchorPane pagingLayout = pagingLoader.load();

            Stage PagingStage = new Stage();
            PagingStage.setTitle("Paginare");
            PagingStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(pagingLayout);
            PagingStage.setScene(scene);

            PageController pageController = pagingLoader.getController();
            pageController.setService(serviceRetea, pageSize, entity);
            PagingStage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
