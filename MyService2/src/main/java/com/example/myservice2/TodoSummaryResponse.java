package com.example.myservice2;

public record TodoSummaryResponse(
        long total,
        long completed,
        long pending
) {
}
