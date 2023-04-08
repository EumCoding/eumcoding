package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.VideoTestMultipleList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoTestMultipleListRepository extends JpaRepository<VideoTestMultipleList, String> {
}
