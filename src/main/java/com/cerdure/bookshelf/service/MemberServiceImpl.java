package com.cerdure.bookshelf.service;

import com.cerdure.bookshelf.domain.member.Address;
import com.cerdure.bookshelf.domain.member.Member;
import com.cerdure.bookshelf.domain.member.MemberCoupon;
import com.cerdure.bookshelf.domain.member.MemberProfile;
import com.cerdure.bookshelf.dto.loginApi.ApiJoinDto;
import com.cerdure.bookshelf.dto.loginApi.MemberApiLoginInfoDto;
import com.cerdure.bookshelf.dto.member.InfoUpdateDto;
import com.cerdure.bookshelf.dto.member.MemberDto;
import com.cerdure.bookshelf.repository.MemberCouponRepository;
import com.cerdure.bookshelf.repository.MemberProfileRepository;
import com.cerdure.bookshelf.repository.MemberRepository;
import com.cerdure.bookshelf.service.interfaces.MemberInfoUpdateService;
import com.cerdure.bookshelf.service.interfaces.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberCouponRepository memberCouponRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final MemberInfoUpdateService memberInfoUpdate;

    @Override
    @Transactional
    public void save(Member member) {
        memberRepository.save(member);
    }

    @Transactional
    public Long join(MemberDto memberDto) {
        validateDuplicateMember(memberDto);
        MemberProfile basicProfile = getBasicProfile();
        Member member = memberDto.createMember(passwordEncoder,basicProfile);
        memberRepository.save(member);
        return member.getId();
    }
    
    public MemberProfile getBasicProfile() {
        String fullPath = System.getProperty("user.dir") + "/src/main/resources/static/upload-img/"+"basic.jpeg";
        MemberProfile basicProfile = MemberProfile.builder()
                .profileFullPath(fullPath)
                .storeProfileName("basic.jpeg")
                .originalProfileName("basic.jpeg")
                .build();
        basicProfile.prePersist();
        MemberProfile savedProfile = memberProfileRepository.save(basicProfile);
        return savedProfile;
    }

    public void validateDuplicateMember(MemberDto memberDto) {
        Optional<Member> findMembers = memberRepository.findByPhone(memberDto.getPhone());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    @Override
    @Transactional
    public MemberApiLoginInfoDto apiJoin(ApiJoinDto apiJoinDto) {
        Member member = memberRepository.findByEmail(apiJoinDto.getEmail());
        Optional<Member> memberCheck = memberRepository.findByPhone(apiJoinDto.getPhoneNumber());

        if(!memberCheck.isEmpty()){
            log.info("이미 가입한 회원입니다");
            memberRepository.deleteById(member.getId());
            return null;
        }

        Address address = Address.builder()
                .street(apiJoinDto.getStreet())
                .city(apiJoinDto.getCity())
                .zipcode(apiJoinDto.getZipcode())
                .build();
        String passWordEmail = member.getEmail();
        String encode = passwordEncoder.encode(passWordEmail);
        Member joinedMember = member.apiJoin(apiJoinDto.getPhoneNumber(), address, encode,apiJoinDto.getMemberName());
        Member savedMember = memberRepository.save(joinedMember);

        return MemberApiLoginInfoDto.builder()
                .address(savedMember.getAddress())
                .password(passWordEmail)
                .phone(savedMember.getPhone())
                .build();
    }

    @Override
    public Member findMember(Authentication authentication) {
        return memberRepository.findByPhone(authentication.getName()).get();
    }

    @Override
    public InfoUpdateDto showInfo(Authentication authentication) {
        Member member = this.findMember(authentication);
        String profile = member.getMemberProfile().getProfileDir() + member.getMemberProfile().getStoreProfileName();
        return InfoUpdateDto.builder()
                .city(member.getAddress().getCity())
                .postNum(member.getAddress().getZipcode())
                .street(member.getAddress().getStreet())
                .nickName(member.getNickname())
                .phone(member.getPhone())
                .memberprofile(profile)
                .memberJoinType(member.getMemberJoinType())
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId).get();
    }

    @Override
    public Member findByPhone(String phone) {
        return memberRepository.findByPhone(phone).get();
    }

    @Transactional
    public void update(Long id, MemberDto memberDto) {
//        Member member = memberRepository.findOne(id);
//        member.setName(name);
    }

    @Transactional
    public void delete(Long id) {
    }

    @Override
    public void useCoupon(Long couponId) {
        MemberCoupon memberCoupon = memberCouponRepository.findById(couponId).get();
        memberCoupon.changeUseDate(LocalDateTime.now());
        memberCouponRepository.save(memberCoupon);
    }

    @Override
    @Transactional
    public void changePoint(Authentication authentication, int minusPoint) {
        Member member = findMember(authentication);
        member.changePoint(member.getPoint() - minusPoint);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void changePoint(Member member, int minusPoint) {
        member.changePoint(member.getPoint() - minusPoint);
        memberRepository.save(member);
    }
}
