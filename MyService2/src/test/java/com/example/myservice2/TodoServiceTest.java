package com.example.myservice2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TodoService.class)
class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    @Test
    void createAndReadTodo() {
        TodoResponse created = todoService.create(new TodoRequest("Prepare demo", "Show ingress and persistence", false));

        TodoResponse loaded = todoService.findById(created.id());

        assertThat(loaded.title()).isEqualTo("Prepare demo");
        assertThat(loaded.completed()).isFalse();
        assertThat(loaded.createdAt()).isNotNull();
    }

    @Test
    void updateSummaryAndDeleteTodo() {
        TodoResponse created = todoService.create(new TodoRequest("Lock security", "Add RBAC and network policy", false));
        TodoResponse updated = todoService.update(created.id(), new TodoRequest("Lock security", "Add RBAC and network policy", true));

        TodoSummaryResponse summary = todoService.summary();

        assertThat(updated.completed()).isTrue();
        assertThat(summary.total()).isEqualTo(1);
        assertThat(summary.completed()).isEqualTo(1);
        assertThat(summary.pending()).isEqualTo(0);

        todoService.delete(created.id());

        assertThatThrownBy(() -> todoService.findById(created.id()))
                .isInstanceOf(TodoNotFoundException.class);
    }
}
