package org.example.socialnetwork.repository.paging;

import org.example.socialnetwork.domain.Entity;
import org.example.socialnetwork.repository.Repository;

public interface PagingRepository<ID,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    //returneaza pagina
    Page<E> findAll(Pageable pageable);
}
