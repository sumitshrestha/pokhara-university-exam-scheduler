package puexamroutine.api.dto;

import java.util.List;

public record RegistrationMetadataResponse(
        List<GroupOption> groups,
        List<CentreOption> centres
) {
    public record GroupOption(
            String faculty,
            String level,
            String discipline,
            List<ProgramOption> programs,
            List<CourseOption> courses
    ) {
    }

    public record ProgramOption(
            String name,
            List<String> colleges
    ) {
    }

    public record CourseOption(
            String code,
            String name
    ) {
    }

    public record CentreOption(
            String name,
            Integer maxLimit
    ) {
    }
}