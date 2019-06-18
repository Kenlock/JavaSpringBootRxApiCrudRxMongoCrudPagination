package com.melardev.spring.mongo.controllers;


import com.melardev.spring.mongo.dtos.responses.*;
import com.melardev.spring.mongo.entities.Todo;
import com.melardev.spring.mongo.repositories.TodosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("todos")
public class TodosController {

    @Autowired
    TodosRepository todosRepository;

    @GetMapping
    public Mono<? extends AppResponse> getAll(ServerHttpRequest request,
                                              @RequestParam(value = "page", defaultValue = "1") int page,
                                              @RequestParam(value = "page_size", defaultValue = "5") int pageSize) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        Flux<Todo> todos = todosRepository.findAll(pageRequest);
        Mono<AppResponse> response = getResponseFromTodosFlux(todos, todosRepository.count(), request, page, pageSize);
        return response;
    }

    @GetMapping("/pending")
    public Mono<AppResponse> getPending(ServerHttpRequest request,
                                        @RequestParam(value = "page", defaultValue = "1") int page,
                                        @RequestParam(value = "page_size", defaultValue = "5") int pageSize) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        Flux<Todo> todos = todosRepository.findByCompletedFalse(pageRequest);
        Mono<AppResponse> response = getResponseFromTodosFlux(todos, todosRepository.countByCompletedIsFalse(), request, page, pageSize);
        return response;
    }

    @GetMapping("/completed")
    public Mono<AppResponse> getCompleted(ServerHttpRequest request,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "page_size", defaultValue = "5") int pageSize) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        Flux<Todo> todos = todosRepository.findByCompletedIsTrue(pageRequest);
        Mono<AppResponse> response = getResponseFromTodosFlux(todos, todosRepository.countByCompletedIsTrue(), request, page, pageSize);
        return response;
    }

    @GetMapping("/{id}")
    public Mono<? extends AppResponse> getById(@PathVariable("id") String id) {
        Mono<Todo> res = this.todosRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("Not Found " + id)));
        // return res.<AppResponse>map(TodoDetailsResponse::new);
        Mono<AppResponse> responseMono = res.map(TodoDetailsResponse::new);
        return responseMono;
    }

    @PostMapping
    public Mono<? extends AppResponse> create(@Valid @RequestBody Todo todo) {
        Mono<Todo> todoMono = todosRepository.save(todo);
        return todoMono.<AppResponse>map(TodoDetailsResponse::new);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<AppResponse>> update(@PathVariable("id") String id, @RequestBody Todo todoInput) {

        Mono<ResponseEntity<AppResponse>> res = todosRepository.findById(id)
                .flatMap(t -> {
                    String title = todoInput.getTitle();
                    if (title != null)
                        t.setTitle(title);

                    String description = todoInput.getDescription();
                    if (description != null)
                        t.setDescription(description);

                    t.setCompleted(todoInput.isCompleted());
                    return todosRepository.save(t).map(te -> (AppResponse) new TodoDetailsResponse(te));
                }).map(ResponseEntity::ok).defaultIfEmpty(new ResponseEntity<>(new AppResponse(false, "Not found"), HttpStatus.NOT_FOUND));
        return res;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse> delete(@PathVariable("id") String id) {
        try {
            todosRepository.deleteById(id).subscribe();
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Something went wrong"), HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>(new SuccessResponse("Deleted successfully"), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<AppResponse> deleteAll() {
        try {
            todosRepository.deleteAll().subscribe();
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Something went wrong"), HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>(new SuccessResponse("Deleted successfully"), HttpStatus.OK);
    }


    @GetMapping("/completed/simple")
    public Flux<Todo> getCompleted() {
        return todosRepository.findByCompletedIsTrue();
    }

    private Mono<AppResponse> getResponseFromTodosFlux(Flux<Todo> todos, Mono<Long> count, ServerHttpRequest request, int page, int pageSize) {
        // All credits go for @cheron.antoine, he gave me the solution for making this to work:
        // https://medium.com/@cheron.antoine/reactor-java-2-how-to-manipulate-the-data-inside-mono-and-flux-b36ae383b499

        /* More readable code, but longer
        return todos.collectList().flatMap(new Function<List<Todo>, Mono<AppResponse>>() {
            @Override
            public Mono<AppResponse> apply(List<Todo> todos) {
                return todosRepository.count().map(new Function<Long, PageMeta>() {
                    @Override
                    public PageMeta apply(Long totalItemsCount) {
                        return PageMeta.build(todos, request.getURI().getPath(), page, pageSize, totalItemsCount);
                    }
                }).map(new Function<PageMeta, AppResponse>() {
                    @Override
                    public AppResponse apply(PageMeta pageMeta) {
                        return TodoListResponse.build(todos, pageMeta);
                    }
                });
            }
        });
        */

        // Less readable but less code, read the above if this one is hard to read, they are equivalent
        return todos.collectList().flatMap(todoList -> count
                .map(totalItemsCount -> PageMeta.build(todoList, request.getURI().getPath(), page, pageSize, totalItemsCount))
                .map(pageMeta -> TodoListResponse.build(todoList, pageMeta)));

    }

}