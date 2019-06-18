package com.melardev.spring.mongo.repositories;


import com.melardev.spring.mongo.entities.Todo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TodosRepository extends ReactiveCrudRepository<Todo, String> {

//     Flux<Todo> findByCompleted(boolean done);

    // You can not just create findAll(pageable) because the framework is looking for a condition
    // so let's use this workaround @Query exists:true to make it look for everything and no crashing the app
    @Query("{ id: { $exists: true }}")
    Flux<Todo> findAll(Pageable page);

    @Query(fields = "{id: 1, title: 1, completed:1, createdAt: 1, updatedAt:1}", value = "{id: {$exists:true}}")
    Flux<Todo> findAllHqlSummary();

    @Query(fields = "{id: 1, title: 1, completed:1, createdAt: 1, updatedAt:1}", value = "{id: {$exists:true}}")
    Flux<Todo> findAllHqlSummary(Pageable pageable);

    @Query(fields = "{description:0}")
    Flux<Todo> findByCompletedFalse(Pageable pageRequest);

    Flux<Todo> findByCompletedFalse();

    Flux<Todo> findByCompletedIsFalse();

    Flux<Todo> findByCompletedTrue();

    @Query(fields = "{description:0}")
    Flux<Todo> findByCompletedIsTrue(Pageable pageRequest);

    Flux<Todo> findByCompletedIsTrue();

    Mono<Todo> findById(String id);

    Flux<Todo> findByTitleContains(String title);

    Flux<Todo> findByDescriptionContains(String description);

    @Query(value = "{'completed': false}", count = true)
    Mono<Long> countByHqlCompletedFalse();

    Mono<Long> countByCompletedIsFalse();

    Mono<Long> countByCompletedFalse();

    @Query(value = "{'completed': false}", count = true)
    Mono<Long> countByHqlCompletedTrue();

    Mono<Long> countByCompletedIsTrue();

    Mono<Long> countByCompletedTrue();

    Mono<Long> countByCompleted(boolean completed);

    @Query(value = "{'completed': ?0}", count = true)
    Mono<Long> countByHqlCompleted(boolean completed);

    // @Query("{'title': ?0, 'description' : ?1")
    // Mono<Todo> findByTitleAndDescription(String title, String description);

    //Flux<Todo> findAll(PageRequest pageRequest);

    Flux<Todo> findByDescription(Mono<String> description);

    Mono<Todo> findByTitleAndDescription(Mono<String> title, String description);

}