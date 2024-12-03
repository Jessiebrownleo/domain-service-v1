package com.micro.subdomainservice.mapper;

import com.micro.subdomainservice.domain.Domain;
import com.micro.subdomainservice.dto.DomainRequest;
import com.micro.subdomainservice.dto.DomainResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DomainMapper {
    @Mapping(target = "fullDomain", ignore = true)
    DomainResponse toResponse(Domain domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    Domain toDomain(DomainRequest request);
}

