package com.interviewcoach.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads/profile-images")
public class ProfileImageFileController {

    @Value("${app.file.profile-upload-dir:uploads/profile-images}")
    private String profileUploadDir;

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(profileUploadDir)
                    .toAbsolutePath()
                    .normalize()
                    .resolve(fileName)
                    .normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String lowerFileName = fileName.toLowerCase();

            MediaType mediaType = MediaType.IMAGE_JPEG;

            if (lowerFileName.endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (lowerFileName.endsWith(".webp")) {
                mediaType = MediaType.parseMediaType("image/webp");
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
