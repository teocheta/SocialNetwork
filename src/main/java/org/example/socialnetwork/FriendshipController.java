package org.example.socialnetwork;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.socialnetwork.controller.MessageAlert;
import org.example.socialnetwork.domain.Prietenie;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.events.ChangeEvent;
import org.example.socialnetwork.observer.Observer;
import org.example.socialnetwork.service.ServiceRetea;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipController implements Observer<ChangeEvent> {

    ServiceRetea serviceRetea;

    Utilizator utilizator;
    ObservableList<Prietenie> model = FXCollections.observableArrayList();

    @FXML
    TableView<Prietenie> tableView;

    @FXML
    TableColumn<Prietenie,Utilizator> tableColumnUser1;
    @FXML
    TableColumn<Prietenie,Utilizator> tableColumnUser2;
    @FXML
    TableColumn<Prietenie, LocalDateTime> tableColumnFriendsSince;

    public void setService(ServiceRetea serviceRetea,Utilizator utilizator){
        this.serviceRetea = serviceRetea;
        this.serviceRetea.addObserver(this);
        this.utilizator = utilizator;
        initModel();

    }

    public void initialize(){
        tableColumnUser1.setCellValueFactory(new PropertyValueFactory<Prietenie, Utilizator>("User1"));
        tableColumnUser2.setCellValueFactory(new PropertyValueFactory<Prietenie, Utilizator>("User2"));
        tableColumnFriendsSince.setCellValueFactory(new PropertyValueFactory<Prietenie,LocalDateTime>("friendsSince"));
        tableView.setItems(model);
    }


    private void initModel() {
        Iterable<Prietenie> prietenii = serviceRetea.getAllFriendships();
        List<Prietenie> prietenieLista = StreamSupport.stream(prietenii.spliterator(),false)
                .filter(prietenie -> prietenie.getUser1().equals(utilizator) || prietenie.getUser2().equals(utilizator))
                .collect(Collectors.toList());
        model.setAll(prietenieLista);
    }

    @Override
    public void update(ChangeEvent eveniment) {
        initModel();
    }

    public void handleDeletePrietenie(ActionEvent actionEvent){
        Prietenie selected = (Prietenie) tableView.getSelectionModel().getSelectedItem();
        if(selected != null){
            serviceRetea.deleteFriendship(selected.getUser1().getUserName(),selected.getUser2().getUserName());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Stergere prietenie","Prietenia a fost stearsa!");


        }
        else {
            MessageAlert.showErrorMessage(null,"Nu a fost selectata nicio prietenie!");
        }
        initModel();

    }
}
