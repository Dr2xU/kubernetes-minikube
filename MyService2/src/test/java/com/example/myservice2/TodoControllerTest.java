package com.example.myservice2;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Test
    void findAllReturnsTodos() throws Exception {
        given(todoService.findAll()).willReturn(List.of(
                new TodoResponse(1L, "Write report", "Prepare screenshots", false, Instant.parse("2026-03-27T10:15:30Z"))
        ));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Write report"));
    }

    @Test
    void createReturnsCreatedTodo() throws Exception {
        given(todoService.create(any(TodoRequest.class))).willReturn(
                new TodoResponse(2L, "Deploy stack", "Push images and apply manifests", false, Instant.parse("2026-03-27T11:00:00Z"))
        );

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Deploy stack",
                                  "description": "Push images and apply manifests",
                                  "completed": false
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deploy stack")));
    }

    @Test
    void updateReturnsUpdatedTodo() throws Exception {
        given(todoService.update(any(Long.class), any(TodoRequest.class))).willReturn(
                new TodoResponse(2L, "Deploy stack", "Push images and apply manifests", true, Instant.parse("2026-03-27T11:00:00Z"))
        );

        mockMvc.perform(put("/todos/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Deploy stack",
                                  "description": "Push images and apply manifests",
                                  "completed": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        doNothing().when(todoService).delete(1L);

        mockMvc.perform(delete("/todos/1"))
                .andExpect(status().isNoContent());
    }
}
