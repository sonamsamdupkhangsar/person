package org.mycompany.db.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface PersonRepository extends ReactiveCrudRepository<Person, UUID> {
    //get a page of person with a custom query
    @Query("select * from Person")
    Flux<Person> findAll(Pageable pageable);
}
