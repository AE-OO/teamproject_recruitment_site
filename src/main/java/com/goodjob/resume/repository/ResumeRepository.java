package com.goodjob.resume.repository;

import com.goodjob.member.Member;
import com.goodjob.resume.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 박채원 22.10.02 작성
 */

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Resume findByResumeId(Long resumeId);

    @Transactional
    @Modifying
    @Query("update Resume r set r.resumeMemPhone =:memPhoneNum, r.resumeMemEmail =:memEmail, r.resumeMemAddress =:memAddress where r.resumeId =:resumeId")
    void updateMemberInfo(String memPhoneNum, String memEmail, String memAddress, Long resumeId);

    List<Resume> getResumeByResumeMemIdOrderByResumeId(Member member);

    @Transactional
    void deleteByResumeId(Long resumeId);
}
