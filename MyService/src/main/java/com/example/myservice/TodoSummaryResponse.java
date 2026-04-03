package com.example.myservice;

public record TodoSummaryResponse(
        long total,
        long completed,
        long pending
) {
}
