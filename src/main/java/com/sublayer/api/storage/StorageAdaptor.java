package com.sublayer.api.storage;

import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public class StorageAdaptor implements Storage {

    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String keyName) {
        // TODO Auto-generated method stub

    }

    @Override
    public String store(InputStream inputStream, String fileName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Stream<Path> loadAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Path load(String keyName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Resource loadAsResource(String keyName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(String keyName) {
        // TODO Auto-generated method stub

    }

    @Override
    public String generateUrl(String keyName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     */
    @Override
    public String[] store(InputStream[] inputStreams, String[] fileNames, String dirPath) {
        // TODO Auto-generated method stub
        return null;
    }
}
