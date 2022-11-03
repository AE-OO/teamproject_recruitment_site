package com.goodjob.status.service;

import com.goodjob.post.postdto.PageResultDTO;
import com.goodjob.resume.Resume;
import com.goodjob.status.Status;
import com.goodjob.status.dto.ApplierListDTO;
import com.goodjob.status.dto.ApplyListDTO;
import com.goodjob.status.dto.SendMailDTO;
import com.goodjob.status.repository.StatusRepository;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService{

    private final StatusRepository statusRepository;

    @Override
    public void applyResume(Long postId, Long resumeId) {
        log.info("=========== 지원완료 ===========");
        statusRepository.save(dtoToEntity(postId, resumeId));
    }

    @Override
    public PageResultDTO<ApplyListDTO, Status> getApplyList(String loginId, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum,10);
        Page<Status> applyList = statusRepository.getStatusByStatResumeId_ResumeMemId_MemLoginIdOrderByStatApplyDateDesc(loginId, pageable);
        Function<Status, ApplyListDTO> fn = (entity -> entityToApplyListDTO(entity));
        return new PageResultDTO(applyList, fn);
    }

    @Override
    public PageResultDTO<ApplierListDTO, Status> getApplierList(String loginId, Long postId, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 10);
        Page<Status> applierList = statusRepository.getStatusByStatPostId_PostComId_ComLoginIdAndStatPostId_PostIdOrderByStatApplyDateDesc(loginId, postId, pageable);
        Function<Status, ApplierListDTO> fn = (entity -> entityToApplierListDTO(entity));
        return new PageResultDTO(applierList, fn);
    }

    @Override
    public void changePass(Long statId) {
        statusRepository.updateStatPass(statId);
    }

    @Override
    public void changeUnPass(Long statId) {
        statusRepository.updateStatUnPass(statId);
    }

    @Override
    public SendMailDTO getApplierToSendMail(Long statId) {
        log.info("=========== 메일발송 ===========");
        SendMailDTO sendMailDTO = entityToSendMailDTO(statusRepository.findOneApplier(statId));
        return sendMailDTO;
    }

    @Override
    public boolean havePass(String loginId) {
        return statusRepository.existsStatusByStatResumeId_ResumeMemId_MemLoginIdAndStatShowIsFalseAndStatPass(loginId,"서류합격");
    }

    @Override
    public void changeStatShow(String loginId) {
        List<Long> list = statusRepository.getStatId(loginId);
        for(Long statId : list){
            statusRepository.changeStatShow(statId);
        }
    }

}
