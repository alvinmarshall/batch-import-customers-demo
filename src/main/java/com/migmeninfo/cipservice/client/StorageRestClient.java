package com.migmeninfo.cipservice.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "storageRestClient", url = "${service.storage-endpoint}")
public interface StorageRestClient {
    @GetMapping(value = "/{bucket}/{key}")
    Response getFile(@PathVariable("bucket") String bucket, @PathVariable("key") String key);
}
