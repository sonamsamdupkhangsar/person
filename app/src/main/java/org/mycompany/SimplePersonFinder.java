package org.mycompany;

import org.mycompany.db.repo.Person;
import org.mycompany.db.repo.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class SimplePersonFinder implements PersonFinder {
    private static final Logger LOG = LoggerFactory.getLogger(SimplePersonFinder.class);

    private PersonRepository personRepository;

    public SimplePersonFinder(PersonRepository personRepostiory) {
        this.personRepository = personRepostiory;
    }

    @Override
    public Mono<Person> getById(UUID id) {
        LOG.info("get a person by id {}", id);

        return personRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("No person with id: "+ id)));
    }

    @Override
    public Mono<Person> save(String firstName, String lastName) {
        LOG.info("create a new person with firstName {}, lastName: {}", firstName, lastName);

        return personRepository.save(new Person(null, firstName, lastName));
    }

    @Override
    public Mono<Person> update(UUID id, String firstName, String lastName) {
        LOG.info("update person with id: {}", id);

        return personRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("No person with id: "+ id)))
                .map(person -> new Person(person.getId(), firstName, lastName))
                .flatMap(person -> personRepository.save(person));
    }

    @Override
    public Mono<Page<Person>> getPage(Pageable pageable) {
        LOG.info("find all person by page");
        return personRepository.findAll(pageable)
                .collectList().map(people -> new PageImpl<>(people));
    }
}
