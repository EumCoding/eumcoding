package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.VideoTestBlockList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

        public BlockResponseDTO(VideoTestBlockList videoTestBlockList) {
            this.id = videoTestBlockList.getId();
            this.block = videoTestBlockList.getBlock();
        }
    }

    private int videoTestBlockListId; // 사용자에게 고유하게 부여되는 값

    private int videoTestId;

    private String block; // 제공될 블럭
}
