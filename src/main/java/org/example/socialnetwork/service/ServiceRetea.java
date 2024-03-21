package org.example.socialnetwork.service;

import javafx.collections.ObservableList;
import org.example.socialnetwork.domain.*;
import org.example.socialnetwork.domain.validators.ValidationException;
import org.example.socialnetwork.domain.validators.Validator;
import org.example.socialnetwork.events.ChangeEvent;
import org.example.socialnetwork.observer.Observer;
import org.example.socialnetwork.repository.Repository;
import org.example.socialnetwork.repository.paging.Page;
import org.example.socialnetwork.repository.paging.Pageable;
import org.example.socialnetwork.repository.paging.PageableImplementation;
import org.example.socialnetwork.repository.paging.PagingRepository;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.example.socialnetwork.events.ChangeEventType.*;

public class ServiceRetea implements Service {

    private PagingRepository<Long, Utilizator> utilizatoriRepo;

    private PagingRepository<Tuple<Utilizator,Utilizator>, Prietenie> prietenieRepo;

    private PagingRepository<Tuple<Utilizator,Utilizator>, FriendRequest> cereriRepo;

    private  Repository<Long,Message> messageRepo;

    private Validator<Utilizator> utilizatorValidator;

    private Validator<Prietenie> prietenieValidator;

    private Validator<FriendRequest> friendRequestValidator;

    private Validator<Message> messageValidator;

    private int pageSize;

    public ServiceRetea(PagingRepository<Long, Utilizator> utilizatoriRepo, PagingRepository<Tuple<Utilizator, Utilizator>,
            Prietenie> prietenieRepo,PagingRepository<Tuple<Utilizator,Utilizator>,FriendRequest> cereriRepo,Repository<Long,Message> messageRepo,
                        Validator<Utilizator> utilizatorValidator,Validator<Prietenie> prietenieValidator,
                        Validator<FriendRequest> friendRequestValidator,Validator<Message> messageValidator) {
        this.utilizatoriRepo = utilizatoriRepo;
        this.prietenieRepo = prietenieRepo;
        this.cereriRepo = cereriRepo;
        this.messageRepo = messageRepo;
        this.utilizatorValidator = utilizatorValidator;
        this.prietenieValidator = prietenieValidator;
        this.friendRequestValidator = friendRequestValidator;
        this.messageValidator = messageValidator;
    }
    @Override
    public Optional<Utilizator> addUser(String firstName, String lastName, String userName,String passsword) throws NoSuchAlgorithmException {
        String encryptedPassword = StringHash.toHexString(StringHash.getSHA(passsword));
        Utilizator utilizator = new Utilizator(firstName,lastName,userName,encryptedPassword);
        try {
            utilizatorValidator.validate(utilizator);
        } catch (ValidationException exception){
            throw new ServiceException(exception.getMessage());
        }
        if(exista(utilizator)){
            throw new ServiceException("Exista deja utilizator cu acelasi username!");
        }
        notifyObservers(new ChangeEvent(ADD,utilizator));
        return utilizatoriRepo.save(utilizator);

    }
    public boolean exista(Utilizator utilizator){
        return getUserByUsername(utilizator.getUserName()).isPresent();
    }

    @Override
    public Optional<Utilizator> deleteUser(String userName) {
        Optional<Utilizator> utilizator = getUserByUsername(userName);
        if(utilizator.isEmpty()){
            throw new ServiceException("Utilizator inexistent!");
        }
        /*
        List<Prietenie> prietenii_de_sters = new ArrayList<>();
        StreamSupport.stream(prietenieRepo.findAll().spliterator(),false)
                .filter(prietenie -> prietenie.getUser1().equals(utilizator) || prietenie.getUser2().equals(utilizator))
                .forEach(prietenii_de_sters::add);
        prietenii_de_sters.forEach(prietenie -> prietenieRepo.delete(prietenie.getId()));

         */
        notifyObservers(new ChangeEvent(DELETE,utilizator.get()));
        return utilizatoriRepo.delete(utilizator.get().getId());
    }

