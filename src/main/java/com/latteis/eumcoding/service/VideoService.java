package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.LectureProgressDTO;
import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.dto.VideoProgressDTO;
import com.latteis.eumcoding.dto.payment.PaymentDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.*;
import com.latteis.eumcoding.util.FileUploadProgressListener;
import com.latteis.eumcoding.util.MultipartUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * The type Video service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);


    @Autowired
    private FileUploadProgressListener progressListener;

    @Autowired
    private HttpSession httpSession;

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

    // 진행률 리스너를 이용한 동영상 업로드
    public void uploadVideoWithProgress(int memberId, VideoDTO.UploadRequestDTO uploadRequestDTO,
                                        List<MultipartFile> videoFile, List<MultipartFile> thumb) throws IOException {
        try{
            // 진행률 리스너를 세션에 추가
            httpSession.setAttribute("uploadProgressListener", progressListener);

            // 기존의 비디오 업로드 로직...
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

            /*// 동영상이 있을 경우
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

            }*/

            // 동영상이 있을 경우
            if (videoFile != null && !videoFile.isEmpty()) {
                MultipartFile multipartFile = videoFile.get(0); // 동영상 한개만 저장
                if(multipartFile.getSize() <= 0 || multipartFile.getOriginalFilename() == null) {
                    logger.error("Invalid video file provided");
                    return;  // or throw an exception
                }
                // 비디오 파일이라고 체크
                isVideoFile = true;

                // 동영상 저장
                File newFile = saveFile(video.getPath(), video.getId(), getVideoFileDirectoryPath(), multipartFile, isVideoFile);

                // 이미지 파일명 DB에 저장
                video.setPath(newFile.getName());

                // 동영상 재생시간 받아와서 저장
                video.setPlayTime(getVideoPlayTime(newFile));
            }else{
                logger.error("동영상 업로드 : 동영상이 들어오지 않음");
            }

            // 강의 썸네일 이미지가 있을 경우
            if (thumb != null && !thumb.isEmpty()) {
                MultipartFile multipartFile = thumb.get(0); // 이미지 한개만 저장
                if(multipartFile.getSize() <= 0 || multipartFile.getOriginalFilename() == null) {
                    logger.error("Invalid thumbnail file provided");
                    return;  // or throw an exception
                }
                // 비디오 파일이 아니라고 체크
                isVideoFile = false;

                // 이미지 저장
                File newFile = saveFile(video.getThumb(), video.getId(), getVideoThumbDirectoryPath(), multipartFile, isVideoFile);

                // 이미지 파일명 DB에 저장
                video.setThumb(newFile.getName());
            }

            videoRepository.save(video);

            // 업로드가 완료되면 진행률 리스너를 세션에서 제거
            httpSession.removeAttribute("uploadProgressListener");
        }catch(Exception e){
            logger.error("Error uploading video", e);  // 로그 추가
            e.printStackTrace();

        }

    }

    /*
    * 비디오 정보 가져오기
    */
    @Transactional
    public VideoDTO.ViewResponseDTO getVideoInfo(int memberId, VideoDTO.IdRequestDTO idRequestDTO) {

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 비디오 정보 가져오기
        Video video = videoRepository.findById(idRequestDTO.getId());
        Preconditions.checkNotNull(video, "등록된 비디오가 없습니다. (비디오 ID : %s)", idRequestDTO.getId());

        // 비디오 정보로 Lecture 가져오기
        Lecture lecture = video.getSection().getLecture();
        Section section = video.getSection();

        // 비디오 경로 추가
        VideoDTO.ViewResponseDTO viewResponseDTO = new VideoDTO.ViewResponseDTO(video);
        viewResponseDTO.setPath(domain + port + "/eumCodingImgs/lecture/video/file/" + video.getPath());

        // 구매한 이력이 있거나 미리보기 허용인지 검사 + 내가 만든 강의인지 검사
        PayLecture payLecture = payLectureRepository.findByMemberAndLectureAndState(member, video.getSection().getLecture(), PaymentDTO.PaymentState.SUCCESS);
        if(lecture.getMember().getId() != memberId){
            Preconditions.checkArgument(payLecture != null || video.getPreview() == VideoDTO.VideoPreview.POSSIBLE, "비디오를 시청할 권한이 없습니다");
        }

        // 비디오 시청 기록이 없다면
        if (!videoProgressRepository.existsByVideoAndLectureProgressPayLecturePaymentMember(video, member)) {
            // 비디오 순서가 처음이 아니라면
            if (video.getSequence() != 0) {
                // 이전 순서 비디오 가져오기
                Video previousVideo = videoRepository.findBySectionAndSequence(section, video.getSequence() - 1);
                // 이전 순서 비디오 시청 기록 가져오기
                VideoProgress previousVideoProgress = videoProgressRepository.findByVideoAndLectureProgressPayLecturePaymentMember(previousVideo, member);
                // 기록이 없거나 완료하지 않았다면 거부
                if ((previousVideoProgress == null || previousVideoProgress.getState() == VideoProgressDTO.VideoProgressState.STUDYING) && member.getRole() == 0) {
                    throw new ResponseMessageException(ErrorCode.VIDEO_PRECONDITION_FAILED);
                }
            }
            // 비디오 순서가 처음이라면
            else {
                // 해당 비디오의 섹션의 순서가 처음이 아니라면
                if (section.getSequence() != 0) {
                    // 이전 순서 섹션 가져오기
                    Section previousSection = sectionRepository.findByLectureAndSequence(lecture, section.getSequence() - 1);
                    // 이전 순서 섹션의 마지막 순서인 비디오 가져오기
                    Video lastSequenceVideo = videoRepository.findTopBySectionIdOrderBySequenceDesc(previousSection.getId());
                    // 마지막 순서 비디오의 기록이 없거나 완료하지 않았다면
                    VideoProgress videoProgress = videoProgressRepository.findByVideoAndLectureProgressPayLecturePaymentMember(lastSequenceVideo, member);
                    if (videoProgress == null || videoProgress.getState() == VideoProgressDTO.VideoProgressState.STUDYING) {
                        throw new ResponseMessageException(ErrorCode.VIDEO_PRECONDITION_FAILED);
                    }
                }
            }
        } // 모두 통과하면 자격 충족




        // 비디오 시청 기록 가져와서 추가
        List<VideoProgress> videoProgressList = videoProgressRepository.findByMemberAndVideo(member, video);
        if(videoProgressList.size() > 1){
            for(int i = 1 ; i < videoProgressList.size() ; i++) {
                videoProgressRepository.deleteById(videoProgressList.get(i).getId());
            }
        }
        VideoProgress videoProgress = (videoProgressList == null || videoProgressList.size() == 0) ? null : videoProgressList.get(0);
        VideoProgressDTO.ViewedResultResponseDTO viewedResultResponseDTO = (videoProgress == null) ? null : new VideoProgressDTO.ViewedResultResponseDTO(videoProgress);
        viewResponseDTO.setViewedResultResponseDTO(viewedResultResponseDTO);

        LectureProgress lectureProgress = null;
        // 해당 강의를 구매한 이력은 있고 수강 기록은 없다면 수강기록과 비디오 기록 생성
        List<LectureProgress> lectureProgressList = lectureProgressRepository.findByMemberAndLecture(member, lecture);
        if(lectureProgressList != null && lectureProgressList.size() == 0){
            Preconditions.checkNotNull("해당 강의를 수강중인 학생이 아닙니다. (학생 ID : %s)", member.getId());
        }else{
            lectureProgress = lectureProgressList.get(0);
        }
        if(lectureProgressList.size() >= 2) {
            for(int i = 1 ; i < lectureProgressList.size() ; i++) {
                lectureProgressRepository.deleteById(lectureProgressList.get(i).getId());
            }
        }
        if (payLecture != null && lectureProgress == null) {
            // lectureProgress 생성 후 저장
            LectureProgress newLectureProgress = LectureProgress.builder()
                            .payLecture(payLecture)
                            .state(LectureProgressDTO.LectureProgressState.STUDYING)
                            .startDay(LocalDateTime.now())
                            .build();
            List<LectureProgress> tempLectureProgress = lectureProgressRepository.findByMemberAndLecture(member, video.getSection().getLecture());
            if(tempLectureProgress.size() == 0)
                lectureProgressRepository.save(newLectureProgress);

            VideoProgress newVideoProgress = VideoProgress.builder()
                    .lectureProgress(newLectureProgress)
                    .video(video)
                    .state(LectureProgressDTO.LectureProgressState.STUDYING)
                    .startDay(LocalDateTime.now())
                    .build();

            List<VideoProgress> tempVideoProgress = videoProgressRepository.findByVideoAndLectureProgress(video, newLectureProgress);
            if(tempVideoProgress.size() == 0){
                videoProgressRepository.save(newVideoProgress);
            }

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

        // 수강 중인 강의인지 검사(List로받아오기)
        List<LectureProgress> lectureProgress = lectureProgressRepository.findByMemberAndLecture(member, video.getSection().getLecture());

        // lectureProgress가 여러개있으면 하나만 남기고 지우기
        if(lectureProgress.size() > 1){
            for(int i = 1 ; i < lectureProgress.size() ; i++) {
                lectureProgressRepository.deleteById(lectureProgress.get(i).getId());
            }
        }

        // video와 member로 조회
        List<VideoProgress> videoProgressList = videoProgressRepository.findByVideoIdAndMemberId(video.getId(), member.getId());

        // videoProgressList가 여러개 있으면 하나만 남기고 지우기
        if(videoProgressList.size() > 1){
            for(int i = 1 ; i < videoProgressList.size() ; i++) {
                videoProgressRepository.deleteById(videoProgressList.get(i).getId());
            }
        }

        // videoProgress가 없으면 생성
        if(videoProgressList.size() == 0){
            // videoProgress 생성
            VideoProgress newVideoProgress = VideoProgress.builder()
                    .lectureProgress(lectureProgress.get(0))
                    .video(video)
                    .state(LectureProgressDTO.LectureProgressState.STUDYING)
                    .startDay(LocalDateTime.now())
                    .build();
            videoProgressRepository.save(newVideoProgress);
            Preconditions.checkNotNull("해당 강의를 수강중인 학생이 아닙니다. (학생 ID : %s)", member.getId());
        }

//        if(lectureProgress.size() == 0){
//            log.info("수강중인 강의가 없음... progress 생성")
//            // lectureProgress와 videoProgress 생성
//            LectureProgress newLectureProgress = LectureProgress.builder()
//                    .payLecture(payLectureRepository.findByMemberAndLectureAndState(member, video.getSection().getLecture(), PaymentDTO.PaymentState.SUCCESS))
//                    .state(LectureProgressDTO.LectureProgressState.STUDYING)
//                    .startDay(LocalDateTime.now())
//                    .build();
//            lectureProgressRepository.save(newLectureProgress);
//            // videoProgress 생성
//            VideoProgress newVideoProgress = VideoProgress.builder()
//                    .lectureProgress(newLectureProgress)
//                    .video(video)
//                    .state(LectureProgressDTO.LectureProgressState.STUDYING)
//                    .startDay(LocalDateTime.now())
//                    .build();
//            videoProgressRepository.save(newVideoProgress);
//            Preconditions.checkNotNull("해당 강의를 수강중인 학생이 아닙니다. (학생 ID : %s)", member.getId());
//        }
//        // 수강 중인 강의인지 검사
//        List<LectureProgress> lectureProgress = lectureProgressRepository.findByMemberAndLecture(member, video.getSection().getLecture());
//        Preconditions.checkNotNull(lectureProgress, "해당 강의를 수강하고 있지 않습니다. (강의 ID : %s)", video.getSection().getLecture().getId());

        VideoProgress videoProgress1 = null;
        // 해당 비디오 시청 기록 가져오기
        List<VideoProgress> videoProgress = videoProgressRepository.findByVideoIdAndMemberId(video.getId(), member.getId());
        if(videoProgress.size() == 0){
            Preconditions.checkNotNull("해당 비디오를 시청하고 있지 않습니다. (비디오 ID : %s)", video.getId());
        }else{
             videoProgress1 = videoProgress.get(0);
        }
        if(videoProgress.size() > 1) {
            for(int i = 1 ; i < videoProgress.size() ; i++) {
                videoProgressRepository.deleteById(videoProgress.get(i).getId());
            }
        }
        // 받아온 마지막 영상 위치가 비디오 전체 시간과 같다면 수강 완료
//        if (video.getPlayTime().equals(viewedResultRequestDTO.getLastView())) {
//            videoProgress1.setState(VideoProgressDTO.VideoProgressState.COMPLETION);
//            videoProgress1.setEndDay(LocalDateTime.now());
//        }

        // 받아온 마지막 영상 위치가 비디오 전체 시간의 90% 이상이라면 수강 완료
        if (video.getPlayTime().toSecondOfDay() * 0.9 <= viewedResultRequestDTO.getLastView().toSecondOfDay()) {
            videoProgress1.setState(VideoProgressDTO.VideoProgressState.COMPLETION);
            videoProgress1.setEndDay(LocalDateTime.now());
        }
        videoProgress1.setLastView(viewedResultRequestDTO.getLastView());
        videoProgressRepository.save(videoProgress1);

    }

    // 파일 저장
    private File saveFile(String fileName, int videoId, File directoryPath, MultipartFile multipartFile, boolean isVideoFile) {
        try{
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
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
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


    /**
     * 마지막으로 시청한 영상 ID를 반환
     * @param authentication 로그인 정보
     * @return Video ID (기록이 없으면 0 return)
     */
    public int getLastViewVideoID(Authentication authentication) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Member member = memberRepository.findByMemberId(memberId);

        // 등록된 회원인지 검사
        if (member == null) {
            throw new ResponseMessageException(ErrorCode.USER_UNREGISTERED);
        }

        VideoProgress lastViewVideoID = videoProgressRepository.findTopByLectureProgressPayLecturePaymentMemberOrderByStartDayDesc(member);

        if (lastViewVideoID == null) {
            return 0;
        }

        return lastViewVideoID.getVideo().getId();

    }

}
