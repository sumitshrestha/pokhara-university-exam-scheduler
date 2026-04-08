package puexamroutine.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import puexamroutine.control.Controller;
import puexamroutine.control.domain.CourseCode;
import puexamroutine.control.domain.Day;
import puexamroutine.control.domain.Group;
import puexamroutine.control.domain.RegularCourses;
import puexamroutine.control.domain.interfaces.CandidateInterface;
import puexamroutine.control.domain.interfaces.DependentCourses;
import puexamroutine.control.domain.routine.ExaminationDayCalander;
import puexamroutine.control.domain.routine.GroupProgramRoutineList;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import puexamroutine.control.domain.routine.ProgramExamDayCourses;
import puexamroutine.control.domain.routine.ProgramRoutine;
import puexamroutine.control.schedule.Result;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "*")
public class ScheduleController {

    private final Controller controller;

    public ScheduleController(Controller controller) {
        this.controller = controller;
    }

    /**
     * POST /api/schedule/auto-hunt
     * No body required.
     * Iterates over all feasible (minGap, maxGap) combinations (minGap 0-4, maxGap 0-8)
     * and returns the schedule with the fewest unscheduled groups, breaking ties by
     * fewest total calendar days (i.e. tightest schedule / least gaps).
     */
    @PostMapping("/auto-hunt")
    public ResponseEntity<?> autoHunt() {
        Result bestResult = null;
        int bestMinGap = -1;
        int bestMaxGap = -1;
        int bestUnscheduled = Integer.MAX_VALUE;
        int bestCalendarDays = Integer.MAX_VALUE;
        int trialsSearched = 0;

        final int MAX_MIN = 4;
        final int MAX_MAX = 8;

        outer:
        for (int minGap = 0; minGap <= MAX_MIN; minGap++) {
            for (int maxGap = minGap; maxGap <= MAX_MAX; maxGap++) {
                controller.setMinimumGap(minGap);
                controller.setMaxGap(maxGap);
                Result result = controller.start(false);
                trialsSearched++;
                if (!result.executedSuccessfully()) continue;

                int unscheduled = result.getunScheduledGroups() == null ? 0 : result.getunScheduledGroups().size();
                int calDays = countAssignedDays(result.getExamCalander());

                boolean betterUnscheduled = unscheduled < bestUnscheduled;
                boolean sameUnscheduledFewerDays = (unscheduled == bestUnscheduled && calDays < bestCalendarDays);

                if (betterUnscheduled || sameUnscheduledFewerDays) {
                    bestResult = result;
                    bestMinGap = minGap;
                    bestMaxGap = maxGap;
                    bestUnscheduled = unscheduled;
                    bestCalendarDays = calDays;
                }

                if (bestUnscheduled == 0) break outer;
            }
        }

        if (bestResult == null) {
            return ResponseEntity.unprocessableEntity().body(Map.of(
                "error", "No feasible schedule found for any gap combination (minGap 0-4, maxGap 0-8)",
                "trialsSearched", trialsSearched
            ));
        }

        List<Map<String, String>> unscheduledGroups = new ArrayList<>();
        if (bestResult.getunScheduledGroups() != null) {
            for (Group group : bestResult.getunScheduledGroups()) {
                unscheduledGroups.add(Map.of(
                    "faculty", group.getFaculty(),
                    "level", group.getLevel(),
                    "discipline", group.getDiscipline()
                ));
            }
        }

        int groupRoutineCount = bestResult.getGroupRoutine() == null ? 0 : bestResult.getGroupRoutine().size();
        int unscheduledGroupCount = unscheduledGroups.size();
        int totalGroupCount = groupRoutineCount + unscheduledGroupCount;

        List<CalendarDayDto> calendarDays = buildCalendar(bestResult.getExamCalander());
        List<GroupRoutineDto> groupRoutines = buildGroupRoutines(bestResult.getGroupRoutine());

        String scheduleStatus = unscheduledGroupCount == 0 ? "COMPLETE" : "PARTIAL";
        String message = unscheduledGroupCount == 0
            ? String.format("Best schedule found: minGap=%d, maxGap=%d (%d trials searched).", bestMinGap, bestMaxGap, trialsSearched)
            : String.format("Best partial schedule: minGap=%d, maxGap=%d. %d group(s) still unscheduled (%d trials searched).", bestMinGap, bestMaxGap, unscheduledGroupCount, trialsSearched);

        return ResponseEntity.ok(Map.ofEntries(
            Map.entry("success", true),
            Map.entry("scheduleStatus", scheduleStatus),
            Map.entry("message", message),
            Map.entry("bestMinGap", bestMinGap),
            Map.entry("bestMaxGap", bestMaxGap),
            Map.entry("trialsSearched", trialsSearched),
            Map.entry("groupRoutineCount", groupRoutineCount),
            Map.entry("unscheduledGroupCount", unscheduledGroupCount),
            Map.entry("totalGroupCount", totalGroupCount),
            Map.entry("unscheduledGroups", unscheduledGroups),
            Map.entry("calendarDays", calendarDays),
            Map.entry("groupRoutines", groupRoutines)
        ));
    }