    public Optional<Utilizator> updateUser(String firstName,String lastName,String username){
        Optional<Utilizator> utilizatorVechi = getUserByUsername(username);
        Long id = utilizatorVechi.get().getId();
        String password = utilizatorVechi.get().getPassword();
        Utilizator utilizatorNou = new Utilizator(firstName,lastName,username,password);
        utilizatorNou.setId(id);
        try{
            utilizatorValidator.validate(utilizatorNou);
        }
        catch (ValidationException e){
            throw new ServiceException(e.getMessage());
        }
        notifyObservers(new ChangeEvent(UPDATE, utilizatorNou, utilizatorVechi.get()));
        return utilizatoriRepo.update(utilizatorNou);
    }

    @Override
    public Iterable<Utilizator> getAllUsers() {

        return utilizatoriRepo.findAll();
    }

    @Override
    public Optional<Utilizator> getUserByUsername(String userName) {
        return StreamSupport.stream(utilizatoriRepo.findAll().spliterator(),false)
                .filter(utilizator -> utilizator.getUserName().equals(userName)).findFirst();
    }

    @Override
    public void addFriendship(String userName1,String userName2) {
        Utilizator utilizator1 = getUserByUsername(userName1).get();
        Utilizator utilizator2 = getUserByUsername(userName2).get();
        LocalDateTime friendsFrom = LocalDateTime.now();
        Prietenie prietenie = new Prietenie(utilizator1,utilizator2,friendsFrom);
        try{
            prietenieValidator.validate(prietenie);
        } catch (ValidationException exception){
            throw new ServiceException(exception.getMessage());
        }
        if(exista_prietenie(prietenie)){
            throw new ServiceException("Prietenie deja existenta!");
        }
        utilizator1.addFriend(utilizator2);
        utilizator2.addFriend(utilizator1);
        prietenieRepo.save(prietenie);
    }
    public boolean exista_prietenie(Prietenie prietenie) {
        return StreamSupport.stream(prietenieRepo.findAll().spliterator(),false)
                .anyMatch(prietenie1 -> prietenie1.getUser1().equals(prietenie.getUser1()) &&
                        prietenie1.getUser2().equals(prietenie.getUser2()) ||
                        prietenie1.getUser1().equals(prietenie.getUser2()) &&
                                prietenie1.getUser2().equals(prietenie.getUser1()));
    }

    @Override
    public void deleteFriendship(String userName1,String userName2) {
        Utilizator utilizator1 = getUserByUsername(userName1).get();
        Utilizator utilizator2 = getUserByUsername(userName2).get();
        Optional<Prietenie> prietenie_de_sters = StreamSupport.stream(prietenieRepo.findAll().spliterator(),false)
                .filter(prietenie -> (prietenie.getUser1().equals(utilizator1) && prietenie.getUser2().equals(utilizator2))
                        || (prietenie.getUser1().equals(utilizator2) && prietenie.getUser2().equals(utilizator1)))
                .findFirst();
        if(prietenie_de_sters.isEmpty()){
            throw new ServiceException("Prietenie inexistenta!");
        }
        utilizator1.deleteFriend(utilizator2);
        utilizator2.deleteFriend(utilizator1);
        notifyObservers(new ChangeEvent(DELETE,prietenie_de_sters));
        prietenieRepo.delete(prietenie_de_sters.get().getId());

    }

