package com.fakeco.instafake.controllers;

import com.fakeco.instafake.dto.request.PostRequest;
import com.fakeco.instafake.dto.response.StoryResponse;
import com.fakeco.instafake.models.StoryModel;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.services.FileProcessingService;
import com.fakeco.instafake.services.StoryService;
import com.fakeco.instafake.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/story")
public class StoryController {

    @Autowired
    private StoryService storyService;

    @Autowired
    FileProcessingService fileProcessingService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<StoryResponse> createPost(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute PostRequest postRequest
    ) throws Exception {

        System.out.println(postRequest.getUsername()+ " " + "[File Name] "+file.getName()+ " |[SIZE] "+file.getSize() +" BYTES");

        UserModel user = userService.findByUsername(postRequest.getUsername());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        String filename = user.getUsername() +"_"+ formattedDateTime;

        ResponseEntity<String> uri = fileProcessingService.uploadFile(filename, file);

        StoryModel story = storyService.createStory(user, uri.getBody(), filename);

        String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(story.getFileName())
                .toUriString();

        StoryResponse storyResponse = new StoryResponse(story.getId(), story.getFileUrl(), user.getUsername(), user.getId(), story.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(storyResponse);
    }

    @PostMapping("/getStories")
    public ResponseEntity<List<StoryResponse>> getStories(
            String username
    ) throws Exception {
        List<StoryResponse> storyList = storyService.getStories().stream().map(
                storyModel -> {
                    UserModel user = userService.findById(storyModel.getUser().getId());
                    return new StoryResponse(
                            storyModel.getId(),
                            storyModel.getFileUrl(),
                            user.getUsername(),
                            user.getId(),
                            storyModel.getCreatedAt()
                    );
                }
        ).toList();

        return ResponseEntity.ok(storyList);
    }
}
