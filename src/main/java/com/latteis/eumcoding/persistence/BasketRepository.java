package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Basket;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Integer> {
    Basket findByMemberIdAndLectureId(int memberId, int lectureId);

    @Query(value = "SELECT b.* FROM basket b " +
            "JOIN member m on b.member_id = m.id " +
            "JOIN lecture l on b.lecture_id = l.id " +
            "WHERE b.id =:id AND m.id =:memberId",nativeQuery = true)
    Basket findByBasketId(@Param("memberId") int memberId,@Param("id") int id);
    @Query("SELECT b FROM Basket b WHERE b.member = :member AND b.lecture = :lecture AND b.member.role = 0 AND b.member.state = 1")
    Optional<Basket> findByMemberAndLecture(@Param("member") Member member, @Param("lecture") Lecture lecture);

    // 아이디로 찾기
    Optional<Basket> findById(int id);

    @Query("SELECT b FROM Basket b WHERE b.member = :member AND b.member.role = 0 AND b.member.state = 1 ORDER BY b.id ASC")
    Page<Basket> findByMemberIdAndRoleAndState(@Param("member") Member member, Pageable pageable);

    @Query("SELECT count(b.lecture) FROM Basket b WHERE b.member = :member AND b.lecture = :lecture")
    int countByMemberAndLecture(@Param("member") Member member,@Param("lecture") Lecture lecture);
}
