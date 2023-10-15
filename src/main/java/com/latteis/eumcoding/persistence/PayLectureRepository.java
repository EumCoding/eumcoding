package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.PayLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PayLectureRepository extends JpaRepository<PayLecture, Integer> {


    //해당 강의를 결제한 학생들을 파악하기 위해 사용
    //totalStudent구하기위해서, state 가 0이면 실패, 1이면 성공
    @Query("SELECT p1 FROM PayLecture p1 JOIN p1.payment p JOIN p1.lecture l WHERE l.id = :lectureId AND p.state = :state")
    List<PayLecture> findByLectureIdAndState(@Param("lectureId")int lectureId, @Param("state") int state);


    //해당 회원이 강의를 결제했는지 확인
    //이걸 이용해서 질문게시판에 질문을 남길 떄 회원이 결제한 강의에 대해서만 질문을 남길 수 있게 조건을 달기 위해 사용
    @Query("SELECT p FROM PayLecture p WHERE p.payment.member.id = :memberId AND p.payment.member.role = 0 AND p.payment.state = 1 AND p.lecture.id = :lectureId")
    Optional<PayLecture> findByMemberIdAndLectureId(int memberId, int lectureId);

    //결제 하려면 member는 role:0 회원 이어야하고, paymentLecture에있는 강좌는 lecture에 있는 강좌여야한다.
    //강좌의 state는 1이어야한다 이건 service부분에 조건을 걸어놓음
    //이미 결제된 강좌에대해서 중복 결제 못하게 막기위해 사용
    @Query("SELECT pl FROM PayLecture pl " +
            "JOIN pl.payment p " +
            "JOIN p.member m " +
            "JOIN pl.lecture l " +
            "WHERE m.id = :memberId AND l.id = :lectureId AND m.role = 0 AND p.state = 1")
    List<PayLecture> findByMemberAndLecture(@Param("memberId") int memberId, @Param("lectureId") int lectureId);


    /*
     * Member, Lecture, state에 맞는 엔티티 가져오기
     * */
    @Query("SELECT pl FROM PayLecture pl WHERE pl.payment.member = :member AND pl.lecture = :lecture AND pl.payment.state = :state")
    PayLecture findByMemberAndLectureAndState(@Param("member") Member member, @Param("lecture") Lecture lecture, @Param("state") int state);

    /*
     * Lecuter, state에 맞는 엔티티 수 가져오기
     */
    long countByLectureAndPaymentState(Lecture lecture, int state);

    @Query("SELECT pl FROM PayLecture pl JOIN pl.payment p JOIN pl.lecture l WHERE p.id = :paymentId AND pl.lecture.id = l.id")
    List<PayLecture> findByPaymentId(@Param("paymentId") int paymentId);

    //해당 학생이 결제 이력이 있는지 확인
    @Query("SELECT pl FROM PayLecture pl WHERE pl.payment.member.id = :memberId AND pl.payment.state = 1")
    List<PayLecture> findLecturesByStudentId(@Param("memberId") int memberId);

    /*
    * 이번 달 총 판매량 가져오기
    */
    @Query("SELECT COUNT(pl) FROM PayLecture pl " +
            "WHERE pl.lecture.member = :member " +
            "AND pl.payment.state = 1 " +
            "AND FUNCTION('YEAR', pl.payment.payDay) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND FUNCTION('MONTH', pl.payment.payDay) = FUNCTION('MONTH', CURRENT_DATE ) ")
    int cntTotalVolumeThisMonth(@Param("member") Member member);

    /*
    * 이번 달 총 수익 가져오기
    */
    @Query("SELECT SUM(pl.price) FROM PayLecture pl " +
            "WHERE pl.lecture.member = :member " +
            "AND pl.payment.state = 1 " +
            "AND FUNCTION('YEAR', pl.payment.payDay) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND FUNCTION('MONTH', pl.payment.payDay) = FUNCTION('MONTH', CURRENT_DATE ) ")
    String sumTotalRevenueThisMonth(@Param("member") Member member);

    /*
    * 이번 달 판매량 많은 순으로 가져오기
    */
 /*   @Query("SELECT pl.lecture.id, COUNT(pl.lecture.id) FROM PayLecture pl " +
            "WHERE pl.lecture.member = :member " +
            "AND pl.payment.state = 1 " +
            "AND FUNCTION('YEAR', pl.payment.payDay) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND FUNCTION('MONTH', pl.payment.payDay) = FUNCTION('MONTH', CURRENT_DATE ) ")
    List<Object[]> cntVolumeOrderByCnt(@Param("member") Member member);
*/

    /*
     * 이번 달 판매량 많은 순으로 가져오기
     */
 /*   @Query("SELECT pl.lecture.id, COUNT(pl.lecture.id) FROM Lecture l join PayLecture pl " +
            "WHERE l.member = :member " +
            "AND  " +
            "AND FUNCTION('YEAR', pl.payment.payDay) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND FUNCTION('MONTH', pl.payment.payDay) = FUNCTION('MONTH', CURRENT_DATE ) ")
    List<Object[]> cntVolumeOrderByCnt1(@Param("member") Member member);
*/
}
