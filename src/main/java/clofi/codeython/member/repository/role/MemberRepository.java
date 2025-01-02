package clofi.codeython.member.repository.role;

import clofi.codeython.member.domain.Member;

import java.util.List;

public interface MemberRepository{

    Member findByUserNo(Long userNo);

    Member findByUsername(String userName);

    void existsByNickname(String nickName);

    void existsByUsername(String userName);

    Member findByNickname(String nickName);

    List<Member> findTop5ByOrderByExpDesc();

    List<Member> findAllByOrderByExpDesc();

    Member save(Member member);

    List<Member> findAll();
}
