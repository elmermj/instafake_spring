package com.fakeco.instafake.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileProcessingService {

    @Value("${file.upload-dir}")
    private String basePath;

    public List<String> fileList() {
        File dir = new File(basePath);
        File[] files = dir.listFiles();

        return files != null ? Arrays.stream(files).map(File::getName).collect(Collectors.toList()) : null;
    }

    public ResponseEntity<String> uploadFile(String fileName, MultipartFile multipartFile) {
        File dir = new File(basePath+fileName);

        if(dir.exists()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("ERROR");
        }
        String[] parts = multipartFile.getContentType().split("/");
        String fileExt = "."+ parts[1];

        Path path = Path.of(basePath+fileName+fileExt);

        try{
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(basePath)
                    .path(fileName)
                    .toUriString();

            return ResponseEntity.ok(fileUri);

        } catch (Exception e){

            System.out.println(e.getMessage());

        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("ERROR");

    }

    public Resource downloadFile(String fileName) {
        File dir = new File(basePath+fileName);
        try{
            if(dir.exists()){
                Resource resource = new UrlResource(dir.toURI());
                return resource;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

}
