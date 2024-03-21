package org.example.socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.socialnetwork.domain.Message;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.service.ServiceException;
import org.example.socialnetwork.service.ServiceRetea;

import java.util.Optional;

public class ReplyToMessageController {

    @FXML
    private TextField textFieldReply;

    private ServiceRetea serviceRetea;

    private Stage dialogStage;

    private Utilizator utilizator;

    private Message message;

    @FXML
    private void initialize(){

    }
    public void setService(ServiceRetea serviceRetea, Stage dialogStage, Utilizator utilizator, Message message ){
        this.serviceRetea = serviceRetea;
        this.dialogStage = dialogStage;
        this.utilizator = utilizator;
        this.message = message;
    }

    public void handleReplyToMessage(ActionEvent actionEvent) {
        String mesaj = textFieldReply.getText();
        try{
            Optional<Message> reply = serviceRetea.replyToMessage(message,mesaj,utilizator);
            if(reply.isEmpty()){
                dialogStage.close();
            }
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Message reply","Raspunsul a fost trimis!");

        }catch (ServiceException e){
            MessageAlert.showErrorMessage(null,e.getMessage());

        }

        dialogStage.close();
    }

    public void handleCancel(ActionEvent actionEvent) {
        dialogStage.close();
    }
}
