package com.micro.subdomainservice.repository;

import com.micro.subdomainservice.domain.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface DomainRepository extends JpaRepository<Domain, UUID> {
    boolean existsBySubdomain(String subdomain);
    Optional<Domain> findBySubdomain(String subdomain);
}
