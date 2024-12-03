package com.micro.subdomainservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DomainUpdateRequest {
        @NotBlank(message = "IP address is required")
        private String ipAddress;
}