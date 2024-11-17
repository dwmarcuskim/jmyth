package com.marcus.jmyth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RunsResponseDto {
    private String id;
    private String object;
    private long createdAt;
    private String assistantId;
    private String threadId;
    private String status;
    private long startedAt;
    private long expiresAt;
    private long cancelledAt;
    private long failedAt;
    private long completedAt;
    private String model;

    private String error;
}
