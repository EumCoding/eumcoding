package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.LectureProgressDTO;
import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.dto.VideoProgressDTO;
import com.latteis.eumcoding.dto.payment.PaymentDTO;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
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

    private final LectureProgressRepository lectureProgressRepository;

    private final PayLectureRepository payLectureRepository;

    private final VideoProgressRepository videoProgressRepository;

    @Value("${file.path.lecture.video.file}")
    private String videoFilePath;

    @Value("${file.path.lecture.video.thumb}")
    private String thumbFilePath;

    @Value("${ffprobe.location}")
    private String ffprobePath;

    @Value("${server.domain}")
    private String domain;

    @Value("${server.port}")
    private String port;

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
    public VideoDTO.ViewResponseDTO getVideoInfo(int memberId, VideoDTO.IdRequestDTO idRequestDTO) {

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 비디오 정보 가져오기
        Video video = videoRepository.findById(idRequestDTO.getId());
        Preconditions.checkNotNull(video, "등록된 비디오가 없습니다. (비디오 ID : %s)", idRequestDTO.getId());

        // 비디오 경로 추가
        VideoDTO.ViewResponseDTO viewResponseDTO = new VideoDTO.ViewResponseDTO(video);
        viewResponseDTO.setPath(domain + port + "/eumCodingImgs/lecture/video/file/" + video.getPath());

        // 구매한 이력이 있거나 미리보기 허용인지 검사
        PayLecture payLecture = payLectureRepository.findByMemberAndLectureAndState(member, video.getSection().getLecture(), PaymentDTO.PaymentState.SUCCESS);
        Preconditions.checkArgument(payLecture != null || video.getPreview() == VideoDTO.VideoPreview.POSSIBLE, "비디오를 시청할 권한이 없습니다");

        // 비디오 시청 기록 가져와서 추가
        VideoProgress videoProgress = videoProgressRepository.findByMemberAndVideo(member, video);
        VideoProgressDTO.ViewedResultResponseDTO viewedResultResponseDTO = (videoProgress == null) ? null : new VideoProgressDTO.ViewedResultResponseDTO(videoProgress);
        viewResponseDTO.setViewedResultResponseDTO(viewedResultResponseDTO);

        // 해당 강의를 구매한 이력은 있고 수강 기록은 없다면 수강기록과 비디오 기록 생성
        LectureProgress lectureProgress = lectureProgressRepository.findByMemberAndLecture(member, video.getSection().getLecture());
        if (payLecture != null && lectureProgress == null) {
            // lectureProgress 생성 후 저장
            LectureProgress newLectureProgress = LectureProgress.builder()
                            .payLecture(payLecture)
                            .state(LectureProgressDTO.LectureProgressState.STUDYING)
                            .startDay(LocalDateTime.now())
                            .build();
            lectureProgressRepository.save(newLectureProgress);

            // videoProgress 생성 후 저장
            videoProgressRepository.save(VideoProgress.builder()
                    .lectureProgress(newLectureProgress)
                    .video(video)
                    .state(LectureProgressDTO.LectureProgressState.STUDYING)
                    .startDay(LocalDateTime.now())
                    .build());
        }
        // 해당 강의를 구매한 이력은 있고 해당 비디오 시청 기록이 없다면
        else if (payLecture != null && lectureProgress != null && videoProgressRepository.findByVideoAndLectureProgress(video, lectureProgress) == null) {
            // 비디오 시청 기록 생성 후 저장
            videoProgressRepository.save(VideoProgress.builder()
                    .lectureProgress(lectureProgress)
                    .video(video)
                    .state(LectureProgressDTO.LectureProgressState.STUDYING)
                    .startDay(LocalDateTime.now())
                    .build());
        }

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

        // 삭제할 비디오보다 뒤 순서에 있는 비디오들 순서 당기기
        List<Video> videoList = videoRepository.findAllBySectionAndSequenceGreaterThan(video.getSection(), video.getSequence());
        videoList.forEach(video1 -> {
            video1.setSequence(video1.getSequence() - 1);
            videoRepository.save(video1);
        });

        videoRepository.delete(video);

    }


    /*
    * 비디오 순서 앞으로 이동
    */
    public void updateSequenceUp(int memberId, VideoDTO.IdRequestDTO idRequestDTO) {

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // Section 가져오기
        Video video = videoRepository.findByIdAndSectionLectureMember(idRequestDTO.getId(), member);
        Preconditions.checkNotNull(video, "등록된 비디오가 아닙니다. (video ID : %s)", idRequestDTO.getId());

        // 강사 회원인지 검사
        Preconditions.checkArgument((member.getRole() == MemberDTO.MemberRole.TEACHER) || (member.getRole() == MemberDTO.MemberRole.ADMIN), "강사나 관리자 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 해당 섹션의 앞 순서 섹션 가져오기
        Video frontVideo = videoRepository.findBySectionAndSequence(video.getSection(), video.getSequence() - 1);
        Preconditions.checkNotNull(frontVideo, "앞 순서 비디오가 없습니다. (video ID : %s)", idRequestDTO.getId());

        // 순서 + 1
        frontVideo.setSequence(frontVideo.getSequence() + 1);
        // 저장
        videoRepository.save(frontVideo);

        // 기존 비디오 순서 - 1
        video.setSequence(video.getSequence() - 1);
        // 저장
        videoRepository.save(video);

    }

    /*
    * 비디오 순서 뒤로 이동
    */
    public void updateSequenceDown(int memberId, VideoDTO.IdRequestDTO idRequestDTO) {

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // video 가져오기
        Video video = videoRepository.findByIdAndSectionLectureMember(idRequestDTO.getId(), member);
        Preconditions.checkNotNull(video, "등록된 비디오가 아닙니다. (video ID : %s)", idRequestDTO.getId());

        // 강사 회원인지 검사
        Preconditions.checkArgument((member.getRole() == MemberDTO.MemberRole.TEACHER) || (member.getRole() == MemberDTO.MemberRole.ADMIN), "강사나 관리자 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 해당 섹션의 앞 순서 섹션 가져오기
        Video backVideo = videoRepository.findBySectionAndSequence(video.getSection(), video.getSequence() + 1);
        Preconditions.checkNotNull(backVideo, "뒷 순서 비디오가 없습니다. (video ID : %s)", idRequestDTO.getId());

        // 순서 + 1
        backVideo.setSequence(backVideo.getSequence() - 1);
        // 저장
        videoRepository.save(backVideo);

        // 기존 비디오 순서 - 1
        video.setSequence(video.getSequence() + 1);
        // 저장
        videoRepository.save(video);

    }

    /*
    * 동영상 시청 결과 저장
    * */
    public void saveViewedResult(int memberId, VideoProgressDTO.ViewedResultRequestDTO viewedResultRequestDTO) {

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // video 가져오기
        Video video = videoRepository.findById(viewedResultRequestDTO.getVideoId());
        Preconditions.checkNotNull(video, "등록된 비디오가 아닙니다. (video ID : %s)", viewedResultRequestDTO.getVideoId());

        // 수강 중인 강의인지 검사
        LectureProgress lectureProgress = lectureProgressRepository.findByMemberAndLecture(member, video.getSection().getLecture());
        Preconditions.checkNotNull(lectureProgress, "해당 강의를 수강하고 있지 않습니다. (강의 ID : %s)", video.getSection().getLecture().getId());

        // 해당 비디오 시청 기록 가져오기
        VideoProgress videoProgress = videoProgressRepository.findByVideoAndLectureProgress(video, lectureProgress);
        // 받아온 마지막 영상 위치가 비디오 전체 시간과 같다면 수강 완료
        if (video.getPlayTime().equals(viewedResultRequestDTO.getLastView())) {
            videoProgress.setState(VideoProgressDTO.VideoProgressState.COMPLETION);
            videoProgress.setEndDay(LocalDateTime.now());
        }
        videoProgress.setLastView(viewedResultRequestDTO.getLastView());
        videoProgressRepository.save(videoProgress);

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
