package com.micro.subdomainservice.service;

import com.micro.subdomainservice.domain.Domain;
import com.micro.subdomainservice.dto.DomainRequest;
import com.micro.subdomainservice.dto.DomainResponse;
import com.micro.subdomainservice.dto.DomainUpdateRequest;
import com.micro.subdomainservice.exception.DomainException;
import com.micro.subdomainservice.mapper.DomainMapper;
import com.micro.subdomainservice.repository.DomainRepository;
import com.micro.subdomainservice.util.SubdomainGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;
    private final DomainMapper domainMapper;
    private final SubdomainGenerator subdomainGenerator;
    private final NameComService nameComService;

    @Value("${app.domain.base-url}")
    private String baseDomain;

    @Override
    @Transactional
    public DomainResponse create(DomainRequest request) {
        String subdomain;
        int attempts = 0;
        do {
            subdomain = subdomainGenerator.generateSubdomain(request.getProjectName());
            attempts++;
            if (attempts > 5) {
                throw new DomainException("Unable to generate unique subdomain");
            }
        } while (domainRepository.existsBySubdomain(subdomain));

        // Create DNS record at Name.com
        boolean dnsCreated = nameComService.createSubdomain(subdomain, request.getIpAddress());
        if (!dnsCreated) {
            throw new DomainException("Failed to create DNS record at Name.com");
        }

        Domain domain = Domain.builder()
                .subdomain(subdomain)
                .ipAddress(request.getIpAddress())
                .projectName(request.getProjectName())
                .status(Domain.DomainStatus.ACTIVE)
                .build();

        Domain savedDomain = domainRepository.save(domain);
        DomainResponse response = domainMapper.toResponse(savedDomain);
        response.setFullDomain(String.format("%s.%s", subdomain, baseDomain));

        return response;
    }

    @Override
    @Transactional
    public DomainResponse update(UUID id, DomainUpdateRequest request) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new DomainException("Domain not found"));

        boolean dnsUpdated = nameComService.updateDnsRecord(domain.getSubdomain(), request.getIpAddress());
        if (!dnsUpdated) {
            throw new DomainException("Failed to update DNS record at Name.com");
        }

        domain.setIpAddress(request.getIpAddress());
        Domain updatedDomain = domainRepository.save(domain);

        DomainResponse response = domainMapper.toResponse(updatedDomain);
        response.setFullDomain(String.format("%s.%s", updatedDomain.getSubdomain(), baseDomain));

        return response;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new DomainException("Domain not found"));

        boolean dnsDeleted = nameComService.deleteSubdomain(domain.getSubdomain());
        if (!dnsDeleted) {
            throw new DomainException("Failed to delete DNS record from Name.com");
        }

        domainRepository.delete(domain);
    }

    @Override
    public DomainResponse getById(UUID id) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new DomainException("Domain not found"));
        DomainResponse response = domainMapper.toResponse(domain);
        response.setFullDomain(String.format("%s.%s", domain.getSubdomain(), baseDomain));
        return response;
    }

    @Override
    public List<DomainResponse> getAll() {
        return domainRepository.findAll().stream()
                .map(domain -> {
                    DomainResponse response = domainMapper.toResponse(domain);
                    response.setFullDomain(String.format("%s.%s", domain.getSubdomain(), baseDomain));
                    return response;
                })
                .collect(Collectors.toList());
    }
}