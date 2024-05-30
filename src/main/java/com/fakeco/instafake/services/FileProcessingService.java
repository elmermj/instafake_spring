package com.fakeco.instafake.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        String[] parts = multipartFile.getContentType().split("/");
        String fileExt = "." + parts[1];
        String fullFileName = fileName + fileExt;

        Path path = Paths.get(basePath, fullFileName);

        try {
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

//            String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                    .path("/server/medias/")
//                    .path(fullFileName)
//                    .toUriString();
            String fileUri = "server/medias/"+fullFileName;

            System.out.println("File URI::::: " + fileUri);

            return ResponseEntity.ok(fileUri);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("ERROR");
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        // Construct the path to the file
        Path filePath = Paths.get("path_to_your_files_directory", fileName);

        try {
            // Load file as Resource
            Resource resource = new UrlResource(filePath.toUri());

            // Check if file exists
            if (resource.exists()) {
                // Set content type
                MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
                try {
                    mediaType = MediaType.parseMediaType(Files.probeContentType(filePath));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                // Return ResponseEntity with file content as body
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                // If file not found, return 404 Not Found response
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // If any exception occurs, return 500 Internal Server Error response
            return ResponseEntity.status(500).build();
        }
    }
}
