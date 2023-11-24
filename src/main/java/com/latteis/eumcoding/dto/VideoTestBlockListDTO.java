package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.VideoTestBlockList;
import com.latteis.eumcoding.util.blockCoding.Block;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTestBlockListDTO {

    @Data
    @NoArgsConstructor
    @ApiModel(value = "동영상 블록 문제 DTO")
    public static class BlockResponseDTO {

        @ApiModelProperty(value = "블록 문제 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "제공될 블럭", example = "제공될 블럭")
        private String block;

        @ApiModelProperty(name = "제공될 블럭의 값", example = "제공될 블럭의 값")
        private String value; // 없어도 됨

        public BlockResponseDTO(VideoTestBlockList videoTestBlockList) {
            this.id = videoTestBlockList.getId();
            this.block = videoTestBlockList.getBlock();
            this.value = videoTestBlockList.getValue();
        }
    }

    private int videoTestBlockListId; // 사용자에게 고유하게 부여되는 값

    private int videoTestId;

    private String block; // 제공될 블럭

    @Data
    @NoArgsConstructor
    @ApiModel(value = "동영상 블록")
    public static class BlockList{
        @ApiModelProperty(value = "문제 id", example = "1")
        private int videoTestId;

        @ApiModelProperty(value = "블록 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "제공될 블럭 타입", example = "제공될 블럭 타입")
        private String block;

        @ApiModelProperty(value = "제공될 블럭의 값", example = "제공될 블럭의 값")
        private String value; // 없어도 됨

        public BlockList(VideoTestBlockList videoTestBlockList) {
            this.id = videoTestBlockList.getId();
            this.block = videoTestBlockList.getBlock();
            this.value = videoTestBlockList.getValue();
        }
    }

    @Data
    @NoArgsConstructor
    @ApiModel(value = "동영상 블록 문제 결과 요청 DTO")
    public static class TestResultRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "문제 ID", example = "1")
        private int videoTestId;

        @ApiModelProperty(value = "학생 답안 블럭", example = "")
        private List<Block> blockList;

    }

}
