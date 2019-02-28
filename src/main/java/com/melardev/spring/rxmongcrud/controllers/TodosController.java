package com.melardev.spring.rxmongcrud.controllers;


import com.melardev.spring.rxmongcrud.dtos.responses.*;
import com.melardev.spring.rxmongcrud.entities.Todo;
import com.melardev.spring.rxmongcrud.repositories.TodosRepository;
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
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Mono<AppResponse> response = getResponseFromTodosFlux(todos, request, page, pageSize);
        return response;
    }


    @GetMapping("/{id}")
    public Mono<? extends AppResponse> get(@PathVariable("id") String id) {
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
                    t.setDescription(todoInput.getDescription());
                    t.setCompleted(todoInput.isCompleted());
                    return todosRepository.save(t).map(te -> (AppResponse) new TodoDetailsResponse(te));
                }).map(ResponseEntity::ok).defaultIfEmpty(new ResponseEntity<>(new AppResponse(false, "Not found"), HttpStatus.NOT_FOUND));
        return res;
    }

    @PutMapping("/{id}/2")
    public Mono<ResponseEntity<Todo>> update2(@PathVariable("id") String id,
                                              @RequestBody Todo todo) {
        System.out.println("Update Customer with ID = " + id + "...");

        return todosRepository.findById(id).flatMap(customerData -> {
            customerData.setTitle(todo.getTitle());
            customerData.setDescription(todo.getDescription());
            customerData.setCompleted(todo.isCompleted());
            return todosRepository.save(customerData);
        }).map(updatedcustomer -> new ResponseEntity<>(updatedcustomer, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Spring-reactive-sample returns Mono<Void>
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

    @GetMapping("/pending")
    public Mono<AppResponse> getPending(ServerHttpRequest request,
                                        @RequestParam(value = "page", defaultValue = "1") int page,
                                        @RequestParam(value = "page_size", defaultValue = "5") int pageSize) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        Flux<Todo> todos = todosRepository.findByCompletedFalse(pageRequest);
        Mono<AppResponse> response = getResponseFromTodosFlux(todos, request, page, pageSize);
        return response;
    }


    @GetMapping("/completed")
    public Mono<AppResponse> getCompleted(ServerHttpRequest request,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "page_size", defaultValue = "5") int pageSize) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        Flux<Todo> todos = todosRepository.findByCompletedIsTrue(pageRequest);
        Mono<AppResponse> response = getResponseFromTodosFlux(todos, request, page, pageSize);
        return response;
    }

    @GetMapping("/completed/simple")
    public Flux<Todo> getCompleted() {
        return todosRepository.findByCompletedIsTrue();
    }

    private Mono<AppResponse> getResponseFromTodosFlux(Flux<Todo> todos, ServerHttpRequest request, int page, int pageSize) {
        Mono<AppResponse> monoResult = todos.collectList().flatMap(new Function<List<Todo>, Mono<? extends AppResponse>>() {
            @Override
            public Mono<? extends AppResponse> apply(List<Todo> todos) {
                // TODO: I really have to improve the asynchronosicty of that count()

                PageMeta pageMeta = PageMeta.build(todos, request.getURI().getPath(), page, pageSize, todosRepository.count());
                // PageImpl<Todo> pageMeta = new PageImpl<>(todos, pageRequest, todosRepository.count().block());
                return Mono.just(new TodoListResponse(pageMeta, getTodos(todos)));
            }
        });
        return monoResult;
    }


    private List<TodoSummaryDto> getTodos(List<Todo> todos) {
        List<TodoSummaryDto> todoDtos = todos.stream().map(TodoSummaryDto::build).collect(Collectors.toList());
        return todoDtos;
    }
}