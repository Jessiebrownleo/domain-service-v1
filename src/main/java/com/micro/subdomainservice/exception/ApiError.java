package com.micro.subdomainservice.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private String message;
    private String error;
    private Integer status;
    private String timestamp;  // Changed from LocalDateTime
}