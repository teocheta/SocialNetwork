package org.example.socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.socialnetwork.domain.Message;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.events.ChangeEvent;
import org.example.socialnetwork.observer.Observer;
import org.example.socialnetwork.service.ServiceRetea;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ManageMessagesController implements Observer<ChangeEvent> {

    ServiceRetea serviceRetea;

    Iterable<Message> messages;

    Utilizator utilizator;
    ObservableList<Message> model = FXCollections.observableArrayList();

    ObservableList<String> conversationModel = FXCollections.observableArrayList();

    @FXML
    TableView<Message> tableView;

    @FXML
    ListView<String> listView;

    @FXML
    TableColumn<Message, Utilizator> tableColumnFrom;

    @FXML
    TableColumn<Message, List<Utilizator>> tableColumnTo;

    @FXML
    TableColumn<Message, String> tableColumnMesaj;

    @FXML
    TableColumn<Message, LocalDateTime> tableColumnData;

    @FXML
    TableColumn<Message, String> tableColumnReply;

    @FXML
    TextField textFieldUsername;



    public void setService(ServiceRetea serviceRetea, Iterable<Message> messages, Utilizator utilizator) {
        this.serviceRetea = serviceRetea;
        this.messages = messages;
        this.utilizator = utilizator;
        this.serviceRetea.addObserver(this);
        initmodel(messages);
    }

    public void initialize(){
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<Message,Utilizator>("From"));
        tableColumnTo.setCellValueFactory(new PropertyValueFactory<Message,List<Utilizator>>("To"));
        tableColumnMesaj.setCellValueFactory(new PropertyValueFactory<Message,String>("Mesaj"));
        tableColumnData.setCellValueFactory(new PropertyValueFactory<Message,LocalDateTime>("Data"));
        tableColumnReply.setCellValueFactory(new PropertyValueFactory<Message,String>("Reply"));
        tableView.setItems(model);
    }

    private void initmodel(Iterable<Message> messages) {

        List<Message> messagesList = StreamSupport.stream(messages.spliterator(),false)
                .collect(Collectors.toList());
        model.setAll(messagesList);


    }

    public void handleReplyMessage(ActionEvent actionEvent) {
        Message selected = (Message) tableView.getSelectionModel().getSelectedItem();
        if(selected != null ){
            replyToMessage(selected);
        }
        else {
            MessageAlert.showErrorMessage(null,"Nu a fost selectat niciun mesaj!");
        }


    }

    private void replyToMessage(Message message){
        try {
            FXMLLoader loader = new FXMLLoader();
           // loader.setLocation(getClass().getResource("replytomessage-view.fxml"));
            loader.setLocation(getClass().getClassLoader().getResource("org/example/socialnetwork/replytomessage-view.fxml"));


            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Reply To Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ReplyToMessageController replyToMessageController = loader.getController();
            replyToMessageController.setService(serviceRetea,dialogStage,utilizator,message);

            dialogStage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    @Override
    public void update(ChangeEvent eveniment) {
        initmodel(messages);
    }



    public void handleShowConversation(ActionEvent actionEvent) {
        String username = textFieldUsername.getText();
        if(username.isEmpty()){
            MessageAlert.showErrorMessage(null,"Nu a fost introdus niciun username!");
        }
        else {
            List<String> conversation = serviceRetea.getConversation(utilizator.getUserName(), username);
            if(conversation.isEmpty()){
                MessageAlert.showErrorMessage(null,"Nu aveti mesaje cu acest utilizator!");
            }
            listView.getItems().clear();
            for (String mesaj : conversation) {
                listView.getItems().add(mesaj);
            }
        }
    }
}
