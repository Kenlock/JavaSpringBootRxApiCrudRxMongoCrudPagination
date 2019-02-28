package com.melardev.spring.rxmongcrud.dtos.responses;

import com.melardev.spring.rxmongcrud.entities.Todo;

import java.util.Collection;
import java.util.List;

public class TodoListResponse extends SuccessResponse{
    private final PageMeta pageMeta;
    private final Collection<TodoSummaryDto> todos;

    public TodoListResponse(PageMeta pageMeta, List<TodoSummaryDto> todos) {
        this.todos = todos;
        this.pageMeta = pageMeta;
    }

    public PageMeta getPageMeta() {
        return pageMeta;
    }

    public Collection<TodoSummaryDto> getTodos() {
        return todos;
    }
}
