package clofi.codeython.member.repository;

import clofi.codeython.member.domain.Member;
import clofi.codeython.member.repository.jpa.MemberJpaRepository;
import clofi.codeython.member.repository.role.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member findByUserNo(Long userNo) {
        return memberJpaRepository.findByUserNo(userNo).orElseThrow(() -> new EntityNotFoundException("일치하는 사용자가 없습니다."));
    }

    @Override
    public Member findByUsername(String userName) {
        return memberJpaRepository.findByUsername(userName);
    }

    @Override
    public void existsByNickname(String nickName) {
        if (memberJpaRepository.existsByNickname(nickName)) {
            throw new IllegalArgumentException("이미 존재한 닉네임입니다.");
        }
    }

    @Override
    public void existsByUsername(String userName) {
        if (memberJpaRepository.existsByUsername(userName)) {
            throw new IllegalArgumentException("이미 존재하는 아이디 입니다.");
        }
    }

    @Override
    public Member findByNickname(String nickName) {
        return null;
    }

    @Override
    public List<Member> findTop5ByOrderByExpDesc() {
        return List.of();
    }

    @Override
    public List<Member> findAllByOrderByExpDesc() {
        return List.of();
    }

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public void deleteAllInBatch() {
        memberJpaRepository.deleteAllInBatch();
    }
}
