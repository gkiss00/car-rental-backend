package org.kiss.storage;

import java.time.Duration;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private static final Duration UPLOAD_URL_TTL = Duration.ofMinutes(10);

    private final S3Presigner s3Presigner;
    private final StorageProperties storageProperties;

    public String generateUploadUrl(String objectKey) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(storageProperties.bucketName())
                .key(objectKey)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(UPLOAD_URL_TTL)
                .putObjectRequest(putObjectRequest)
                .build();

        log.info("Presigning PUT object request: bucket={}, key={}, signatureDuration={}",
                putObjectRequest.bucket(), putObjectRequest.key(), presignRequest.signatureDuration());

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        log.debug("Presigned PUT object response: url={}, expiration={}, isBrowserExecutable={}, signedHeaders={}",
                presignedRequest.url(), presignedRequest.expiration(),
                presignedRequest.isBrowserExecutable(), presignedRequest.signedHeaders());

        return presignedRequest.url().toString();
    }

    public String publicUrlFor(String objectKey) {
        String base = storageProperties.publicUrl();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/" + objectKey;
    }
}
