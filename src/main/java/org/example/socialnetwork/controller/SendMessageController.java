package org.example.socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.socialnetwork.domain.FriendRequest;
import org.example.socialnetwork.domain.Message;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.service.ServiceException;
import org.example.socialnetwork.service.ServiceRetea;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SendMessageController {

    @FXML
    private TextField textFieldMesaj;

    @FXML
    private TextField textFieldTo;

    private ServiceRetea serviceRetea;

    private Utilizator utilizator;

    Stage dialogStage;

    private void initialize(){

    }
    public void setService(ServiceRetea serviceRetea, Stage dialogStage, Utilizator utilizator) {
        this.serviceRetea = serviceRetea;
        this.dialogStage = dialogStage;
        this.utilizator = utilizator;

    }

    public void handleCancel(){
        dialogStage.close();
    }

    public void handleSendMessage(ActionEvent actionEvent) {
        String mesaj = textFieldMesaj.getText();
        String StringTo = textFieldTo.getText();
        String[] array = StringTo.split(",");
        List<Utilizator> To = new ArrayList<>();
        for(String username : array){
            Optional<Utilizator> utilizator1 = serviceRetea.getUserByUsername(username);
            if(utilizator1.isPresent()){
                Utilizator utilizator2 = utilizator1.get();
                To.add(utilizator2);
            }
        }
        /*
        if(To.isEmpty()){
            MessageAlert.showErrorMessage(null,"recievers list is empty!");
        }*/
        try {
            Optional<Message> message = serviceRetea.addMessage(utilizator,To,mesaj);
            if(mesaj.isEmpty()){
                dialogStage.close();
            }
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Trimitere mesaj","Mesajul a fost trimis!");


        } catch (ServiceException e){
            MessageAlert.showErrorMessage(null,e.getMessage());

        }
        dialogStage.close();
    }
}
