package com.micro.subdomainservice.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Component
public class SubdomainGenerator {
    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int RANDOM_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public String generateSubdomain(String projectName) {
        // Sanitize project name
        String sanitized = sanitizeProjectName(projectName);

        // Generate random string
        String randomPart = generateRandomString();

        // Combine project name and random string
        return String.format("%s-%s", sanitized, randomPart);
    }

    private String sanitizeProjectName(String projectName) {
        // Convert to lowercase and remove accents
        String normalized = Normalizer.normalize(projectName.toLowerCase(), Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutAccents = pattern.matcher(normalized).replaceAll("");

        // Replace spaces and special characters with hyphens
        String withHyphens = withoutAccents.replaceAll("[^a-z0-9]", "-");

        // Remove consecutive hyphens and trim
        String cleaned = withHyphens.replaceAll("-+", "-").replaceAll("^-|-$", "");

        // Limit length
        return cleaned.substring(0, Math.min(cleaned.length(), 20));
    }

    private String generateRandomString() {
        StringBuilder sb = new StringBuilder(RANDOM_LENGTH);
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            sb.append(ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length())));
        }
        return sb.toString();
    }
}