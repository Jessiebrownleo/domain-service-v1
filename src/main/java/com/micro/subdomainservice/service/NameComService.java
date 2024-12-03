package com.micro.subdomainservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NameComService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${name.com.api.token}")
    private String apiToken;

    @Value("${name.com.api.username}")
    private String apiUsername;

    @Value("${app.domain.base-url}")
    private String baseDomain;

    private static final String API_BASE_URL = "https://api.name.com/v4/domains";
    private static final int DEFAULT_TTL = 300;

    public record DnsRecord(String host, String type, String answer, int ttl) {}

    public boolean createSubdomain(String subdomain, String ipAddress) {
        try {
            String url = String.format("%s/%s/records", API_BASE_URL, baseDomain);

            HttpHeaders headers = createAuthHeaders();

            var dnsRecord = new DnsRecord(subdomain, "A", ipAddress, DEFAULT_TTL);
            HttpEntity<DnsRecord> requestEntity = new HttpEntity<>(dnsRecord, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    String.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create subdomain: " + e.getMessage());
        }
    }

    public boolean updateDnsRecord(String subdomain, String newIpAddress) {
        try {
            String recordId = findDnsRecordId(subdomain);
            if (recordId == null) {
                throw new RuntimeException("DNS record not found for subdomain: " + subdomain);
            }

            String url = String.format("%s/%s/records/%s", API_BASE_URL, baseDomain, recordId);
            HttpHeaders headers = createAuthHeaders();

            var dnsRecord = new DnsRecord(subdomain, "A", newIpAddress, DEFAULT_TTL);
            HttpEntity<DnsRecord> requestEntity = new HttpEntity<>(dnsRecord, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update DNS record: " + e.getMessage());
        }
    }

    public boolean deleteSubdomain(String subdomain) {
        try {
            String recordId = findDnsRecordId(subdomain);
            if (recordId == null) {
                return false;
            }

            String url = String.format("%s/%s/records/%s", API_BASE_URL, baseDomain, recordId);
            HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthHeaders());

            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete subdomain: " + e.getMessage());
        }
    }

    private String findDnsRecordId(String subdomain) {
        try {
            String url = String.format("%s/%s/records", API_BASE_URL, baseDomain);
            HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode records = objectMapper.readTree(response.getBody()).get("records");
                if (records != null && records.isArray()) {
                    for (JsonNode record : records) {
                        if (record.has("host") &&
                                record.get("host").asText().equals(subdomain) &&
                                record.has("id")) {
                            return record.get("id").asText();
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find DNS record: " + e.getMessage());
        }
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(apiUsername, apiToken);
        return headers;
    }
}
