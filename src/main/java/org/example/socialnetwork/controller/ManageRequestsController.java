package org.example.socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.socialnetwork.domain.FriendRequest;
import org.example.socialnetwork.domain.FriendRequestStatus;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.events.ChangeEvent;
import org.example.socialnetwork.observer.Observer;
import org.example.socialnetwork.service.ServiceRetea;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ManageRequestsController implements Observer<ChangeEvent> {
    ServiceRetea serviceRetea;

    Iterable<FriendRequest> cereri;

    ObservableList<FriendRequest> model = FXCollections.observableArrayList();

    @FXML
    TableView<FriendRequest> tableView;

    @FXML
    TableColumn<FriendRequest, Utilizator> tableColumnUser1;
    @FXML
    TableColumn<FriendRequest, Utilizator> tableColumnUser2;
    @FXML
    TableColumn<FriendRequest, FriendRequestStatus> tableColumnStatus;

    public void setService(ServiceRetea serviceRetea, Iterable<FriendRequest> cereri) {
        this.serviceRetea = serviceRetea;
        this.cereri = cereri;
        this.serviceRetea.addObserver(this);
        initModel(cereri);

    }
    public void initialize(){
        tableColumnUser1.setCellValueFactory(new PropertyValueFactory<FriendRequest, Utilizator>("User1"));
        tableColumnUser2.setCellValueFactory(new PropertyValueFactory<FriendRequest, Utilizator>("User2"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<FriendRequest, FriendRequestStatus>("Status"));
        tableView.setItems(model);
    }


    private void initModel(Iterable<FriendRequest> cereri) {
        List<FriendRequest> cereriLista = StreamSupport.stream(cereri.spliterator(),false)
                .collect(Collectors.toList());
        model.setAll(cereriLista);
    }


    public void handleAcceptFriendRequest(ActionEvent actionEvent) {
        FriendRequest selected = (FriendRequest) tableView.getSelectionModel().getSelectedItem();
        if(selected != null){
            if(!selected.getStatus().equals(FriendRequestStatus.PENDING)){
                MessageAlert.showErrorMessage(null,"Cererea a fost deja gestioanata!");
                return;
            }
            Optional<FriendRequest> accepted = serviceRetea.manageFriendRequest(selected,FriendRequestStatus.APPROVED);
            if(accepted.isPresent()){
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Manage friend request","Cererea a fost acceptata!");

            }
        }
        else {
            MessageAlert.showErrorMessage(null,"Nu a fost selectata nicio cerere!");
        }
    }

    public void handleRejectFriendRequest(ActionEvent actionEvent) {
        FriendRequest selected = (FriendRequest) tableView.getSelectionModel().getSelectedItem();
        if(selected != null){
            if(!selected.getStatus().equals(FriendRequestStatus.PENDING)){
                MessageAlert.showErrorMessage(null,"Cererea a fost deja gestionata!");
                return;
            }
            Optional<FriendRequest> rejected = serviceRetea.manageFriendRequest(selected,FriendRequestStatus.REJECTED);
            if(rejected.isPresent()){
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Manage friend request","Cererea a fost respinsa!");

            }
        }
        else {
            MessageAlert.showErrorMessage(null,"Nu a fost selectata nicio cerere!");
        }

    }


    @Override
    public void update(ChangeEvent eveniment) {
        initModel(cereri);
    }
}
