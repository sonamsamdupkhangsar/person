package org.mycompany;

import org.mycompany.db.repo.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PersonFinder {
    Mono<Person> getById(UUID id);
    Mono<Person> save(String firstName, String lastName);
    Mono<Person> update(UUID id, String firstName, String lastName);
    Mono<Page<Person>> getPage(Pageable pageable);
}
