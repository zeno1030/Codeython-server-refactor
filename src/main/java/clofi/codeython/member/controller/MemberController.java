package clofi.codeython.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import clofi.codeython.member.dto.request.CreateMemberRequest;
import clofi.codeython.member.dto.request.UpdateMemberRequest;
import clofi.codeython.member.dto.response.MemberResponse;
import clofi.codeython.member.dto.response.RankingResponse;
import clofi.codeython.member.service.MemberService;
import clofi.codeython.security.CustomMemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/api/signup")
    public ResponseEntity<Long> signUp(@Valid @RequestBody CreateMemberRequest createMemberRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(memberService.signUp(createMemberRequest));
    }

    @GetMapping("/api/users")
    public ResponseEntity<MemberResponse> getMember(@AuthenticationPrincipal CustomMemberDetails userDetails) {
        String username = userDetails.getUsername();

        return ResponseEntity.ok(memberService.getMember(username));
    }

    @PatchMapping("/api/users")
    public ResponseEntity<Long> update(
        @AuthenticationPrincipal CustomMemberDetails userDetails,
        @Valid @RequestBody UpdateMemberRequest updateMemberRequest) {
        String username = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).body(memberService.update(username, updateMemberRequest));
    }

    @GetMapping("/api/ranking")
    public ResponseEntity<RankingResponse> ranking(
        @AuthenticationPrincipal CustomMemberDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(memberService.ranking(username));
    }

}