    @Override
    public Iterable<Prietenie> getAllFriendships() {
        return prietenieRepo.findAll();
    }
    private void dfs(Utilizator utilizator, Set<Utilizator> vizitat, Set<Utilizator> communitate) {
        vizitat.add(utilizator);
        communitate.add(utilizator);
        utilizator.getFriends().stream()
                .filter(prieten-> !vizitat.contains(prieten))
                .forEach(prieten->{
                    dfs(prieten,vizitat,communitate);
                });


    }
    @Override
    public Iterable<Iterable<Utilizator>> getAllCommunities() {
        Set<Iterable<Utilizator>> toate_comunitatile = new HashSet<>();
        Set<Utilizator> vizitat = new HashSet<>();
        StreamSupport.stream(utilizatoriRepo.findAll().spliterator(),false)
                .filter(utilizator -> !vizitat.contains(utilizator))
                .forEach(utilizator -> {
                    Set<Utilizator> comunitate = new HashSet<>();
                    dfs(utilizator,vizitat,comunitate);
                    toate_comunitatile.add(comunitate);
                });
        return toate_comunitatile;
    }

    @Override
    public int getNumberOfCommunities() {

        Set<Utilizator> vizitat = new HashSet<>();
        AtomicInteger nr_comunitati = new AtomicInteger();
        StreamSupport.stream(utilizatoriRepo.findAll().spliterator(), false)
                .filter(utilizator -> !vizitat.contains(utilizator))
                .forEach(utilizator -> {
                    nr_comunitati.getAndIncrement();
                    Set<Utilizator> comunitate = new HashSet<>();
                    dfs(utilizator,vizitat,comunitate);
                });
        return nr_comunitati.get();

    }

    @Override
    public Iterable<Utilizator> getMostSociableCommunity() {
        return StreamSupport
                .stream(getAllCommunities().spliterator(),false)
                .max(Comparator.comparingInt(communitate ->
                        StreamSupport.stream(communitate.spliterator(), false)
                                .mapToInt(utilizator -> utilizator.getFriends().size())
                                .sum()
                ))
                .orElse(null);


    }

    public Iterable<Prietenie> getUsersFriendsFromMonth(String username,String luna){
        return StreamSupport
                .stream(prietenieRepo.findAll().spliterator(),false)
                .filter(prietenie -> prietenie.getFriendsSince().getMonthValue() == Integer.parseInt(luna) &&
                        (prietenie.getUser1().getUserName().equals(username) ||
                        prietenie.getUser2().getUserName().equals(username)))
                .collect(Collectors.toList());
    }

    @Override
    public void notifyObservers(ChangeEvent t) {
            observers.stream().forEach(x->x.update(t));
    }

    private List<Observer<ChangeEvent>> observers = new ArrayList<>();



    @Override
    public void addObserver(Observer<ChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<ChangeEvent> e) {
       observers.remove(e);

    }



    public Optional<FriendRequest> addFriendRequest(String username1, String username2) {
        Utilizator utilizator1 = getUserByUsername(username1).get();
        Utilizator utilizator2 = getUserByUsername(username2).get();
        FriendRequest friendRequest = new FriendRequest(utilizator1,utilizator2, FriendRequestStatus.PENDING);
        try {
            friendRequestValidator.validate(friendRequest);
        } catch (ValidationException e){
            throw new ServiceException(e.getMessage());

        }
        if(exista_prietenie(new Prietenie(utilizator1,utilizator2, LocalDateTime.now())))
        {
            throw new ServiceException("Prietenie deja existenta!");
        }

        return cereriRepo.save(friendRequest);
    }

    public Iterable<FriendRequest> getAllRequests() {
        return cereriRepo.findAll();
    }

    public Iterable<FriendRequest> manageFriendRequests(String username){
        Utilizator utilizator = getUserByUsername(username).get();
        Iterable<FriendRequest> toateCererile = getAllRequests();
        if (toateCererile == null || !toateCererile.iterator().hasNext()) {
            return Collections.emptyList();
        }
        return StreamSupport.stream(toateCererile.spliterator(),false)
                .filter(cerere->cerere.getUser2().equals(utilizator))
                .collect(Collectors.toList());
    }

