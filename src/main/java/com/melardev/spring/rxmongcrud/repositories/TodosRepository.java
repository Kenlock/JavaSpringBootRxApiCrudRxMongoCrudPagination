package com.melardev.spring.rxmongcrud.repositories;


import com.melardev.spring.rxmongcrud.entities.Todo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TodosRepository extends ReactiveCrudRepository<Todo, String> {

//     Flux<Todo> findByCompleted(boolean done);

    Mono<Todo> findById(String id);

    Flux<Todo> findByCompletedTrue();

    Flux<Todo> findByCompletedFalse();

    Flux<Todo> findByCompletedIsTrue();

    Flux<Todo> findByCompletedIsFalse();

    Flux<Todo> findByTitleContains(String title);

    Flux<Todo> findByDescriptionContains(String description);

    // You can not just create findAll(pageable) because the framework is looking for a condition
    // so let's use this workaround @Query exists:true to make it look for everything and no crashing the app
    @Query("{ id: { $exists: true }}")
    Flux<Todo> findAll(Pageable page);

    Flux<Todo> findByCompletedFalse(Pageable pageRequest);

    Flux<Todo> findByCompletedIsTrue(Pageable pageRequest);

    // @Query("{'title': ?0, 'description' : ?1")
    // Mono<Todo> findByTitleAndDescription(String title, String description);

    //Flux<Todo> findAll(PageRequest pageRequest);
/*
    // for deferred execution
    Flux<Todo> findByDescription(Mono<String> description);

    Mono<Todo> findByTitleAndDescription(Mono<String> title, String description);
    */
}