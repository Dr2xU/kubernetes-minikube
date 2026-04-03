package com.example.myservice;

import java.util.List;

public record TodoDashboardResponse(
        String greeting,
        TodoSummaryResponse summary,
        List<TodoResponse> todos
) {
}