    public Optional<FriendRequest> manageFriendRequest(FriendRequest cerere,FriendRequestStatus status) {
        Utilizator utilizator1 = cerere.getUser1();
        Utilizator utilizator2 = cerere.getUser2();
        if(status.equals(FriendRequestStatus.APPROVED)){
            notifyObservers(new ChangeEvent(ACCEPT,cerere));
            addFriendship(utilizator1.getUserName(),utilizator2.getUserName());

        } else if (status.equals(FriendRequestStatus.REJECTED)) {
            notifyObservers(new ChangeEvent(REJECT,cerere));

        }
        cerere.setStatus(status);
        cereriRepo.update(cerere);
        return Optional.of(cerere);
    }


    public Optional<Message> addMessage(Utilizator utilizator, List<Utilizator> to, String mesaj) {
        Message message = new Message(utilizator,to,mesaj,LocalDateTime.now());
        try{
            messageValidator.validate(message);
        }catch(ValidationException e){
            throw new ServiceException(e.getMessage());
        }
       return messageRepo.save(message);
    }

    public Iterable<Message> getAllMessages(){
       return messageRepo.findAll();
    }
    public Iterable<Message> getUsersMessages(String username) {
        Utilizator utilizator = getUserByUsername(username).get();
        Iterable<Message> toateMesajele = getAllMessages();
        if (toateMesajele == null || !toateMesajele.iterator().hasNext()) {
            return Collections.emptyList();
        }
        return StreamSupport.stream(toateMesajele.spliterator(),false)
                .filter(mesaj->mesaj.getTo().contains(utilizator))
                .collect(Collectors.toList());
    }

    public Optional<Message> replyToMessage(Message message, String mesaj, Utilizator utilizator) {
        List<Utilizator> to = new ArrayList<>();
        System.out.println(message.getFrom());
        to.add(message.getFrom());
        Message newMessage = new Message(utilizator,to,mesaj,LocalDateTime.now());
        try{
            messageValidator.validate(newMessage);
        }catch (ValidationException e){
            throw new ServiceException(e.getMessage());
        }

        Optional<Message> reply =  messageRepo.save(newMessage);
        messageRepo.setReply(message,newMessage);
        notifyObservers(new ChangeEvent(REPLY,newMessage));
        message.setReply(reply.get());
        return reply;

    }


    public List<String> getConversation(String username1, String username2) {

        Utilizator utilizator1 = getUserByUsername(username1).get();
        Utilizator utilizator2 = getUserByUsername(username2).get();
        Iterable<Message> allMessages = getAllMessages();
        List<Message> sortedMessages = StreamSupport.stream(allMessages.spliterator(),false)
                .collect(Collectors.toList());
        sortedMessages.sort(Comparator.comparing(Message::getData));


        List<String> conversation = new ArrayList<>();
        for(Message message:sortedMessages){
            if( (message.getFrom().equals(utilizator1) && message.getTo().contains(utilizator2))
            || (message.getFrom().equals(utilizator2) && message.getTo().contains(utilizator1)) ){
                conversation.add(message.getMesaj());
            }
        }
        conversation.forEach(System.out::println);

        return conversation;
    }

    public Page<Utilizator> getUsersPage(int pageNumber){
        Pageable pageable = new PageableImplementation(pageNumber, pageSize);
        return utilizatoriRepo.findAll(pageable);
    }

    public void setPageSize(int pageSize){
        this.pageSize = pageSize;
    }

    public Page<Prietenie> getFriendshipPage(int pageNumber){
        Pageable pageable = new PageableImplementation(pageNumber, pageSize);
        return prietenieRepo.findAll(pageable);
    }

    public Page<FriendRequest> getFriendRequestPage(int pageNumber){
        Pageable pageable = new PageableImplementation(pageNumber, pageSize);
        return cereriRepo.findAll(pageable);
    }

    public List<Message> getLatestMessages(int nr) {
        Iterable<Message> allMessages = getAllMessages();
        List<Message> messages = new ArrayList<Message>();
        for(Message message : allMessages){
            messages.add(message);
        }
        //   int startIndex = messages.size() - nr;
        //  List<Message> latestMessages = messages.subList(startIndex, messages.size());
        return messages;
    }
}

