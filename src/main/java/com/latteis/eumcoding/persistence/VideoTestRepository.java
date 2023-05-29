package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.dto.VideoTestDTO;
import com.latteis.eumcoding.model.Video;
import com.latteis.eumcoding.model.VideoTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface VideoTestRepository extends JpaRepository<VideoTest, Integer> {


    List<VideoTest> findAllByVideoOrderByTestTime(Video video);

    VideoTest findById(int videoTestId);

    boolean existsByVideoIdAndTestTime(int videoId, LocalTime testTime);

//    @Query("select new com.latteis.eumcoding.dto.VideoTestDTO.ListResponseDTO(vt.id, vt.testTime, vt.title, vt.type, vt.score) from VideoTest vt ")
//    List<VideoTestDTO.ListResponseDTO> getList();

}