    private static int countAssignedDays(ExaminationDayCalander calander) {
        if (calander == null) return Integer.MAX_VALUE;
        int count = 0;
        var it = calander.getCalanderIterator();
        while (it.hasNext()) {
            var id = it.next();
            Day day = calander.getExamDay(id);
            if (day != null && day.isAssigned()) count++;
        }
        return count;
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

        int unscheduledGroupCount = unscheduledGroups.size();
        int totalGroupCount = groupRoutineCount + unscheduledGroupCount;

        if (groupRoutineCount == 0 && unscheduledGroupCount > 0) {
            return ResponseEntity.unprocessableEntity().body(Map.of(
                "success", false,
                "scheduleStatus", "NO_FEASIBLE_SCHEDULE",
                "message", "No feasible schedule could be created for any group with the current constraints/data.",
                "groupRoutineCount", groupRoutineCount,
                "unscheduledGroupCount", unscheduledGroupCount,
                "totalGroupCount", totalGroupCount,
                "unscheduledGroups", unscheduledGroups,
                "calendarDays", calendarDays,
                "groupRoutines", groupRoutines
            ));
        }

        if (unscheduledGroupCount > 0) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "scheduleStatus", "PARTIAL",
                "message", "Schedule generated partially. Some groups could not be scheduled.",
                "groupRoutineCount", groupRoutineCount,
                "unscheduledGroupCount", unscheduledGroupCount,
                "totalGroupCount", totalGroupCount,
                "unscheduledGroups", unscheduledGroups,
                "calendarDays", calendarDays,
                "groupRoutines", groupRoutines
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
            "scheduleStatus", "COMPLETE",
            "message", "Schedule generated successfully.",
                "groupRoutineCount", groupRoutineCount,
            "unscheduledGroupCount", unscheduledGroupCount,
            "totalGroupCount", totalGroupCount,
                "unscheduledGroups", unscheduledGroups,
                "calendarDays", calendarDays,
                "groupRoutines", groupRoutines
        ));
    }

    /**
     * GET /api/schedule/diagnose?faculty=X&level=Y&discipline=Z
     * Returns a conflict-graph analysis for the specified group to explain why it cannot be scheduled.
     */
    @GetMapping("/diagnose")
    public ResponseEntity<?> diagnose(
            @RequestParam String faculty,
            @RequestParam String level,
            @RequestParam String discipline) {

        var groupList = controller.getGroupList();
        if (groupList == null) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", "No university data loaded"));
        }

        Group grp;
        try {
            grp = groupList.getGroup(faculty.trim().toUpperCase(), level.trim().toUpperCase(), discipline.trim().toUpperCase());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", "Group not found: " + faculty + "/" + level + "/" + discipline));
        }

        Set<RegularCourses> regularCourseSets = groupList.getRegularCourses(grp);
        if (regularCourseSets == null) {
            regularCourseSets = Collections.emptySet();
        }

        Collection<CandidateInterface> backCandidates = groupList.getBackCandidates(grp);
        if (backCandidates == null) {
            backCandidates = Collections.emptyList();
        }
        var courseList = groupList.getCourseList(grp);

        Collection<CourseCode> regularCourseCodes = courseList != null ? courseList.getRegularCourses() : List.of();
        Collection<CourseCode> backCourseCodes = courseList != null ? courseList.getBackCourses() : List.of();
        if (regularCourseCodes == null) {
            regularCourseCodes = List.of();
        }
        if (backCourseCodes == null) {
            backCourseCodes = List.of();
        }

        // Build the vertex set (all courses appearing in the conflict graph)
        Set<String> allCourseKeys = new LinkedHashSet<>();
        for (CourseCode c : regularCourseCodes) {
            if (c != null && c.getCourseCode() != null) {
                allCourseKeys.add(c.getCourseCode());
            }
        }
        for (CourseCode c : backCourseCodes) {
            if (c != null && c.getCourseCode() != null) {
                allCourseKeys.add(c.getCourseCode());
            }
        }
        for (CandidateInterface cand : backCandidates) {
            if (cand == null || cand.getAllCoursesToAttend() == null) {
                continue;
            }
            for (CourseCode c : cand.getAllCoursesToAttend()) {
                if (c != null && c.getCourseCode() != null) {
                    allCourseKeys.add(c.getCourseCode());
                }
            }
        }

        // Build DependentCourses list exactly as Grouper does
        List<DependentCourses> depList = new ArrayList<>(regularCourseSets);
        depList.addAll(backCandidates);

        // Build adjacency (string-keyed to avoid CourseCode reference equality issues)
        Map<String, Set<String>> adjacency = new HashMap<>();
        for (String k : allCourseKeys) adjacency.put(k, new HashSet<>());

        int totalEdges = 0;
        for (DependentCourses dep : depList) {
            if (dep == null || dep.getDependentCourses() == null) {
                continue;
            }
            List<String> courses = dep.getDependentCourses().stream()
                    .filter(Objects::nonNull)
                    .map(CourseCode::getCourseCode)
                    .filter(Objects::nonNull)
                    .filter(allCourseKeys::contains)
                    .distinct()
                    .collect(Collectors.toList());
            for (int i = 0; i < courses.size(); i++) {
                for (int j = i + 1; j < courses.size(); j++) {
                    String a = courses.get(i), b = courses.get(j);
                    if (!a.equals(b) && !adjacency.get(a).contains(b)) {
                        adjacency.get(a).add(b);
                        adjacency.get(b).add(a);
                        totalEdges++;
                    }
                }
            }
        }

        // Per-course degree
        List<Map<String, Object>> topConflictedCourses = allCourseKeys.stream()
                .map(c -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("course", c);
                    m.put("degree", adjacency.getOrDefault(c, Set.of()).size());
                    return m;
                })
                .sorted(Comparator.comparingInt((Map<String, Object> m) -> (int) m.get("degree")).reversed())
                .limit(30)
                .collect(Collectors.toList());

        // Regular course sets summary
        int maxCoursesInSet = 0;
        List<Map<String, Object>> regSetsSummary = new ArrayList<>();
        for (RegularCourses rs : regularCourseSets) {
                if (rs == null || rs.getCourses() == null) {
                continue;
                }

                int sz = rs.getCourses().size();
            if (sz > maxCoursesInSet) maxCoursesInSet = sz;
            List<String> courses = rs.getCourses().stream()
                    .filter(Objects::nonNull)
                    .map(CourseCode::getCourseCode)
                    .filter(Objects::nonNull)
                    .sorted()
                    .collect(Collectors.toList());

            // Count how many internal edges exist within this set (from back candidates)
            int internalConflicts = 0;
            for (int i = 0; i < courses.size(); i++)
                for (int j = i + 1; j < courses.size(); j++)
                    if (adjacency.getOrDefault(courses.get(i), Set.of()).contains(courses.get(j)))
                        internalConflicts++;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("college", rs.getCollege().getCollegeName());
            m.put("program", rs.getProgram().getProgramName());
            m.put("semester", rs.getSemester().getSemester());
            m.put("courseCount", sz);
            m.put("internalConflicts", internalConflicts);
            m.put("courses", courses);
            regSetsSummary.add(m);
        }
        regSetsSummary.sort(Comparator
                .comparingInt((Map<String, Object> m) -> (int) m.get("internalConflicts")).reversed()
                .thenComparingInt((Map<String, Object> m) -> (int) m.get("courseCount")).reversed());

        // Back candidates causing the most cross-semester edges
        List<Map<String, Object>> topCandidates = new ArrayList<>();
        for (CandidateInterface cand : backCandidates) {
            if (cand == null) {
            continue;
            }

            CourseCode[] candBackPapers = cand.getBackPapers();
            CourseCode[] candRegularPapers = cand.getRegularPapers();

            List<String> back = (candBackPapers == null ? Stream.<CourseCode>empty() : Arrays.stream(candBackPapers))
                .filter(Objects::nonNull)
                .map(CourseCode::getCourseCode)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
            List<String> regular = (candRegularPapers == null ? Stream.<CourseCode>empty() : Arrays.stream(candRegularPapers))
                .filter(Objects::nonNull)
                .map(CourseCode::getCourseCode)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

            int total = cand.getAllCoursesToAttend() == null ? 0 : cand.getAllCoursesToAttend().size();
            if (total < 2) continue;

            // Count how many edges this candidate uniquely forces between different semesters
            // (back papers vs regular papers = cross-semester edge)
            int crossSemesterEdges = 0;
            for (String bp : back)
                for (String rp : regular)
                    if (!bp.equals(rp)) crossSemesterEdges++;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("candidateId", cand.getCandidateID());
            m.put("state", cand.getState());
            m.put("currentSemester", cand.getSemester());
            m.put("backPaperCount", back.size());
            m.put("regularPaperCount", regular.size());
            m.put("totalCourses", total);
            m.put("crossSemesterEdges", crossSemesterEdges);
            m.put("backPapers", back);
            m.put("regularPapers", regular);
            topCandidates.add(m);
        }
        topCandidates.sort(Comparator
                .comparingInt((Map<String, Object> m) -> (int) m.get("crossSemesterEdges")).reversed()
                .thenComparingInt((Map<String, Object> m) -> (int) m.get("totalCourses")).reversed());
        if (topCandidates.size() > 25) topCandidates = topCandidates.subList(0, 25);

        // Minimum viable maxGap estimate (with minGap=0):
        //   feasibility formula: (index_gap)*(minGap+1)-1 < maxGap
        //   best-case index_gap = 1 when semester courses are consecutive → needs maxGap > 0 i.e. ≥ 1
        //   if internalConflicts exist within a set, courses in that set can NEVER all land on the same day,
        //   making it structurally infeasible regardless of gap
        long setsWithInternalConflicts = regSetsSummary.stream()
                .filter(m -> (int) m.get("internalConflicts") > 0).count();
        int estimatedMinMaxGap = maxCoursesInSet > 1 ? 1 : 0;
        String feasibilityNote;
        if (setsWithInternalConflicts > 0) {
            feasibilityNote = setsWithInternalConflicts + " semester set(s) have back-paper candidates who sit two or more courses from that same semester. " +
                    "This creates internal conflicts: those courses MUST be on different days (conflict graph edge) yet the scheduler requires them within a small gap window. " +
                    "This is structurally infeasible regardless of the gap setting. Remove or fix the conflicting back-paper registrations to resolve this.";
        } else if (maxCoursesInSet > 1) {
            feasibilityNote = "No internal conflicts detected. The group likely timed out during graph coloring due to complexity. " +
                    "Try increasing the scheduling timeout or simplifying back-paper registrations.";
        } else {
            feasibilityNote = "All semester sets have a single course. Scheduling should be feasible. Check for data loading issues.";
        }

        return ResponseEntity.ok(Map.ofEntries(
                Map.entry("group", Map.of("faculty", grp.getFaculty(), "level", grp.getLevel(), "discipline", grp.getDiscipline())),
                Map.entry("summary", Map.ofEntries(
                        Map.entry("totalRegularCourses", regularCourseCodes.size()),
                        Map.entry("totalBackCourses", backCourseCodes.size()),
                        Map.entry("totalCoursesInGraph", allCourseKeys.size()),
                        Map.entry("totalRegularCourseSets", regularCourseSets.size()),
                        Map.entry("totalBackCandidates", backCandidates.size()),
                        Map.entry("totalConflictEdges", totalEdges),
                        Map.entry("maxCoursesInAnySemesterSet", maxCoursesInSet),
                        Map.entry("semesterSetsWithInternalConflicts", setsWithInternalConflicts),
                        Map.entry("estimatedMinViableMaxGap", estimatedMinMaxGap),
                        Map.entry("feasibilityNote", feasibilityNote)
                )),
                Map.entry("regularCourseSets", regSetsSummary),
                Map.entry("topConflictedCourses", topConflictedCourses),
                Map.entry("topConflictingCandidates", topCandidates)
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
