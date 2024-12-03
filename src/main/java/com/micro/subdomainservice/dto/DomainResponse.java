package com.micro.subdomainservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DomainResponse{
    private UUID id;
    private String projectName;
    private String subdomain;
    private String ipAddress;
    private String status;
    private String fullDomain;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
