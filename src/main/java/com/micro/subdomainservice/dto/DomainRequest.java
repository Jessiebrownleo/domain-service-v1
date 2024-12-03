package com.micro.subdomainservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class DomainRequest {
        @NotBlank(message = "Project name is required")
        private String projectName;

        @NotBlank(message = "IP address is required")
        private String ipAddress;
}