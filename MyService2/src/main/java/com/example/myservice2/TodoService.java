package com.example.myservice2;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> findAll() {
        return todoRepository.findAll()
                .stream()
                .map(TodoResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TodoResponse findById(Long id) {
        return TodoResponse.fromEntity(findTodo(id));
    }

    public TodoResponse create(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(request.completed());
        return TodoResponse.fromEntity(todoRepository.save(todo));
    }

    public TodoResponse update(Long id, TodoRequest request) {
        Todo todo = findTodo(id);
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(request.completed());
        return TodoResponse.fromEntity(todoRepository.save(todo));
    }

    public void delete(Long id) {
        Todo todo = findTodo(id);
        todoRepository.delete(todo);
    }

    @Transactional(readOnly = true)
    public TodoSummaryResponse summary() {
        List<Todo> todos = todoRepository.findAll();
        long completed = todos.stream().filter(Todo::isCompleted).count();
        long total = todos.size();
        return new TodoSummaryResponse(total, completed, total - completed);
    }

    private Todo findTodo(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }
}
