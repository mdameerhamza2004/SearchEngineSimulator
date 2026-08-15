package com.searchengine.simulator.model.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentRequest(
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Content is required") String content,
        String category
) {
}
