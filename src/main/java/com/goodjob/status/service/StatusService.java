package com.goodjob.status.service;

import com.goodjob.post.Post;
import com.goodjob.resume.Resume;
import com.goodjob.status.Status;
import com.goodjob.status.dto.ApplyDTO;

/**
 * 박채원 22.10.26 작성
 */

public interface StatusService {
    void applyResume(Long postId, Long resumeId);

    default Status dtoToEntity(Long postId, Long resumeId){
        Post post = Post.builder().postId(postId).build();
        Resume resume = Resume.builder().resumeId(resumeId).build();

        Status status = Status.builder()
                .statPostId(post)
                .statResumeId(resume)
                .build();

        return status;
    }
}
