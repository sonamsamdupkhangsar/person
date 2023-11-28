package org.mycompany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.print.attribute.standard.Media;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Service
public class Handler {

    private static final Logger LOG = LoggerFactory.getLogger(Handler.class);

    @Autowired
    private PersonFinder personFinder;

    public Mono<ServerResponse> getPerson(ServerRequest serverRequest) {
        LOG.info("get person by id");

        return personFinder.getById(UUID.fromString(serverRequest.pathVariable("id")))
                .flatMap(person -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(person))
                .onErrorResume(throwable -> {
                    LOG.error("get person by id failed, error: "+ throwable.getMessage());
                    return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", throwable.getMessage()));
                });

    }

    public Mono<ServerResponse> createNew(ServerRequest serverRequest) {
        LOG.info("store a new person");
        return serverRequest.bodyToMono(Map.class).flatMap(map -> {
            return personFinder.save(map.get("firstName").toString(), map.get("lastName").toString())
                    .flatMap(person -> ServerResponse.created(URI.create("/persons/"+ person.getId()))
                            .contentType(MediaType.APPLICATION_JSON).bodyValue(person))
                    .onErrorResume(throwable -> {
                        LOG.error("failed to create person d, error: "+ throwable.getMessage());
                        return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", throwable.getMessage()));
                    });
        });
    }

    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        LOG.info("update a person object");

        return serverRequest.bodyToMono(Map.class).flatMap(map ->
                personFinder.update(
                                UUID.fromString(map.get("id").toString()), map.get("firstName").toString(), map.get("lastName").toString())
                        .flatMap(person -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(person))
                        .onErrorResume(throwable -> {
                            LOG.error("update person by id call failed, error", throwable.getMessage());
                            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(Map.of("error", throwable.getMessage()));
                        })
        );

    }

    public Mono<ServerResponse> getPageOfPeople(ServerRequest serverRequest) {
        LOG.info("get a page of people");
        int page = 0;
        int size = 100;

        try {
            if (serverRequest.pathVariable("page") != null) {
                page = Integer.parseInt(serverRequest.pathVariable("page"));
            }
            if (serverRequest.pathVariable("size") != null) {
                size = Integer.parseInt(serverRequest.pathVariable("size"));
            }
        }
        catch (IllegalArgumentException e) {
            LOG.warn("no page/size or variable found, use default page {}, and size: {}", page, size);
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        return personFinder.getPage(pageRequest)
                .flatMap(pageOfPeople -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(pageOfPeople))
                .onErrorResume(throwable -> {
                    LOG.error("get person by by page call failed, error", throwable.getMessage());
                    return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("error", throwable.getMessage()));
                });
    }
}
