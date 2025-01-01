package clofi.codeython.problem.core.dto.response;

import java.time.LocalDate;

import clofi.codeython.problem.core.domain.Record;

public record RecordResponse(
    Long recordId,
    LocalDate date,
    String title,
    Integer accuracy,
    Integer grade,
    Integer memberCnt
) {
    public static RecordResponse of(Record record, String problemTitle) {
        return new RecordResponse(
            record.getRecordNo(),
            record.getCreatedAt().toLocalDate(),
            problemTitle,
            record.getAccuracy(),
            record.getGrade(),
            record.getMemberCnt()
        );
    }
}
