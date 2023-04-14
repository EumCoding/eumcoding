package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    // 이메일로 찾기
    Member findByEmail(String email);

    // 해당하는 이메일이 있는지 확인
    Boolean existsByEmail (String email);

    // 이메일과 비밀번호로 찾기
    Member findByEmailAndPassword(String email, String password);

    // 아이디로 찾기
    Optional<Member> findById(int id);



    // 아이디로 비밀번호 가져오기
    @Query(value = "SELECT password FROM member WHERE id = :id", nativeQuery = true)
    String findPasswordByMemberId(@Param("id") int id);

    // 아이디로 닉네임 가져오기
    @Query(value = "SELECT nickname FROM member WHERE id = :id", nativeQuery = true)
    String findNicknameByMemberId(@Param("id") int id);



    // 아이디로 전화번호 가져오기
    @Query(value = "SELECT tel FROM member WHERE id = :id", nativeQuery = true)
    String findTelByMemberId(@Param("id") int id);

    // 같은 닉네임이 있는지 확인
    @Query(value = "SELECT COUNT(id) FROM member WHERE nickname = :nickname", nativeQuery = true)
    int findByNickname(@Param("nickname") String nickname);

    // 아이디로 이름 가져오기
    @Query(value = "SELECT name FROM member WHERE id = :id", nativeQuery = true)
    String findNameByMemberId(@Param("id") int id);

    // 비밀번호 답변 가져오기
    @Query(value = "SELECT answer FROM member WHERE id = :id", nativeQuery = true)
    String findAnswerByMemberId(@Param("id") int id);

    // 비밀번호 질문답변 가져오기
    @Query(value = "SELECT question, answer FROM member WHERE id = :id", nativeQuery = true)
    Member findQuestionAnswerByMemberId(@Param("id") int id);

    @Query(value = "SELECT * FROM member WHERE id = :id", nativeQuery = true)
    Member findByMemberId(@Param("id") int id);
}
