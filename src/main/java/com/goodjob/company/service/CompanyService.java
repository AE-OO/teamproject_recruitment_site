/**
 * HO - 2022.10.05
 * 서비스 클래스
 * 기업회원 회원가입 메서드
 * + 2022.10.06 회원가입시 아이디 중복확인 메서드
 *
 * +2022.10.17
 * 로그인 폼 기업/개인 통일 위해 로그인, 비밀번호 창 name 통일. 필드도 loginId,pw 통일(38라인 변경)
 */
package com.goodjob.company.service;

import com.goodjob.company.Comdiv;
import com.goodjob.company.Company;
import com.goodjob.company.Region;
import com.goodjob.company.repository.CompanyRepository;
import com.goodjob.company.dto.CompanyDTO;
import com.goodjob.company.repository.RegionRepository;
import com.goodjob.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CompanyService {
    private final CompanyRepository companyRepository;

    //22.10.09 - 비밀번호 암호화를 위해 추가
    private final PasswordEncoder passwordEncoder;
    private final RegionRepository regionRepository;


    //비밀번호 변경
    public void changePw(CompanyDTO companyDTO,String comLoginId){
        companyDTO.setPw(passwordEncoder.encode(companyDTO.getPw()));
        log.info("???: 서비스 changePw에서 암호화하는과정에서 에러일가?");
        Company company = companyDTO.toEntityForFindId();
        log.info("???: toEntity에서나는 에러같은데");
        log.info("???: 로그인 아이디 뭐 받아오는거지" + company.getComLoginId());
        log.info("???: 로그인 아이디 뭐 받아오는거지" + comLoginId);

        companyRepository.updatePassword(company.getComPw(),comLoginId);

    }

    //아이디 찾기 10.30일날 한 듯. DTO로 처음부터 받았는데 String으로 name,email 받아서 DTO로 변경하기로 함. 224라인에 새로 메서드 만듦.
//    public String findId2(CompanyDTO companyDTO) {
//        Company company1 = companyDTO.toEntityForFindId();
//        String name = company1.getComName();
//        log.info("??? name 받아오는 값: " + name);
//        String email = company1.getComEmail();
//        log.info("??? email 받아오는 값: " + email);
//
//        Long num = companyRepository.countByComNameAndComEmail(name,email);
//        log.info("??? name과 emial로 찾아온 갯수 : "+num);
//        if(num == 0){
//            return "fail";
//        } else {
//            Optional<Company> company = companyRepository.findByComNameAndComEmail(name, email);
//            log.info("???: "+ email +"email일부일텐데??? "+ company.get().getComEmail());
//            return company.get().getComLoginId();
//        }
//
//    }

    //22.10.29 아이디 찾기
    public String findId(CompanyDTO companyDTO){

        Company company1 = companyDTO.toEntityForFindId();
        Long num = companyRepository.countByComName(company1.getComName());
        if(num == 0) {
            return "fail";
        } else {
            Optional<Company> company = companyRepository.findByComName(company1.getComName());
            return company.get().getComLoginId();
        }

    }


    //기업회원가입정보 DB에 저장하는 메서드
    @Transactional
    public Long createCompanyUser(CompanyDTO companyDTO) {
        //22.10.09 - 비밀번호 암호화를 위해 추가
        //companyDTO를 가져와서 여기서 비밀번호를 암호화하는 방법.
        //ho - 22.10.17 getcomPw1 -> getPw (로그인 폼 input name 통일. DTO 필드 loginId,pw 로 통일)
        companyDTO.setPw(passwordEncoder.encode(companyDTO.getPw()));

        Company newCompanyUser = companyDTO.toEntity();
        companyRepository.save(newCompanyUser);
        return newCompanyUser.getComId();
    }

    //아이디 중복확인 메서드
    public int checkId2(String comLoginId) throws Exception {
        return companyRepository.checkId2(comLoginId);
    }

    public Optional<Company> loginIdCheck(String comLoginId) {
        return companyRepository.findByComLoginId(comLoginId);
    }

//    public CompanyDTO entityToDTO(String comLoginId, String comName, String comBusiNum, String comPhone,
//                                  String comComdivCode, String comRegCode, String comEmail1, String comEmail2,
//                                  String comAddress1, String comAddress2, String comAddress3,
//                                  String comAddress4, String comInfo){
//
//        CompanyDTO dto = CompanyDTO.builder()
//                .loginId(comLoginId)
//                .comName(comName)
//                .comBusiNum(comBusiNum)
//                .comPhone(comPhone)
//                .comComdivCode(comComdivCode)
//                .comRegCode(comRegCode)
//                .comEmail1(comEmail1+"@")
//                .comEmail2(comEmail2)
//                .comAddress1(comAddress1)
//                .comAddress2(comAddress2)
//                .comAddress3(comAddress3)
//                .comAddress4(comAddress4)
//                .comInfo(comInfo)
//                .build();
//
//        return dto;
//    }

    public CompanyDTO entityToDTO2(Company company) {
        String comLoginId = company.getComLoginId();
        String comName = company.getComName();
        String comBusiNum = company.getComBusiNum();
        String comPhone = company.getComPhone();
        Comdiv comComdivCode = company.getComComdivCode();
        Region comRegCode = company.getComRegCode();

        String comEmail = company.getComEmail();
        String[] email = comEmail.split("@");

        String comAddress = company.getComAddress();
        String[] address = comAddress.split("@");

        /** 2022.10.25 - 주소 4의 값 없을 때 보여줄 때 에러 발생 -> null일때  "null"을 DB에 넣기로 함. */

        log.info("==============="+address.length);

        String comInfo = company.getComInfo();

        log.info("================address[3]은 무엇이냐" + address[address.length - 1]);

        if (address.length == 3) {
            String[] newAddress = Arrays.copyOf(address, address.length + 1);
            newAddress[address.length] = "null";

            return getCompanyDTO(comLoginId, comName, comBusiNum, comPhone, comComdivCode, comRegCode, email, comInfo, newAddress);

        } else {
            return getCompanyDTO(comLoginId, comName, comBusiNum, comPhone, comComdivCode, comRegCode, email, comInfo, address);
        }


    }

    /** 2022.10.25 - 주소 4의 값 없을 때 보여줄 때 에러 발생 -> null일때  "null"을 DB에 넣기로 함.
     * 메서드 추가 */
    private CompanyDTO getCompanyDTO(String comLoginId, String comName, String comBusiNum, String comPhone, Comdiv comComdivCode, Region comRegCode, String[] email, String comInfo, String[] address) {
        CompanyDTO dto = CompanyDTO.builder()
                .loginId(comLoginId)
                .comName(comName)
                .comBusiNum(comBusiNum)
                .comPhone(comPhone)
                .comComdivCode(comComdivCode.getComdivCode())
                .comComdivName(comComdivCode.getComdivName())
                .comRegCode(comRegCode.getRegCode())
                .comRegName(comRegCode.getRegName())
                .email(email[0])
                .comEmail2(email[1])
                .comAddress1(address[0])
                .comAddress2(address[1])
                .comAddress3(address[2])
                .comAddress4(address[3])
                .comInfo(comInfo)
                .build();

        return dto;
    }

    //22.10.18 - ho 기업회원정보 수정하고 DB에 저장.
    public void companyInfoUpdate(CompanyDTO companyDTO){
        Company company = companyDTO.toEntity();
        System.out.println("==============company.getComComdivCode() = " + company.getComComdivCode());
        System.out.println("===========company = " + company.getComName());
        companyRepository.updateInfo(company);
    }
    public List<String> searchRegName(){
        return regionRepository.regName();
    }

    //22.10.25 - ho 기업회원 탈퇴
    public void delete(Long comId) {
        companyRepository.deleteById(comId);
    }

//    public String findId(String comName, String comEmail){
//        Optional<Company> company = companyRepository.checkNameAndEmail(comName, comEmail);
//        return company.get().getComLoginId();
//    }


    //22.11.01 ho - 아이디 찾기용 DTO만들기 메서드.
    public CompanyDTO getCompanyDTOForFindId(String name, String email) {
        return CompanyDTO.builder()
                .comName(name)
                .comEmail1(email)
                .build();
    }

    //22.11.01 ho - 아이디 찾기. String 받아서 DTO 만들어서 찾기.
    public String findId2(String name, String email) {
        CompanyDTO companyDTO = getCompanyDTOForFindId(name, email);
        Company company1 = companyDTO.toEntityForFindId();
        String newName = company1.getComName();
        log.info("??? name 받아오는 값: " + newName);
        String newEmail = company1.getComEmail();
        log.info("??? email 받아오는 값: " + email);

        Long num = companyRepository.countByComNameAndComEmail(newName,newEmail);
        log.info("??? name과 emial로 찾아온 갯수 : "+num);
        if(num == 0){
            return "fail";
        } else {
            Optional<Company> company = companyRepository.findByComNameAndComEmail(newName, newEmail);
            return company.get().getComLoginId();
        }

    }

}