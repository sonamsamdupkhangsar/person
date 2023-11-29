package org.mycompany;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.annotate.JsonIgnore;
import org.junit.jupiter.api.Test;
import org.mycompany.db.repo.Person;
import org.mycompany.db.repo.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@EnableAutoConfiguration
@AutoConfigureWebTestClient
@SpringBootTest
public class PersonRestIntegTest {

    private static  final Logger LOG = LoggerFactory.getLogger(PersonRepository.class);

    @Autowired
    private WebTestClient webTestClient;

    /**
     * utility create person method to be called from multiple test cases
     */

    private UUID createNewPerson(String firstName, String lastName) {
        LOG.info("create a new person");

        Person person = webTestClient.post().uri("/persons")
                .bodyValue(Map.of("firstName", firstName, "lastName", lastName))
                .exchange().expectStatus().isCreated().expectBody(Person.class)
                .returnResult().getResponseBody();

        assertThat(person.getId()).isNotNull();

        return person.getId();
    }

    @Test
    public void createNew() {
        LOG.info("create new person");
        createNewPerson("Tashi", "Tsering");
    }

    @Test
    public void updatePerson() {
        LOG.info("update person");
        UUID id = createNewPerson("Tashi", "Tsering");

        Mono<Person> personMono = webTestClient.put().uri("/persons")
                .bodyValue(Map.of("id", id, "firstName", "Tenzing"
                        , "lastName", "Gyatso"))
                .exchange().expectStatus().isOk().returnResult(Person.class).getResponseBody().single();

        StepVerifier.create(personMono).assertNext(person -> {
            assertThat(person.getId()).isEqualTo(id);
            assertThat(person.getFirstName()).isEqualTo("Tenzing");
            assertThat(person.getLastName()).isEqualTo("Gyatso");
        }).verifyComplete();
    }

    @Test
    public void getPerson() {
        LOG.info("get person by id");

        UUID id = createNewPerson("John", "Bahadur");

        Mono<Person> personMono = webTestClient.get().uri("/persons/"+id)
                .exchange().expectStatus().isOk().returnResult(Person.class).getResponseBody().single();

        StepVerifier.create(personMono).assertNext(person -> {
            LOG.info("verify person is retrieved by id");
            assertThat(person.getId()).isEqualTo(id);
            assertThat(person.getFirstName()).isEqualTo("John");
            assertThat(person.getLastName()).isEqualTo("Bahadur");
            assertThat(person.getLastName()).isNotEqualTo("Singh");
        }).verifyComplete();
    }

    @Test
    public void getPageOfPeople() {
        LOG.info("get page of people, but lets store some people first");
        int j = 0; //used for lastname
        int i = 0; //user for firstname and count to
        int count = 10;

        for(;i < count; i++) {

            createNewPerson(""+i, ""+j);
            j++;
        }

        //define Page
        Mono<RestPage> restPageMono = webTestClient.get().uri("/persons")
                .exchange().expectStatus().isOk().returnResult(RestPage.class).getResponseBody().single();

        StepVerifier.create(restPageMono).assertNext(restPage -> {
            LOG.info("assert page returned contains totalElements");
            assertThat(restPage.isFirst()).isTrue();
        }).verifyComplete();
    }
}

@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
class RestPage<T> extends PageImpl<T> {
    private static final Logger LOG = LoggerFactory.getLogger(RestPage.class);

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPage(@JsonProperty("content") List<T> content,
                    @JsonProperty("number") int page,
                    @JsonProperty("size") int size,
                    @JsonProperty("totalElements") long total,
                    @JsonProperty("numberOfElements") int numberOfElements,
                    @JsonProperty("pageNumber") int pageNumber
    ) {
        super(content, PageRequest.of(page, size), total);
    }

    public RestPage(Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
        LOG.info("page.content: {}", page.getContent());
    }


}