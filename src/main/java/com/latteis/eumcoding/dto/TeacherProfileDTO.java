package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Lecture;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfileDTO {


    private int memberId;
    private int teacherId;
    private String teacherName;
    private String teacherProfileImage;
    private int totalLecture;
    private int totalStudent;

    //lectureList
    List<LectureDTO.profileDTO> lectureDTOList;

/*    private int lectureId;
    private String lectureName;
    private String lectureThumb;
    private int price;
    private int rating;*/


}
