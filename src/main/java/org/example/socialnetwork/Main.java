package org.example.socialnetwork;

import org.example.socialnetwork.domain.*;
import org.example.socialnetwork.domain.validators.*;
import org.example.socialnetwork.repository.Repository;
import org.example.socialnetwork.repository.db.*;
import org.example.socialnetwork.repository.inmemory.InMemoryRepository;
import org.example.socialnetwork.repository.paging.PagingRepository;
import org.example.socialnetwork.service.ServiceRetea;
import org.example.socialnetwork.ui.Consola;

public class Main {

    public static void main(String[] args) {
        Validator<Utilizator> utilizatorValidator = new UtilizatorValidator();
        Validator<Prietenie> prietenieValidator = new PrietenieValidator();
        Validator<FriendRequest> friendRequestValidator = new FriendRequestValidator();
        Repository<Long,Utilizator> utilizatorRepository = new InMemoryRepository<>(utilizatorValidator);

        PagingRepository<Long,Utilizator> utilizatorRepositoryDB = new RepositoryUtilizatoriDBPaging("jdbc:postgresql://localhost:5433/socialnetwork",
                "postgres", "Tudorrocky12", utilizatorValidator);

        Repository<Tuple<Utilizator,Utilizator>,Prietenie> prietenieRepository = new InMemoryRepository<>(prietenieValidator);

        PagingRepository<Tuple<Utilizator,Utilizator>,Prietenie> prietenieRepositoryDB = new RepositoryPrieteniiDBPaging("jdbc:postgresql://localhost:5433/socialnetwork",
                "postgres", "Tudorrocky12", prietenieValidator);

       // Repository<Tuple<Utilizator, Utilizator>, FriendRequest> cereriRepository = new InMemoryRepository<>(friendRequestValidator);
        Validator<Message> messageValidator = new MessageValidator();
        PagingRepository<Tuple<Utilizator, Utilizator>, FriendRequest> cereriRepository = new RepositoryCereriDBPaging("jdbc:postgresql://localhost:5433/socialnetwork",
                "postgres", "Tudorrocky12", friendRequestValidator);
       // Repository<Long, Message> messageRepository = new InMemoryRepository<>(messageValidator);
        Repository<Long, Message> messageRepository = new RepositoryMesajeDB("jdbc:postgresql://localhost:5433/socialnetwork",
                "postgres", "Tudorrocky12",messageValidator);
        ServiceRetea service = new ServiceRetea(utilizatorRepositoryDB,prietenieRepositoryDB,cereriRepository,messageRepository,utilizatorValidator,prietenieValidator,friendRequestValidator,messageValidator);

        Consola consola = new Consola(service);
        consola.run();

    }
}