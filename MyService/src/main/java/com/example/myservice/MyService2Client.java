package com.example.myservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class MyService2Client {

    private final RestClient restClient;

    public MyService2Client(
            RestClient.Builder restClientBuilder,
            @Value("${myservice2.base-url}") String myService2BaseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(myService2BaseUrl)
                .build();
    }

    public String fetchGreeting() {
        return execute(() -> this.restClient.get()
                .uri("/")
                .retrieve()
                .body(String.class), "Failed to fetch greeting from MyService2");
    }

    public List<TodoResponse> fetchTodos() {
        return execute(() -> this.restClient.get()
                .uri("/todos")
                .retrieve()
                .body(new ParameterizedTypeReference<List<TodoResponse>>() {
                }), "Failed to fetch todos from MyService2");
    }

    public TodoResponse fetchTodo(Long id) {
        return execute(() -> this.restClient.get()
                .uri("/todos/{id}", id)
                .retrieve()
                .body(TodoResponse.class), "Failed to fetch todo " + id + " from MyService2");
    }

    public TodoResponse createTodo(TodoRequest request) {
        return execute(() -> this.restClient.post()
                .uri("/todos")
                .body(request)
                .retrieve()
                .body(TodoResponse.class), "Failed to create todo through MyService2");
    }

    public TodoResponse updateTodo(Long id, TodoRequest request) {
        return execute(() -> this.restClient.put()
                .uri("/todos/{id}", id)
                .body(request)
                .retrieve()
                .body(TodoResponse.class), "Failed to update todo " + id + " through MyService2");
    }

    public void deleteTodo(Long id) {
        execute(() -> {
            this.restClient.delete()
                    .uri("/todos/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
            return null;
        }, "Failed to delete todo " + id + " through MyService2");
    }

    public TodoSummaryResponse fetchSummary() {
        return execute(() -> this.restClient.get()
                .uri("/todos/summary")
                .retrieve()
                .body(TodoSummaryResponse.class), "Failed to fetch todo summary from MyService2");
    }

    private <T> T execute(RestCall<T> restCall, String message) {
        try {
            return restCall.call();
        } catch (RestClientResponseException exception) {
            throw new DownstreamServiceException(message, exception.getStatusCode(), exception);
        } catch (RestClientException exception) {
            throw new DownstreamServiceException(message, org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE, exception);
        }
    }

    @FunctionalInterface
    private interface RestCall<T> {
        T call();
    }
}
