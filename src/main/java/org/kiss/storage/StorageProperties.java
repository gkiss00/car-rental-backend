package org.kiss.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
public record StorageProperties(
        String bucketName,
        String region,
        String endpoint,
        String accessKey,
        String secretKey,
        String publicUrl) {
}
