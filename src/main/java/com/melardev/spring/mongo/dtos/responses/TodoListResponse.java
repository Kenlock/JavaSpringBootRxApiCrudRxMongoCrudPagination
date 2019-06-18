package com.melardev.spring.mongo.dtos.responses;

import com.melardev.spring.mongo.entities.Todo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TodoListResponse extends SuccessResponse {
    private final PageMeta pageMeta;
    private final Collection<TodoSummaryDto> todos;

    public TodoListResponse(List<TodoSummaryDto> todos, PageMeta pageMeta) {
        this.todos = todos;
        this.pageMeta = pageMeta;
    }

    public static TodoListResponse build(List<Todo> todos, PageMeta pageMeta) {
        List<TodoSummaryDto> todoDtos = todos.stream().map(TodoSummaryDto::build).collect(Collectors.toList());
        return new TodoListResponse(todoDtos, pageMeta);
    }

    public PageMeta getPageMeta() {
        return pageMeta;
    }

    public Collection<TodoSummaryDto> getTodos() {
        return todos;
    }
}
