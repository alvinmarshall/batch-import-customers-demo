package com.migmeninfo.cipservice.utils;

import java.io.*;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipUtility {
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    private Map<String, String> files = new LinkedHashMap<>();

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     */
    public void unzip(String zipFilePath, String destDirectory, String rootLevelDir) throws IOException {

        if (rootLevelDir == null || rootLevelDir.isEmpty()) {
            rootLevelDir = "/";
        }
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!filePath.contains("MAC")) {
                    files.put(entry.getName(), filePath);
                }

                if (rootLevelDir.equals("/") || entry.getName().startsWith(rootLevelDir)) {

                    if (!rootLevelDir.equals("/")) {
                        filePath = filePath.replaceFirst(rootLevelDir, "");
                    }
                    if (!entry.isDirectory()) {
                        // if the entry is a file, extracts it
                        extractFile(zipIn, filePath, entry);
                    } else {
                        // if the entry is a directory, make the directory
                        File dir = new File(filePath);
                        dir.mkdirs();
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    /**
     * Extracts a zip entry (file entry)
     */
    private void extractFile(ZipInputStream zipIn, String filePath, ZipEntry entry) throws IOException {
        if (Files.notExists(new File(filePath).getParentFile().toPath())) {
            if (filePath.contains("MAC")) return;
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            files.put(entry.getName(), file.getAbsolutePath());

        }
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }

    }

    public Map<String, String> getFiles() {
        return files.entrySet().stream().map(stringStringEntry -> {
            String[] split = stringStringEntry.getKey().split("/");
            if (split.length == 0) return stringStringEntry;
            int endCharPosition = split.length - 1;
            return Map.entry(split[endCharPosition], stringStringEntry.getValue());
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k, v) -> v, LinkedHashMap::new));
    }
}
