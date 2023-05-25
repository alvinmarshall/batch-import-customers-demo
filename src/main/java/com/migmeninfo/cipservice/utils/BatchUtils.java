package com.migmeninfo.cipservice.utils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BatchUtils {
    public static Optional<String> getBatchFileName(Object input, String fileName) {
        Optional<String> optionalFilePath = Optional.empty();
        if (input instanceof Map) {
            @SuppressWarnings("unchecked") Map<String, String> unZipFiles = (Map<String, String>) input;
            optionalFilePath = unZipFiles.entrySet().stream().map(stringStringEntry -> {
                        if (!stringStringEntry.getKey().equalsIgnoreCase(fileName)) return null;
                        return stringStringEntry.getValue();
                    })
                    .filter(Objects::nonNull)
                    .findAny();
        }
        return optionalFilePath;
    }
}
