package puexamroutine.api.dto;

import java.util.List;

public record BackPaperSubmissionRequest(
        String faculty,
        String level,
        String discipline,
        String program,
        String college,
        List<BackPaperRow> backPapers
) {
    public record BackPaperRow(
            String candidateId,
            String semester,
            String code,
            String name
    ) {
    }
}
