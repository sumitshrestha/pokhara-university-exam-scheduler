package puexamroutine.api.dto;

import java.util.List;

public record RegistrationSubmissionRequest(
        String faculty,
        String level,
        String discipline,
        String program,
        String college,
        String centre,
        String semester,
        List<CourseRow> courses
) {
    public record CourseRow(
            String code,
            String name,
            Integer totalCandidates
    ) {
    }
}