package com.example.myservice;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping
public class MyServiceRest {

    private final MyService2Client myService2Client;

    public MyServiceRest(MyService2Client myService2Client) {
        this.myService2Client = myService2Client;
    }

    @GetMapping("/")
    public String sayHello(){
        return "Hello";
    }

    @GetMapping("/aggregate")
    public String aggregateGreetings() {
        return "MyService -> " + myService2Client.fetchGreeting();
    }

    @GetMapping("/todos")
    public List<TodoResponse> findAllTodos() {
        return myService2Client.fetchTodos();
    }

    @GetMapping("/todos/{id}")
    public TodoResponse findTodo(@PathVariable Long id) {
        return myService2Client.fetchTodo(id);
    }

    @PostMapping("/todos")
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {
        return ResponseEntity.ok(myService2Client.createTodo(request));
    }

    @PutMapping("/todos/{id}")
    public TodoResponse updateTodo(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        return myService2Client.updateTodo(id, request);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        myService2Client.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/todos/summary")
    public TodoSummaryResponse summary() {
        return myService2Client.fetchSummary();
    }

    @GetMapping("/dashboard")
    public TodoDashboardResponse dashboard() {
        return new TodoDashboardResponse(
                "MyService -> " + myService2Client.fetchGreeting(),
                myService2Client.fetchSummary(),
                myService2Client.fetchTodos()
        );
    }

}
