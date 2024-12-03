package com.micro.subdomainservice.service;

import com.micro.subdomainservice.dto.DomainRequest;
import com.micro.subdomainservice.dto.DomainResponse;
import com.micro.subdomainservice.dto.DomainUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface DomainService {
    DomainResponse create(DomainRequest request);
    DomainResponse update(UUID id, DomainUpdateRequest request);
    void delete(UUID id);
    DomainResponse getById(UUID id);
    List<DomainResponse> getAll();
}
