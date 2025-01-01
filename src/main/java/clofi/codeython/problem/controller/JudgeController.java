package clofi.codeython.problem.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import clofi.codeython.problem.judge.dto.request.ExecutionRequest;
import clofi.codeython.problem.judge.dto.request.SubmitRequest;
import clofi.codeython.problem.judge.dto.response.ExecutionResponse;
import clofi.codeython.problem.judge.dto.response.SubmitResponse;
import clofi.codeython.problem.judge.service.JudgeService;
import clofi.codeython.security.CustomMemberDetails;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class JudgeController {
    private final JudgeService judgeService;

    @PostMapping("/api/problems/{problemId}/result")
    public SubmitResponse submit(@RequestBody SubmitRequest submitRequest,
        @PathVariable("problemId") long problemNo,
        @AuthenticationPrincipal CustomMemberDetails userDetails) {
        return judgeService.submit(submitRequest, problemNo, userDetails.getMember());
    }

    @PostMapping("/api/problems/{problemId}/execution")
    public List<ExecutionResponse> execution(@RequestBody ExecutionRequest executionRequest,
        @PathVariable("problemId") long problemNo) {
        return judgeService.execution(executionRequest, problemNo);
    }
}
