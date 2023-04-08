package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.VideoTestBlockList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoTestBlockListRepository extends JpaRepository<VideoTestBlockList, String> {
}
