package com.sublayer.api.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.dto.NftMetadata;
import com.sublayer.api.entity.SubStorage;
import com.sublayer.api.storage.IpfsStorage;
import com.sublayer.api.storage.Storage;
import com.sublayer.api.utils.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Service
public class StorageService {
    private Storage storage;

    @Autowired
    private SubStorageService subStorageService;

    @Autowired
    private SubSystemService subSystemService;

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    /**
     *
     * @param inputStream
     * @param contentLength
     * @param contentType
     * @param fileName
     */
    public SubStorage store(InputStream inputStream, long contentLength, String contentType, String fileName) {
        if(storage instanceof IpfsStorage) {
            String key = generateKey(fileName);
            String fileHash = null;
            try {
                fileHash = storage.store(inputStream, key);
            } catch(Exception e) {
                throw new RuntimeException("Ipfs server not work");
            }
            if(null == fileHash) {
                return null;
            } else {
                SubStorage storageInfo = new SubStorage();
                storageInfo.setName(fileName);
                storageInfo.setSize((int) contentLength);
                storageInfo.setType(contentType);
                storageInfo.setKey(key);
                storageInfo.setUrl(this.storage.generateUrl(fileHash));
                storageInfo.setIpfshash("ipfs://ipfs/" + fileHash.substring(0, fileHash.indexOf(".")));
                subStorageService.add(storageInfo);
                return storageInfo;
            }
        } else {
            String key = generateKey(fileName);
            storage.store(inputStream, contentLength, contentType, key);

            String url = generateUrl(key);
            SubStorage storageInfo = new SubStorage();
            storageInfo.setName(fileName);
            storageInfo.setSize((int) contentLength);
            storageInfo.setType(contentType);
            storageInfo.setKey(key);
            storageInfo.setUrl(url);
            subStorageService.add(storageInfo);

            return storageInfo;
        }
    }

    public List<SubStorage> store(InputStream[] inputStreams, Long[] contentLengths, String[] contentTypes, String[] fileNames) {
        if(storage instanceof IpfsStorage) {
            String basePath = generateUUID();
            int len = fileNames.length;
            String[] keys = new String[len];
            keys[0] = "animation" + fileNames[0].substring(fileNames[0].lastIndexOf("."));
            keys[1] = "image" + fileNames[1].substring(fileNames[1].lastIndexOf("."));
            String[] hashFiles = null;
            try {
                hashFiles = storage.store(inputStreams, keys, basePath);
            } catch(Exception e) {
                throw new RuntimeException("Ipfs server not work");
            }
            if(null == hashFiles) {
                return null;
            } else {

                SubStorage storageInfo = null;
                List<SubStorage> list = new ArrayList<>();
                for(int i=0; i<len; i++) {
                    storageInfo = new SubStorage();
                    storageInfo.setName(fileNames[i]);
                    storageInfo.setSize(contentLengths[i].intValue());
                    storageInfo.setType(contentTypes[i]);
                    storageInfo.setKey(keys[i]);
                    storageInfo.setUrl(this.storage.generateUrl(hashFiles[i]));
                    storageInfo.setCreateTime(new Date());
                    storageInfo.setUpdateTime(new Date());
                    storageInfo.setIpfshash("ipfs://ipfs/" + hashFiles[i].substring(0, hashFiles[i].indexOf("/")));
                    subStorageService.add(storageInfo);
                    list.add(storageInfo);
                }
                return list;
            }
        } else {
            return null;
        }
    }

    private String generateKey(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');
        String suffix = originalFilename.substring(index);

        String key = null;
        SubStorage storageInfo = null;

        do {
            key = RandomUtil.getRandomString(20) + suffix;
            storageInfo = subStorageService.findByKey(key);
        }
        while (storageInfo != null);

        return key;
    }

    private String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public Stream<Path> loadAll() {
        return storage.loadAll();
    }

    public Path load(String keyName) {
        return storage.load(keyName);
    }

    public Resource loadAsResource(String keyName) {
        return storage.loadAsResource(keyName);
    }

    public void delete(String keyName) {
        storage.delete(keyName);
    }

    private String generateUrl(String keyName) {
        return storage.generateUrl(keyName);
    }
}
