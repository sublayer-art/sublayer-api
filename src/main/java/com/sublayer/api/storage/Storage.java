package com.sublayer.api.storage;

import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface Storage {

    /**
     *
     * @param inputStream
     * @param contentLength
     * @param contentType
     * @param keyName
     */
    void store(InputStream inputStream, long contentLength, String contentType, String keyName);

    String store(InputStream inputStream, String fileName);

    String[] store(InputStream[] inputStreams, String[] fileNames, String dirPath);

    Stream<Path> loadAll();

    Path load(String keyName);

    Resource loadAsResource(String keyName);

    void delete(String keyName);

    String generateUrl(String keyName);
}
