package com.fakeco.instafake.repos;

import com.fakeco.instafake.models.StoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoryRepository extends JpaRepository<StoryModel, Long> {

    @Query(value = "SELECT * FROM story_model ORDER BY created_at DESC ", nativeQuery = true)
    List<StoryModel> findAllFromNewest();

}
