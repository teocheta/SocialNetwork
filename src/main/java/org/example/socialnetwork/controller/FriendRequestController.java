package org.example.socialnetwork.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.socialnetwork.domain.FriendRequest;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.service.ServiceException;
import org.example.socialnetwork.service.ServiceRetea;

import java.util.Optional;

public class FriendRequestController {

    @FXML
    private TextField textFieldUsername;


    private ServiceRetea serviceRetea;

    private Utilizator utilizator;

    Stage dialogStage;

    private void initialize(){

    }

    public void setService(ServiceRetea serviceRetea, Stage dialogStage,Utilizator utilizator){
        this.serviceRetea = serviceRetea;
        this.utilizator = utilizator;
        this.dialogStage = dialogStage;

    }

    public void handleCancel(){
        dialogStage.close();
    }

    public void handleSendRequest(ActionEvent actionEvent) {
        String username = textFieldUsername.getText();
       // String username2 = textFieldUsername2.getText();
        try{
            Optional<FriendRequest> friendRequest = serviceRetea.addFriendRequest(utilizator.getUserName(),username);
            if(friendRequest.isEmpty()){
                dialogStage.close();
            }
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Trimitere cerere","Cererea a fost trimisa!");


        }catch (ServiceException e){
            MessageAlert.showErrorMessage(null,e.getMessage());

        }
        dialogStage.close();
    }
}
