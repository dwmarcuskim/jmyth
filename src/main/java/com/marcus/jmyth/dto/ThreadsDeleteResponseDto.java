package com.marcus.jmyth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThreadsDeleteResponseDto {
    private String id;
    private String object;
    private String deleted;
    private String error;
}
