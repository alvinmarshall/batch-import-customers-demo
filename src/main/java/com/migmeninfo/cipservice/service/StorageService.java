package com.migmeninfo.cipservice.service;

import com.migmeninfo.cipservice.client.StorageRestClient;
import feign.Response;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class StorageService {
    private final StorageRestClient storageRestClient;

    public StorageService(StorageRestClient storageRestClient) {
        this.storageRestClient = storageRestClient;
    }

    @SneakyThrows
    public InputStream getFileStream(String bucket, String key) {
        Response response = storageRestClient.getFile(bucket, key);
        return response.body().asInputStream();
    }
}
