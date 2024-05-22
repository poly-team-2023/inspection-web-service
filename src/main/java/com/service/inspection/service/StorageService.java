package com.service.inspection.service;

import com.google.common.base.Preconditions;
import com.service.inspection.configs.BucketName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StorageService {

    private final S3Client amazonS3;

    // TODO правильная обработка ошибок

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveFile(BucketName bucketName, UUID uuid, MultipartFile multipartFile) {
        Preconditions.checkNotNull(uuid);
        Preconditions.checkNotNull(multipartFile);
        Preconditions.checkNotNull(multipartFile);

        saveFile(bucketName, uuid.toString(), multipartFile);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveFile(BucketName bucketName, String key, MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            saveFile(bucketName, key, inputStream);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void saveFile(BucketName bucketName, String key, InputStream inputStream) {
        try {
            saveFile(bucketName, key, inputStream, inputStream.available());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void saveFile(BucketName bucketName, String key, InputStream inputStream, int contentLength) {
            if (amazonS3.listBuckets().buckets().stream().map(Bucket::name)
                    .noneMatch(b -> Objects.equals(b, bucketName.getBucket()))) {
                amazonS3.createBucket(b -> b.bucket(bucketName.getBucket()).build());
            }
            amazonS3.putObject(
                    b -> b.bucket(bucketName.getBucket()).key(key),
                    RequestBody.fromInputStream(inputStream, contentLength)
            );
    }

    public BytesWithContentType getFile(BucketName bucketName, String key) {
        byte[] fileBytes;
        try (ResponseInputStream<GetObjectResponse> responseInputStream =
                     amazonS3.getObject(b -> b.bucket(bucketName.getBucket()).key(key).build())) {
            fileBytes = responseInputStream.readAllBytes();
            return new BytesWithContentType(fileBytes, responseInputStream.response().contentType());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Data
    @AllArgsConstructor
    public static class BytesWithContentType {
        private byte[] bytes;
        private String contentType;
        private String name;

        public BytesWithContentType(byte[] bytes, String contentType) {
            this.bytes = bytes;
            this.contentType = contentType;
        }
    }
}
