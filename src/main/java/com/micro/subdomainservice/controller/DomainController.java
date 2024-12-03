package com.micro.subdomainservice.controller;

import com.micro.subdomainservice.dto.*;
import com.micro.subdomainservice.service.DomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/domains")
@RequiredArgsConstructor
@Tag(name = "Domain Controller", description = "Domain management APIs")
public class DomainController {

    private final DomainService domainService;

    @Operation(summary = "Create new domain")
    @PostMapping
    public ResponseEntity<DomainResponse> createDomain(@RequestBody DomainRequest request) {
        return ResponseEntity.ok(domainService.create(request));
    }

    @Operation(summary = "Update domain")
    @PutMapping("/{id}")
    public ResponseEntity<DomainResponse> updateDomain(
            @PathVariable UUID id,
            @RequestBody DomainUpdateRequest request) {
        return ResponseEntity.ok(domainService.update(id, request));
    }

    @Operation(summary = "Delete domain")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDomain(@PathVariable UUID id) {
        domainService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get domain by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DomainResponse> getDomain(@PathVariable UUID id) {
        return ResponseEntity.ok(domainService.getById(id));
    }

    @Operation(summary = "Get all domains")
    @GetMapping
    public ResponseEntity<List<DomainResponse>> getAllDomains() {
        return ResponseEntity.ok(domainService.getAll());
    }
}