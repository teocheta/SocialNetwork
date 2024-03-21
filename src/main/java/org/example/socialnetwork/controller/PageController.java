package org.example.socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.example.socialnetwork.domain.FriendRequest;
import org.example.socialnetwork.domain.Message;
import org.example.socialnetwork.domain.Prietenie;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.repository.paging.Page;
import org.example.socialnetwork.service.ServiceRetea;

import java.util.List;
import java.util.stream.Collectors;

public class PageController {
    @FXML
    private ListView<String> listView;

    private ServiceRetea service;

    private int currentPageNumber = 1;

    private int pageSize;

    private String entity;

    @FXML
    public void setService(ServiceRetea service, int pageSize, String entity) {

        this.service = service;
        this.pageSize = pageSize;
        this.entity = entity;
        service.setPageSize(pageSize);
        if(entity.equals("utilizatori")){
            setUsers();
        } else if (entity.equals("prietenii")) {
            setFriendships();
        } else if (entity.equals("cereri")) {
            setFriendRequest();
        } else if (entity.equals("mesaje")) {
            setMessage(pageSize);
        }
    }

    private void setMessage(int pageSize) {
        List<Message> messageList = service.getLatestMessages(pageSize);
        listView.getItems().clear();
        for (Message message : messageList) {
            listView.getItems().add(message.getFrom() + " to " + message.getTo() + ": " + message.getMesaj() + " " + message.getData());
        }
    }


    public void setUsers(){
        Page<Utilizator> users = service.getUsersPage(currentPageNumber);
        List<Utilizator> userList = users.getContent().toList();
        listView.getItems().clear();
        for (Utilizator utilizator : userList) {
            listView.getItems().add(utilizator.getFirstName() + " " + utilizator.getLastName() + " " + utilizator.getUserName());
        }
    }

    public void setFriendships(){
        Page<Prietenie> friendships = service.getFriendshipPage(currentPageNumber);
        List<Prietenie> friendshipList = friendships.getContent().toList();
        listView.getItems().clear();
        for (Prietenie prietenie : friendshipList) {
            listView.getItems().add(prietenie.getUser1() + " " + prietenie.getUser2() + " " + prietenie.getFriendsSince());
        }
    }

    private void setFriendRequest() {
        Page<FriendRequest> friendRequests = service.getFriendRequestPage(currentPageNumber);
        List<FriendRequest> friendRequestList = friendRequests.getContent().toList();
        listView.getItems().clear();
        for(FriendRequest friendRequest: friendRequestList){
            listView.getItems().add(friendRequest.getUser1()+ " " + friendRequest.getUser2() + " " + friendRequest.getStatus());
        }
    }

    @FXML
    public void handleNext(ActionEvent actionEvent){
        currentPageNumber++;
        if(entity.equals("utilizatori")) setUsers();
        else if (entity.equals("prietenii")) setFriendships();
        else if (entity.equals("cereri")) setFriendRequest();
    }

    @FXML
    public void handlePrevious(ActionEvent actionEvent){
        if (currentPageNumber > 1) {
            currentPageNumber--;
            if(entity.equals("utilizatori")) setUsers();
            else if (entity.equals("prietenii")) setFriendships();
            else if (entity.equals("cereri")) setFriendRequest();
        }
    }




}
