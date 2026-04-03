package com.example.myservice;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MyServiceRest.class)
class MyServiceRestTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MyService2Client myService2Client;

    @Test
    void rootEndpointReturnsOriginalGreeting() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello"));
    }

    @Test
    void aggregateEndpointReturnsGreetingFromSecondService() throws Exception {
        given(myService2Client.fetchGreeting()).willReturn("Hello from MyService2");

        mockMvc.perform(get("/aggregate"))
                .andExpect(status().isOk())
                .andExpect(content().string("MyService -> Hello from MyService2"));
    }

    @Test
    void todosEndpointReturnsDownstreamTodos() throws Exception {
        given(myService2Client.fetchTodos()).willReturn(List.of(
                new TodoResponse(1L, "Prepare report", "Capture Kubernetes screenshots", false, Instant.parse("2026-03-27T10:15:30Z"))
        ));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Prepare report"));
    }

    @Test
    void createTodoDelegatesToMyService2() throws Exception {
        given(myService2Client.createTodo(new TodoRequest("Deploy stack", "Apply manifests and verify ingress", false)))
                .willReturn(new TodoResponse(2L, "Deploy stack", "Apply manifests and verify ingress", false, Instant.parse("2026-03-27T11:00:00Z")));

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Deploy stack",
                                  "description": "Apply manifests and verify ingress",
                                  "completed": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void dashboardAggregatesGreetingSummaryAndTodos() throws Exception {
        given(myService2Client.fetchGreeting()).willReturn("Hello from MyService2");
        given(myService2Client.fetchSummary()).willReturn(new TodoSummaryResponse(3, 1, 2));
        given(myService2Client.fetchTodos()).willReturn(List.of(
                new TodoResponse(1L, "Secure cluster", "Apply RBAC and network policies", false, Instant.parse("2026-03-27T10:15:30Z"))
        ));

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.greeting").value("MyService -> Hello from MyService2"))
                .andExpect(jsonPath("$.summary.total").value(3))
                .andExpect(jsonPath("$.todos[0].title").value("Secure cluster"));
    }
}
