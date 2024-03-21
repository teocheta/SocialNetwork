package org.example.socialnetwork.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.events.ChangeEvent;
import org.example.socialnetwork.observer.Observer;
import org.example.socialnetwork.service.ServiceException;
import org.example.socialnetwork.service.ServiceRetea;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EditUserController implements Observer<ChangeEvent> {


    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldLastName;

    @FXML
    private TextField textFieldUsername;


    @FXML
    private TextField textFieldPassword;


    private ServiceRetea serviceRetea;

    Stage dialogStage;

    Utilizator utilizator;

    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    private void initialize(){

    }

    private void initModel(){
        Iterable<Utilizator> utilizatori = serviceRetea.getAllUsers();
        List<Utilizator> utilizatoriLista = StreamSupport.stream(utilizatori.spliterator(),false)
                .collect(Collectors.toList());
        model.setAll(utilizatoriLista);
    }
    public void setService(ServiceRetea serviceRetea,Stage dialogStage,Utilizator utilizator,ObservableList<Utilizator> model){
        this.serviceRetea = serviceRetea;
        this.dialogStage = dialogStage;
        this.utilizator = utilizator;
        this.model = model;
        if(utilizator!= null){
            setFields(utilizator);
        }
    }

    private void setFields(Utilizator utilizator) {

        textFieldFirstName.setText(utilizator.getFirstName());
        textFieldLastName.setText(utilizator.getLastName());
        textFieldUsername.setText(utilizator.getUserName());


    }
    private void clearFields(){
        textFieldFirstName.setText("");
        textFieldLastName.setText("");
        textFieldUsername.setText("");
    }
    @FXML
    public void handleCancel(){
        dialogStage.close();
    }

    public void handleAddUtilizator(){

        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();
        Utilizator utilizator = new Utilizator(firstName,lastName,username,password);
        if(this.utilizator == null) {
            addUtilizator(firstName, lastName, username,password);
        }
        else {
            updateUtilizator(utilizator);
        }
    }


    private void addUtilizator(String firstName,String lastName,String username,String password) {
        try{
           Optional<Utilizator> utilizator = serviceRetea.addUser(firstName,lastName,username,password);
           if(utilizator.isEmpty()){
               dialogStage.close();
           }
           MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Adaugare utilizator","Utilizatorul a fost adaugat!");


        }catch (ServiceException | NoSuchAlgorithmException e){
            MessageAlert.showErrorMessage(null,e.getMessage());

        }
        initModel();
        dialogStage.close();
    }

    private void updateUtilizator(Utilizator utilizator){
        try {
            Optional<Utilizator> u = serviceRetea.updateUser(utilizator.getFirstName(), utilizator.getLastName(), utilizator.getUserName());
            if (u.isEmpty())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Modificare utilizator", "Utilizator modificat");
        } catch (ServiceException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        initModel();
        dialogStage.close();

    }


    @Override
    public void update(ChangeEvent eveniment) {

    }
}
