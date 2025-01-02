package clofi.codeython.member.service.role;

import clofi.codeython.member.dto.request.CreateMemberRequest;
import clofi.codeython.member.dto.request.UpdateMemberRequest;
import clofi.codeython.member.dto.response.MemberResponse;
import clofi.codeython.member.dto.response.RankingResponse;

public interface MemberService {
    Long signUp(CreateMemberRequest createMemberRequest);
    Long update(String userName, UpdateMemberRequest updateMemberRequest);
    MemberResponse getMember(String userName);
    RankingResponse ranking(String userName);
}
