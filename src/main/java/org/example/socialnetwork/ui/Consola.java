package org.example.socialnetwork.ui;
import org.example.socialnetwork.domain.Prietenie;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.service.ServiceException;
import org.example.socialnetwork.service.ServiceRetea;

import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Consola implements UI{

    private ServiceRetea service;

    private final Scanner scanner;


    public Consola(ServiceRetea service) {
        this.service = service;
        scanner = new Scanner(System.in);
    }

    @Override
    public void run() {

        boolean ok = true;
        while (ok) {
            printMenu();
            String option = scanner.nextLine();
            switch (option) {
                case "1" -> addUser();
                case "2" -> removeUser();
                case "3" -> printUsers();
                case "4" -> addFriendship();
                case "5" -> removeFriendship();
                case "6" -> printFriendships();
                case "7" -> getNumberOfCommunities();
                case "8" -> getMostSociableCommunity();
                case "9" -> getFriendsFromMonth();
                case "10" ->updateUser();
                case "0" -> ok = false;
                default -> System.out.println("Optiune invalida!");
            }
        }

    }

    @Override
    public void printMenu() {
        System.out.println("--- MENIU---");
        System.out.println("1) Adauga utilizator");
        System.out.println("2) Sterge utilizator");
        System.out.println("3) Afiseaza utilizatori");
        System.out.println("4) Adauga prietenie");
        System.out.println("5) Sterge prietenie");
        System.out.println("6) Afiseaza prietenii");
        System.out.println("7) Afiseaza numarul de comunitati");
        System.out.println("8) Afiseaza cea mai sociabila comunitate");
        System.out.println("9) Afiseaza prietenii utilizatorului dintr-o anumita luna");
        System.out.println("10) Modifica utilizator");
        System.out.println("0) Exit");
        System.out.println();
        System.out.print(">>>");

    }

    @Override
    public void printUsers() {
        Iterable<Utilizator> utilizatori = service.getAllUsers();
        for(Utilizator utilizator:utilizatori){
            System.out.println(utilizator);
        }
        System.out.println();
    }

    @Override
    public void printFriendships() {
        Iterable<Prietenie> prietenii = service.getAllFriendships();
        for(Prietenie prietenie: prietenii){
            System.out.println(prietenie);
        }


    }

    @Override
    public void addUser() {
        String firstName,lastName,userName,password;
        System.out.println("Introdu prenumele:");
        firstName = scanner.nextLine();
        System.out.println("Introdu numele:");
        lastName = scanner.nextLine();
        System.out.println("Introdu username:");
        userName = scanner.nextLine();
        System.out.println("Introdu parola:");
        password = scanner.nextLine();
        System.out.println();
        try {
            service.addUser(firstName, lastName, userName,password);
            System.out.println("Utilizator adaugat!");
        }
        catch (ServiceException | NoSuchAlgorithmException exception){
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void addFriendship() {
        String userName1,userName2;
        System.out.println("Introdu username1:");
        userName1 = scanner.nextLine();
        System.out.println("Introdu username2:");
        userName2 = scanner.nextLine();
        System.out.println();
        try {
            service.addFriendship(userName1, userName2);
            System.out.println("Prietenie adaugata!");
        } catch (ServiceException exception){
            System.out.println(exception.getMessage());
        }


    }

    @Override
    public void removeUser() {
        String userName;
        System.out.println("Introdu username:");
        userName = scanner.nextLine();
        System.out.println();
        try {
            service.deleteUser(userName);
            System.out.println("Utilizator sters!");
        }  catch (ServiceException exception){
            System.out.println(exception.getMessage());
        }


    }

    public void updateUser() {
        String userName,firstName,lastName;
        System.out.println("Introduceti username-ul utilizatorului pe care doriti sa il modificati:");
        userName = scanner.nextLine();
        System.out.println("Introduceti firstName nou:");
        firstName = scanner.nextLine();
        System.out.println("Introduceti lastName nou:");
        lastName = scanner.nextLine();
        System.out.println();
       // Utilizator utilizatorNou = new Utilizator(firstName,lastName,userName);
        try {
            service.updateUser(firstName,lastName,userName);
            System.out.println("Utilizator modificat!");
        }  catch (ServiceException exception){
            System.out.println(exception.getMessage());
        }


    }
    @Override
    public void removeFriendship() {
        String userName1,userName2;
        System.out.println("Introdu username1:");
        userName1 = scanner.nextLine();
        System.out.println("Introdu username2:");
        userName2 = scanner.nextLine();
        System.out.println();
        try {
            service.deleteFriendship(userName1, userName2);
            System.out.println("Prietenie stearsa!");
        } catch (ServiceException exception){
            System.out.println(exception.getMessage());
        }


    }

    @Override
    public void getNumberOfCommunities() {
        int numberOfCommmunities = service.getNumberOfCommunities();
        System.out.println("Numarul de comunitati:");
        System.out.println(numberOfCommmunities);
        System.out.println();

    }

    @Override
    public void getMostSociableCommunity() {
        Iterable<Utilizator> mostSociableCommunity= service.getMostSociableCommunity();
        if(mostSociableCommunity == null){
            System.out.println("Nu exista comunitati momentan!");
            return;
        }
        System.out.println("Cea mai sociabila comunitate este:");
        for (Utilizator utilizator : mostSociableCommunity) {
            System.out.printf(utilizator.getUserName() + ' ');
        }
        System.out.println();

    }
    public void getFriendsFromMonth(){
        String username,luna;
        System.out.println("Introdu username:");
        username = scanner.nextLine();
        System.out.println("Introdu luna:");
        luna = scanner.nextLine();
        Iterable<Prietenie> prietenii = service.getUsersFriendsFromMonth(username,luna);
        System.out.println("nume prieten | prenume prieten | data de la care sunt prieteni");
        for(Prietenie prietenie: prietenii){
            if(prietenie.getUser1().getUserName().equals(username)) {
                System.out.printf(prietenie.getUser2().getFirstName() + " | " + prietenie.getUser2().getLastName() + " | " + prietenie.getFriendsSince());
            }
            else if(prietenie.getUser2().getUserName().equals(username)){
                System.out.printf(prietenie.getUser1().getFirstName() + " | " + prietenie.getUser1().getLastName() + " | " + prietenie.getFriendsSince());
            }

        }
        System.out.println();
    }
}


