package puexamroutine.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import puexamroutine.control.Controller;
import puexamroutine.control.domain.CourseCode;
import puexamroutine.control.domain.Day;
import puexamroutine.control.domain.Group;
import puexamroutine.control.domain.routine.ExaminationDayCalander;
import puexamroutine.control.domain.routine.GroupProgramRoutineList;
import puexamroutine.control.domain.routine.ProgramExamDayCourses;
import puexamroutine.control.domain.routine.ProgramRoutine;
import puexamroutine.control.schedule.Result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "*")
public class ScheduleController {

    private final Controller controller;

    public ScheduleController(Controller controller) {
        this.controller = controller;
    }

    /**
     * POST /api/schedule/generate
     * Body: { "minGap": 1, "maxGap": 3 }
     * Runs the graph coloring scheduler and returns the full result.
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody Map<String, Integer> body) {
        int minGap = body.getOrDefault("minGap", 1);
        int maxGap = body.getOrDefault("maxGap", 3);

        controller.setMinimumGap(minGap);
        controller.setMaxGap(maxGap);

        Result result = controller.start(false);

        if (!result.executedSuccessfully()) {
            String errorMessage = result.getError();
            if (errorMessage == null || errorMessage.isBlank()) {
                errorMessage = "Scheduling failed";
            }
            return ResponseEntity.unprocessableEntity()
                    .body(Map.of("error", errorMessage));
        }

        List<Map<String, String>> unscheduledGroups = new ArrayList<>();
        if (result.getunScheduledGroups() != null) {
            for (Group group : result.getunScheduledGroups()) {
                unscheduledGroups.add(Map.of(
                        "faculty", group.getFaculty(),
                        "level", group.getLevel(),
                        "discipline", group.getDiscipline()
                ));
            }
        }

        int groupRoutineCount = result.getGroupRoutine() == null ? 0 : result.getGroupRoutine().size();

        List<CalendarDayDto> calendarDays = buildCalendar(result.getExamCalander());
        List<GroupRoutineDto> groupRoutines = buildGroupRoutines(result.getGroupRoutine());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Schedule generated successfully.",
                "groupRoutineCount", groupRoutineCount,
                "unscheduledGroupCount", unscheduledGroups.size(),
                "unscheduledGroups", unscheduledGroups,
                "calendarDays", calendarDays,
                "groupRoutines", groupRoutines
        ));
    }

    private static List<CalendarDayDto> buildCalendar(ExaminationDayCalander calander) {
        List<CalendarDayDto> days = new ArrayList<>();
        if (calander == null) {
            return days;
        }

        var iterator = calander.getCalanderIterator();
        while (iterator.hasNext()) {
            var examDayIdentifier = iterator.next();
            Day day = calander.getExamDay(examDayIdentifier);
            if (day == null || !day.isAssigned()) {
                continue;
            }

            List<String> courses = new ArrayList<>();
            for (CourseCode course : day.getExamCourses()) {
                courses.add(course.toString());
            }
            courses.sort(String.CASE_INSENSITIVE_ORDER);

            days.add(new CalendarDayDto(
                    day.getExaminationDate(),
                    day.getShift(),
                    courses
            ));
        }

        days.sort(Comparator
                .comparingInt(CalendarDayDto::dateIndex)
                .thenComparing(CalendarDayDto::shift, String.CASE_INSENSITIVE_ORDER));

        return days;
    }

    private static List<GroupRoutineDto> buildGroupRoutines(java.util.Collection<GroupProgramRoutineList> groupRoutineLists) {
        List<GroupRoutineDto> groupRoutines = new ArrayList<>();
        if (groupRoutineLists == null) {
            return groupRoutines;
        }

        for (GroupProgramRoutineList groupRoutine : groupRoutineLists) {
            Group group = groupRoutine.getGroup();
            List<ProgramRoutineDto> programRoutines = new ArrayList<>();

            for (ProgramRoutine programRoutine : groupRoutine.getAllProgramRoutine()) {
                List<ProgramExamSlotDto> slots = new ArrayList<>();
                for (Day examDay : programRoutine.getExamList()) {
                    ProgramExamDayCourses programExamDayCourses = programRoutine.getExams(examDay);
                    if (programExamDayCourses == null) {
                        continue;
                    }

                    List<String> courses = new ArrayList<>(List.of(programExamDayCourses.getCourseAsString()));
                    courses.sort(String.CASE_INSENSITIVE_ORDER);
                    slots.add(new ProgramExamSlotDto(
                            examDay.getExaminationDate(),
                            examDay.getShift(),
                            courses
                    ));
                }

                slots.sort(Comparator
                        .comparingInt(ProgramExamSlotDto::dateIndex)
                        .thenComparing(ProgramExamSlotDto::shift, String.CASE_INSENSITIVE_ORDER));

                programRoutines.add(new ProgramRoutineDto(
                        programRoutine.getProgram().getProgramName(),
                        slots
                ));
            }

            programRoutines.sort(Comparator.comparing(ProgramRoutineDto::program, String.CASE_INSENSITIVE_ORDER));

            groupRoutines.add(new GroupRoutineDto(
                    group.getFaculty(),
                    group.getLevel(),
                    group.getDiscipline(),
                    programRoutines
            ));
        }

        groupRoutines.sort(Comparator
                .comparing(GroupRoutineDto::faculty, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(GroupRoutineDto::level, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(GroupRoutineDto::discipline, String.CASE_INSENSITIVE_ORDER));

        return groupRoutines;
    }

    private record CalendarDayDto(int dateIndex, String shift, List<String> courses) {}

    private record ProgramExamSlotDto(int dateIndex, String shift, List<String> courses) {}

    private record ProgramRoutineDto(String program, List<ProgramExamSlotDto> slots) {}

    private record GroupRoutineDto(String faculty, String level, String discipline, List<ProgramRoutineDto> programs) {}
}
