package clofi.codeython.problem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import clofi.codeython.problem.core.dto.request.CreateProblemRequest;
import clofi.codeython.problem.core.dto.response.AllProblemResponse;
import clofi.codeython.problem.core.dto.response.GetProblemResponse;
import clofi.codeython.problem.core.dto.response.RecordResponse;
import clofi.codeython.problem.core.service.ProblemService;
import clofi.codeython.security.CustomMemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping("/api/problems")
    public ResponseEntity<Long> createProblem(
        @RequestBody @Valid CreateProblemRequest createProblemRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(problemService.createProblem(createProblemRequest));
    }

    @GetMapping("/api/problems")
    public ResponseEntity<List<AllProblemResponse>> getAllProblem(
        @AuthenticationPrincipal CustomMemberDetails userDetails) {

        return ResponseEntity.ok(problemService.getAllProblem(userDetails.getMember()));
    }

    @GetMapping("/api/problems/{problemId}")
    public ResponseEntity<GetProblemResponse> getProblem(@PathVariable("problemId") Long problemNo,
        @AuthenticationPrincipal CustomMemberDetails userDetails) {
        return ResponseEntity.ok(problemService.getProblem(problemNo, userDetails.getMember()));
    }

    @GetMapping("/api/recent-records")
    public ResponseEntity<List<RecordResponse>> getRecord(
        @AuthenticationPrincipal CustomMemberDetails userDetails) {
        String username = userDetails.getUsername();

        return ResponseEntity.ok(problemService.getRecord(username));
    }

}
