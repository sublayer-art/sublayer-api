package com.sublayer.api.controller;

import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.R;
import com.sublayer.api.entity.SubStorage;
import com.sublayer.api.service.StorageService;
import com.sublayer.api.service.SubStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/storage")
@Tag(name = "Storage Controller", description = "Storage operations")
public class SubStorageController extends BaseController{
    @Autowired
    private StorageService storageService;

    @Autowired
    private SubStorageService subStorageService;

    /**
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    @Operation(summary="Upload file", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object upload(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        SubStorage fcStorage = storageService.store(file.getInputStream(), file.getSize(), file.getContentType(), originalFilename);
        if(null == fcStorage) {
            return R.fail();
        } else {
            return R.ok(fcStorage);
        }
    }

    @PostMapping("/multiupload")
    @Operation(summary="Batch upload file", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public Object multiUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
        int len = files.length;
        String[] fileNames = new String[len];
        InputStream[] inputStreams = new InputStream[len];
        Long[] contentLengths = new Long[len];
        String[] contentTypes = new String[len];
        int i=0;
        for(MultipartFile file: files) {
            fileNames[i] = file.getOriginalFilename();
            inputStreams[i] = file.getInputStream();
            contentLengths[i] = file.getSize();
            contentTypes[i] = file.getContentType();
            i++;
        }
        List<SubStorage> list = storageService.store(inputStreams, contentLengths, contentTypes, fileNames);
        if(null == list) {
            return R.fail();
        } else {
            return R.ok(list);
        }
    }

    /**
     *
     * @param key
     * @return
     */
    @PostMapping("/fetch/{key:.+}")
    @Operation(summary="Fetch file", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public ResponseEntity<Resource> fetch(@PathVariable String key) {
        SubStorage subStorage = subStorageService.findByKey(key);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        if (key.contains("../")) {
            return ResponseEntity.badRequest().build();
        }
        String type = subStorage.getType();
        MediaType mediaType = MediaType.parseMediaType(type);

        Resource file = storageService.loadAsResource(key);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(mediaType).body(file);
    }

    /**
     *
     * @param key
     * @return
     */
    @PostMapping("/download/{key:.+}")
    @Operation(summary="Download file", security={@SecurityRequirement(name= Constants.WEB_TOKEN_NAME)})
    public ResponseEntity<Resource> download(@PathVariable String key) {
        SubStorage fcStorage = subStorageService.findByKey(key);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        if (key.contains("../")) {
            return ResponseEntity.badRequest().build();
        }

        String type = fcStorage.getType();
        MediaType mediaType = MediaType.parseMediaType(type);

        Resource file = storageService.loadAsResource(key);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(mediaType).header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
