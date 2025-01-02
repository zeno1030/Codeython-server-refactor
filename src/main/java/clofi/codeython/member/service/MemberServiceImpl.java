package clofi.codeython.member.service;

import java.util.ArrayList;
import java.util.List;

import clofi.codeython.member.service.role.MemberService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.codeython.member.domain.Member;
import clofi.codeython.member.dto.request.CreateMemberRequest;
import clofi.codeython.member.dto.request.UpdateMemberRequest;
import clofi.codeython.member.dto.response.MemberResponse;
import clofi.codeython.member.dto.response.RankerResponse;
import clofi.codeython.member.dto.response.RankingResponse;
import clofi.codeython.member.repository.role.MemberRepository;
import clofi.codeython.security.CustomMemberDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements UserDetailsService, MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Long signUp(CreateMemberRequest createMemberRequest) {
        memberRepository.existsByNickname(createMemberRequest.getNickname());
        memberRepository.existsByUsername(createMemberRequest.getUsername());
        return memberRepository.save(createMemberRequest.toMember(bCryptPasswordEncoder)).getUserNo();
    }

    @Override
    public Long update(String userName, UpdateMemberRequest updateMemberRequest) {
        Member memberId = memberRepository.findByUsername(userName);
        Member member = memberRepository.findByUserNo(memberId.getUserNo());
        memberRepository.existsByNickname(updateMemberRequest.getNickname());
        member.updateNickName(updateMemberRequest.getNickname());
        return member.getUserNo();
    }

    @Override
    public MemberResponse getMember(String userName) {
        Member member = memberRepository.findByUsername(userName);
        Integer exp = member.getExp();
        int level = calculateUserLevel(exp);
        int remainExp = calculateRemainExp(exp);
        return MemberResponse.of(member, level, remainExp);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member memberData = memberRepository.findByUsername(username);
        return memberData == null ? null : new CustomMemberDetails(memberData);
    }

    @Override
    public RankingResponse ranking(String userName) {
        Member member = memberRepository.findByUsername(userName);
        List<Member> top5Members = memberRepository.findTop5ByOrderByExpDesc();
        List<RankerResponse> rankerResponses = new ArrayList<>();

        int userRank = -1;
        for (int i = 0; i < top5Members.size(); i++) {
            Member currentMember = top5Members.get(i);
            rankerResponses.add(new RankerResponse(
                currentMember.getNickname(), i + 1, calculateUserLevel(currentMember.getExp())));

            if (currentMember.getUserNo().equals(member.getUserNo())) {
                userRank = i + 1;
            }
        }

        if (userRank == -1) {
            userRank = memberRepository.findAllByOrderByExpDesc().indexOf(member) + 1;
        }

        return RankingResponse.of(rankerResponses, userRank);
    }

    private int calculateUserLevel(int exp) {
        return exp / 100 + 1;
    }

    private int calculateRemainExp(int exp) {
        return exp % 100;
    }

}
