package clofi.codeython.member.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import clofi.codeython.member.domain.Member;
import clofi.codeython.member.dto.request.CreateMemberRequest;
import clofi.codeython.member.dto.request.UpdateMemberRequest;
import clofi.codeython.member.dto.response.RankingResponse;
import clofi.codeython.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("닉네임, 아이디, 비밀번호를 입력하면 회원가입을 할 수 있다.")
    @Test
    void signUpTest() {
        //given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
            "zeno",
            "zeno1030",
            "wkrwjs5763!"
        );
        //when
        Long memberId = memberService.signUp(createMemberRequest);
        Member member = memberRepository.findByUserNo(memberId)
            .orElseThrow(() -> new EntityNotFoundException("일치하는 사용자가 없습니다."));
        //then

        assertThat(member.getUsername()).isEqualTo("zeno1030");
        assertThat(member.getNickname()).isEqualTo("zeno");
        assertThat(member.getExp()).isEqualTo(0);
    }

    @DisplayName("닉네임이 겹칠 시 예외가 발생한다.")
    @Test
    void signUpExceptionTest() {
        //given
        Member member = new Member(
            "rnfmal",
            "wl3648",
            "zeno"
        );
        memberRepository.save(member);

        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
            "zeno",
            "zeno1030",
            "wkrwjs5763!"
        );
        //when //then
        assertThatThrownBy(() ->
            memberService.signUp(createMemberRequest)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 존재한 닉네임입니다.");
    }

    @DisplayName("아이디가 겹칠 시 예외가 발생한다.")
    @Test
    void signUpIdExceptionTest() {
        //given
        Member member = new Member(
            "zeno1030",
            "wl3648",
            "rnfmal"
        );
        memberRepository.save(member);

        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
            "zeno",
            "zeno1030",
            "wkrwjs5763!"
        );
        //when //then
        assertThatThrownBy(() ->
            memberService.signUp(createMemberRequest)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 존재하는 아이디 입니다.");
    }

    @DisplayName("닉네임을 업데이트할 수 있다.")
    @Test
    void updateTest() {
        //given
        Member member = new Member(
            "zeno1030",
            "wl3648",
            "rnfmal"
        );
        memberRepository.save(member);
        UpdateMemberRequest updateMemberRequest = new UpdateMemberRequest(
            "zeno"
        );
        //when
        memberService.update(member.getUsername(), updateMemberRequest);
        Member updatedMember = memberRepository.findByUserNo(member.getUserNo())
            .orElseThrow(() -> new EntityNotFoundException("일치하는 사용자가 없습니다."));
        //then
        assertThat(updatedMember.getNickname()).isEqualTo("zeno");
    }

    @DisplayName("사용자의 정보를 조회할 수 있다.")
    @Test
    void getMemberTest() {
        //given
        Member member = new Member(
            "zeno1030",
            "wl3648",
            "rnfmal"
        );
        memberRepository.save(member);
        //when
        Member memberInfo = memberRepository.findByUserNo(member.getUserNo())
            .orElseThrow(() -> new EntityNotFoundException("일치하는 사용자가 없습니다."));
        //then
        assertThat(memberInfo.getNickname()).isEqualTo("rnfmal");
        assertThat(memberInfo.getExp()).isEqualTo(0);
    }

    @DisplayName("랭킹을 조회할 수 있다.")
    @Test
    void RankingTest() {
        //given
        Member member1 = new Member(
            "zeno10301",
            "wl3648",
            "rnfmal1",
            1
        );
        memberRepository.save(member1);

        Member member2 = new Member(
            "zeno10302",
            "wl3648",
            "rnfmal2",
            2
        );
        memberRepository.save(member2);

        Member member3 = new Member(
            "zeno10303",
            "wl3648",
            "rnfmal3",
            3
        );
        memberRepository.save(member3);

        Member member4 = new Member(
            "zeno10304",
            "wl3648",
            "rnfmal4",
            4
        );
        memberRepository.save(member4);

        Member member5 = new Member(
            "zeno10305",
            "wl3648",
            "rnfmal5",
            5
        );
        memberRepository.save(member5);
        //when
        RankingResponse rankingResponse = memberService.ranking("zeno10305");
        //then
        assertThat(rankingResponse.myRank()).isEqualTo(1);
        assertThat(rankingResponse.ranker().getFirst().nickname()).isEqualTo("rnfmal5");
    }
}