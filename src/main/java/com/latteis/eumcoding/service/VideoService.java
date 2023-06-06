package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Section;
import com.latteis.eumcoding.model.Video;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.SectionRepository;
import com.latteis.eumcoding.persistence.VideoRepository;
import com.latteis.eumcoding.util.MultipartUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    private final SectionRepository sectionRepository;

    private final MemberRepository memberRepository;

    @Value("${file.path.lecture.video.file}")
    private String videoFilePath;

    @Value("${file.path.lecture.video.thumb}")
    private String thumbFilePath;

    @Value("${ffprobe.location}")
    private String ffprobePath;

    public File getVideoFileDirectoryPath() {
        File file = new File(videoFilePath);
        file.mkdirs();

        return file;
    }

    public File getVideoThumbDirectoryPath() {
        File file = new File(thumbFilePath);
        file.mkdirs();

        return file;
    }

    /*
    * 비디오 정보 가져오기
    */
    public VideoDTO.ViewResponseDTO getVideoInfo(VideoDTO.IdRequestDTO idRequestDTO) {

        // 비디오 정보 가져오기
        Video video = videoRepository.findById(idRequestDTO.getId());
        Preconditions.checkNotNull(video, "등록된 비디오가 없습니다. (비디오 ID : %s)", idRequestDTO.getId());

        VideoDTO.ViewResponseDTO viewResponseDTO = new VideoDTO.ViewResponseDTO(video);
        viewResponseDTO.setPath("http://localhost:8081/eumCodingImgs/lecture/video/file/" + video.getPath());

        return viewResponseDTO;

    }

    // 동영상 업로드
    public void uploadVideo(int memberId, VideoDTO.UploadRequestDTO uploadRequestDTO, List<MultipartFile> videoFile, List<MultipartFile> thumb) throws IOException {

        // 섹션 정보 가져오기
        Section section = sectionRepository.findBySectionId(uploadRequestDTO.getSectionId());
        Preconditions.checkNotNull(section, "등록된 섹션이 없습니다. (섹션 ID : %s)", uploadRequestDTO.getSectionId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 해당 섹션에 존재하는 비디오 개수 구하기
        long videoCount = videoRepository.countBySectionId(section.getId());

        Video video = Video.builder()
                .section(section)
                .name(uploadRequestDTO.getName())
                .description(uploadRequestDTO.getDescription())
                .uploadDate(LocalDateTime.now())
                .preview(uploadRequestDTO.getPreview())
                .sequence((int) videoCount)
                .build();
        videoRepository.save(video);

        // 비디오 파일인지 구분
        boolean isVideoFile;

        // 동영상이 있을 경우
        if (videoFile != null && !videoFile.isEmpty()) {

            // 비디오 파일이라고 체크
            isVideoFile = true;

            // 동영상 한개만 저장
            MultipartFile multipartFile = videoFile.get(0);

            // 동영상 저장
            File newFile = saveFile(video.getPath(), video.getId(), getVideoFileDirectoryPath(), multipartFile, isVideoFile);

            // 이미지 파일명 DB에 저장
            video.setPath(newFile.getName());

            // 동영상 재생시간 받아와서 저장
            video.setPlayTime(getVideoPlayTime(newFile));
        }

        // 강의 썸네일 이미지가 있을 경우
        if (thumb != null && !thumb.isEmpty()) {

            // 비디오 파일이 아니라고 체크
            isVideoFile = false;

            // 이미지 한개만 저장
            MultipartFile multipartFile = thumb.get(0);

            // 이미지 저장
            File newFile = saveFile(video.getThumb(), video.getId(), getVideoThumbDirectoryPath(), multipartFile, isVideoFile);

            // 이미지 파일명 DB에 저장
            video.setThumb(newFile.getName());

        }

        videoRepository.save(video);

    }

    // 동영상 수정
    public void updateVideo(int memberId, VideoDTO.UpdateRequestDTO updateRequestDTO, List<MultipartFile> videoFile, List<MultipartFile> thumb) throws IOException {

        // 섹션 정보 가져오기
        Video oldVideo = videoRepository.findById(updateRequestDTO.getId());
        Preconditions.checkNotNull(oldVideo, "등록된 동영상이 없습니다. (동영상 ID : %s)", updateRequestDTO.getId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = oldVideo.getSection().getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader || member.getRole() == 2, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", oldVideo.getSection().getLecture().getId(), lectureUploader, memberId);

        Video video = Video.builder()
                .id(oldVideo.getId())
                .section(oldVideo.getSection())
                .name(updateRequestDTO.getName())
                .description(updateRequestDTO.getDescription())
                .uploadDate(oldVideo.getUploadDate())
                .preview(updateRequestDTO.getPreview())
                .build();
        videoRepository.save(video);

        // 비디오 파일인지 구분
        boolean isVideoFile;

        // 동영상이 있을 경우
        if (videoFile != null && !videoFile.isEmpty()) {

            // 비디오 파일이라고 체크
            isVideoFile = true;

            // 동영상 한개만 저장
            MultipartFile multipartFile = videoFile.get(0);

            // 동영상 저장
            File newFile = saveFile(oldVideo.getPath(), video.getId(), getVideoFileDirectoryPath(), multipartFile, isVideoFile);

            // 이미지 파일명 DB에 저장
            video.setPath(newFile.getName());

            // 동영상 재생시간 받아와서 저장
            video.setPlayTime(getVideoPlayTime(newFile));

        }
        else {
            // 새로운 동영상 파일이 없다면 기존 정보 입력
            video.setPath(oldVideo.getPath());
            video.setPlayTime(oldVideo.getPlayTime());
        }

        // 강의 썸네일 이미지가 있을 경우
        if (thumb != null && !thumb.isEmpty()) {

            // 비디오 파일이 아니라고 체크
            isVideoFile = false;

            // 이미지 한개만 저장
            MultipartFile multipartFile = thumb.get(0);

            // 이미지 저장
            File newFile = saveFile(oldVideo.getThumb(), video.getId(), getVideoThumbDirectoryPath(), multipartFile, isVideoFile);

            // 이미지 파일명 DB에 저장
            video.setThumb(newFile.getName());

        }
        else {
            // 새로운 썸네일 이미지가 없다면 기존 정보 저장
            video.setThumb(oldVideo.getThumb());
        }

        videoRepository.save(video);

    }

    // 비디오 삭제
    public void deleteVideo(int memberId, VideoDTO.IdRequestDTO idRequestDTO) {

        // 섹션 정보 가져오기
        Video video = videoRepository.findById(idRequestDTO.getId());
        Preconditions.checkNotNull(video, "등록된 동영상이 없습니다. (동영상 ID : %s)", idRequestDTO.getId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = video.getSection().getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader || member.getRole() == 2, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", video.getSection().getLecture().getId(), lectureUploader, memberId);

        // 동영상, 이미지 삭제
        deleteVideoFile(video.getPath(), getVideoFileDirectoryPath());
        deleteVideoFile(video.getThumb(), getVideoThumbDirectoryPath());

        videoRepository.delete(video);

    }

    // 파일 저장
    private File saveFile(String fileName, int videoId, File directoryPath, MultipartFile multipartFile, boolean isVideoFile) {

        // 기존 파일이 있다면 삭제
        if (fileName != null) {
            deleteVideoFile(fileName, directoryPath);
        }

        File newFile;

        if (isVideoFile){
            // 동영상 저장 (파일명 : "동영상 ID.확장자")
            newFile = MultipartUtils.saveVideo(multipartFile, directoryPath, String.valueOf(videoId));
        }
        else {
            // 파일 저장 (파일명 : "동영상 ID.확장자")
            newFile = MultipartUtils.saveImage(multipartFile, directoryPath, String.valueOf(videoId));
        }

        return newFile;

    }

    // 파일 삭제
    private void deleteVideoFile(String fileName, File directoryPath) {

        // 파일 삭제
        String imagePath = fileName;
        if(imagePath != null) {
            File oldImageFile = new File(directoryPath, imagePath);
            oldImageFile.delete();
        }

    }

    // 동영상 재생시간 구하기
    private LocalTime getVideoPlayTime(File file) throws IOException {

        // 동영상 파일의 메타데이터 읽기
        FFprobe fFprobe = new FFprobe(ffprobePath);
        FFmpegFormat format = fFprobe.probe(file.getAbsolutePath()).format;

        // 동영상 파일의 재생시간 가져오기
        double durationInSeconds = format.duration;
        long durationMillis = (long) (durationInSeconds * 1000);
        // LocalTime으로 변환
        return LocalTime.ofNanoOfDay(durationMillis * 1_000_000);

    }

}
