package org.example.socialnetwork;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.socialnetwork.controller.*;
import org.example.socialnetwork.domain.FriendRequest;
import org.example.socialnetwork.domain.Message;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.events.ChangeEvent;
import org.example.socialnetwork.observer.Observer;
import org.example.socialnetwork.service.ServiceRetea;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<ChangeEvent> {
    ServiceRetea serviceRetea;

    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    Utilizator utilizator;

    @FXML
    TableView<Utilizator> tableView;

    @FXML
    TableColumn<Utilizator,Long> tableColumnId;

    @FXML
    TableColumn<Utilizator,String> tableColumnFirstName;

    @FXML
    TableColumn<Utilizator,String> tableColumnLastName;

    @FXML
    TableColumn<Utilizator,String> tableColumnUsername;

    public void setService(ServiceRetea serviceRetea,Utilizator utilizator){
        this.serviceRetea = serviceRetea;
        this.serviceRetea.addObserver(this);
        this.utilizator = utilizator;
        initModel();

    }
    // Method to highlight the row of the logged-in user
    private void highlightLoggedInUserRow() {
        if (utilizator != null) {
            // Iterate through the items and highlight the row with the logged-in user
            for (Utilizator user : tableView.getItems()) {
                if (user != null && user.equals(utilizator)) {
                    int index = tableView.getItems().indexOf(user);
                    tableView.requestFocus();
                    tableView.getSelectionModel().select(index);
                    tableView.getFocusModel().focus(index);

                    // Apply the CSS class to the selected row
                    tableView.setRowFactory(tv -> new TableRow<Utilizator>() {
                        @Override
                        protected void updateItem(Utilizator item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setStyle(""); // No styling for empty cells
                            } else {
                                // Apply the CSS class only to the selected row
                                if (item.equals(utilizator)) {
                                    getStyleClass().add("highlighted-row");
                                } else {
                                    getStyleClass().remove("highlighted-row");
                                }
                            }
                        }
                    });

                    break;
                }
            }
        }
    }




    public void initialize(){
        tableColumnId.setCellValueFactory(new PropertyValueFactory<Utilizator,Long>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("userName"));
        tableView.setItems(model);


    }

    private void initModel(){
        Iterable<Utilizator> utilizatori = serviceRetea.getAllUsers();
        List<Utilizator> utilizatoriLista = StreamSupport.stream(utilizatori.spliterator(),false)
                .collect(Collectors.toList());
        model.setAll(utilizatoriLista);
        highlightLoggedInUserRow();
    }

    public void handleDeleteUtilizator(ActionEvent actionEvent){
        Utilizator selected = (Utilizator) tableView.getSelectionModel().getSelectedItem();
        if(selected != null){
            Optional<Utilizator> deleted = serviceRetea.deleteUser(selected.getUserName());
            if(deleted.isPresent()){
                MessageAlert.showMessage(null,Alert.AlertType.INFORMATION,"Stergere utilizator","Utilizatorul a fost sters!");

            }
        }
        else {
            MessageAlert.showErrorMessage(null,"Nu a fost selectat niciun utilizator!");
        }
        initModel();

    }

    @FXML
    public void handleAddUtilizator(ActionEvent ev){
        showUserEditDialog(null);

    }

    private void showUserEditDialog(Utilizator utilizator) {
    try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("edituser-view.fxml"));

        AnchorPane root = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit User");
        dialogStage.initModality(Modality.WINDOW_MODAL);

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        EditUserController editUserController = loader.getController();
        editUserController.setService(serviceRetea,dialogStage,utilizator,model);

        dialogStage.show();

    } catch (IOException e) {

        e.printStackTrace();
    }

    }

    public void handleUpdateUtilizator(ActionEvent actionEvent) {
        Utilizator selected = (Utilizator) tableView.getSelectionModel().getSelectedItem();
        if(selected != null){
            showUserEditDialog(selected);
        }
        else {
            MessageAlert.showErrorMessage(null,"Nu a fost selectat niciun utilizator!");
        }


    }

    @Override
    public void update(ChangeEvent eveniment) {
        initModel();
    }

    public void handleShowPrietenii(ActionEvent actionEvent)  {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("friendship-view.fxml"));
            AnchorPane friendshipLayout = loader.load();

            Stage FriendshipsStage = new Stage();
            FriendshipsStage.setTitle("Prietenii");
            FriendshipsStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(friendshipLayout);
            FriendshipsStage.setScene(scene);

            FriendshipController friendshipController = loader.getController();
            friendshipController.setService(serviceRetea,utilizator);

            FriendshipsStage.show();


        }catch (IOException e){
            e.printStackTrace();
        }



    }

    public void handleSendFriendRequest(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("friendrequest-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend Request");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendRequestController friendRequestController = loader.getController();
            friendRequestController.setService(serviceRetea,dialogStage,utilizator);

            dialogStage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
    public void handleManageFriendRequest(ActionEvent actionEvent){

            Iterable<FriendRequest> cereri = serviceRetea.manageFriendRequests(utilizator.getUserName());
            if(cereri == null || !cereri.iterator().hasNext()){
                MessageAlert.showMessage(null,Alert.AlertType.INFORMATION,"Friend requests","Nu aveti nicio cerere de prietenie momentan!");

            }
            else {
                showFriendRequests(cereri);
            }
    }
        


    private void showFriendRequests(Iterable<FriendRequest> cereri) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("managerequests-view.fxml"));
            AnchorPane friendshipLayout = loader.load();

            Stage RequestsStage = new Stage();
            RequestsStage.setTitle("Friend Requests");
            RequestsStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(friendshipLayout);
            RequestsStage.setScene(scene);

            ManageRequestsController manageRequestsController = loader.getController();
            manageRequestsController.setService(serviceRetea,cereri);

            RequestsStage.show();


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleSendMessage(ActionEvent ev){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("sendmessage-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Send message");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            SendMessageController sendMessageController = loader.getController();
            sendMessageController.setService(serviceRetea,dialogStage,utilizator);

            dialogStage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void handleShowMessages(ActionEvent ev){
        Iterable<Message> messages = serviceRetea.getUsersMessages(utilizator.getUserName());

        if(messages == null || !messages.iterator().hasNext()){
            MessageAlert.showMessage(null,Alert.AlertType.INFORMATION,"Mesaje","Nu aveti niciun mesaj momentan!");

        }
        else {
            showMessages(messages);
        }

    }

    private void showMessages(Iterable<Message> messages) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("managemessages-view.fxml"));
            AnchorPane messagesLayout = loader.load();

            Stage MessagesStage = new Stage();
            MessagesStage.setTitle("Messages");
            MessagesStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(messagesLayout);
            MessagesStage.setScene(scene);

            ManageMessagesController manageMessagesController = loader.getController();
            manageMessagesController.setService(serviceRetea, messages, utilizator);

            MessagesStage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



