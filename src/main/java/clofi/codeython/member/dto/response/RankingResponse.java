package clofi.codeython.member.dto.response;

import java.util.List;

public record RankingResponse(
    List<RankerResponse> ranker,
    int myRank
) {
    public static RankingResponse of(
        List<RankerResponse> ranker, int myRank) {
        return new RankingResponse(
            ranker,
            myRank
        );
    }
}
