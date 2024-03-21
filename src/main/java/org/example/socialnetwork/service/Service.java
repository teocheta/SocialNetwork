package org.example.socialnetwork.service;
import org.example.socialnetwork.domain.Prietenie;
import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.events.ChangeEvent;
import org.example.socialnetwork.observer.Observable;
import org.example.socialnetwork.observer.Observer;


import java.security.NoSuchAlgorithmException;
import java.util.Optional;


public interface Service extends Observable<ChangeEvent> {
    /**
     * adauga un utilizator in repository
     * @param firstName - prenume utilizator
     * @param lastName - nume utilizator
     * @param userName - username utilizator
     * @throws ServiceException - daca exista un utilizator cu acelasi username sau utilizatorul introdus nu este valid
     */
    Optional<Utilizator> addUser(String firstName, String lastName, String userName,String password) throws NoSuchAlgorithmException;

    /**
     * sterge un utilizator si prietenii lui din repository
     *
     * @param userName - usernameul utilizatorului care se doreste a fi sters
     * @return utilizatorul sters
     * @throws ServiceException - daca utilizatorul cu acel username nu exista
     */
    Optional<Utilizator> deleteUser(String userName);



    /**
     * returneaza toti utilizatorii
     * @return toti utilizatorii din retea
     */
    Iterable<Utilizator> getAllUsers();

    /**
     * cauta un utilizator dupa username
     *
     * @param userName -usernameul utilizatorului cautat
     * @return utilizatorul cautat
     * null,daca acesta nu exista
     */
    Optional<Utilizator> getUserByUsername(String userName);

    /**
     * adauga o prietenie intre doi utilizatori in retea
     * @param userName1 - usernameul primului utilizator
     * @param userName2 -usernameul celui de-al doilea utilizator
     * @throws ServiceException - daca prietenia nu este valida sau daca aceasta exista deja
     */
    void addFriendship(String userName1,String userName2);

    /**
     * sterge o prietenie dintre doi utilizatori
     * @param userName1 - usersanemul primului utilizator
     * @param userName2 -usernameul celui de-al doilea utilizator
     * @throws ServiceException -daca nu exista acea prietenie
     */
    void deleteFriendship(String userName1,String userName2);

    /**
     * returneaza toate prieteniile
     * @return toate prieteniile din retea
     */
    Iterable<Prietenie> getAllFriendships();

    /**
     * returneaza toate comunitatile
     * @return toate comunitatile din retea
     */
    Iterable<Iterable<Utilizator>> getAllCommunities();

    /**
     * returneaza numarul de comunitati
     * @return numarul de comunitati
     */
    int getNumberOfCommunities();

    /**
     * returneaza cea mai sociabila comunitate
     * @return utilizatorii din cea mai sociabila comunitate
     */
    Iterable<Utilizator> getMostSociableCommunity();

    Iterable<Prietenie> getUsersFriendsFromMonth(String username,String luna);

    void addObserver(Observer<ChangeEvent> e);

    void removeObserver(Observer<ChangeEvent> e);

    void notifyObservers(ChangeEvent t);
}


