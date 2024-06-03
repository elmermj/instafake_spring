package com.fakeco.instafake.services;

import com.fakeco.instafake.dto.response.StoryResponse;
import com.fakeco.instafake.models.StoryModel;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.repos.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class StoryService {

    @Autowired
    private StoryRepository storyRepository;

    public List<StoryModel> getStories (){
        return storyRepository.findAllFromNewest();
    }

    public StoryModel createStory(UserModel user, String uri, String filename) throws IOException {

        StoryModel story = StoryModel.builder()
                .fileName(filename)
                .fileUrl(uri)
                .user(user).build();
        return storyRepository.save(story);
    }

}
