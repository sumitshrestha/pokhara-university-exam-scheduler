package puexamroutine.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import puexamroutine.api.dto.BackPaperSubmissionRequest;
import puexamroutine.api.dto.RegistrationMetadataResponse;
import puexamroutine.api.dto.RegistrationSubmissionRequest;
import puexamroutine.control.Controller;
import puexamroutine.control.database.DatabaseWriter;
import puexamroutine.control.domain.College;
import puexamroutine.control.domain.CourseCode;
import puexamroutine.control.domain.Group;
import puexamroutine.control.domain.Program;
import puexamroutine.control.domain.list.CentreTable;
import puexamroutine.control.domain.list.GroupList;
import puexamroutine.control.domain.list.ProgramList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataController {

    private final Controller controller;

    public DataController(Controller controller) {
        this.controller = controller;
    }

    @GetMapping("/registration-metadata")
    public ResponseEntity<?> getRegistrationMetadata() {
        GroupList groupList = controller.getGroupList();
        CentreTable centreTable = controller.getCentres();
        DatabaseWriter writer = controller.getDatabaseManager();

        if (groupList == null || centreTable == null || writer == null) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Registration metadata is unavailable. Reload the backend data first."));
        }

        Map<String, String> courseNames = writer.readCourses();
        if (courseNames == null) {
            courseNames = new HashMap<>();
        }
        List<RegistrationMetadataResponse.GroupOption> groups = new ArrayList<>();

        for (Group group : groupList.getGroups()) {
            ProgramList programList = groupList.getPrograms(group);
            List<RegistrationMetadataResponse.ProgramOption> programs = new ArrayList<>();
            if (programList != null) {
                for (Program program : programList.getPrograms()) {
                    List<String> colleges = program.getSupportingColleges().stream()
                            .map(College::getCollegeName)
                            .filter(Objects::nonNull)
                            .sorted(String::compareToIgnoreCase)
                            .toList();
                    programs.add(new RegistrationMetadataResponse.ProgramOption(program.getProgramName(), colleges));
                }
            }

            List<RegistrationMetadataResponse.CourseOption> courses = new ArrayList<>();
            Map<CourseCode, String> groupCourses = groupList.getCoursesMap(group);
            if (groupCourses != null) {
                for (Map.Entry<CourseCode, String> entry : groupCourses.entrySet()) {
                    String code = entry.getKey().toString();
                    String name = courseNames.getOrDefault(code, entry.getValue());
                    courses.add(new RegistrationMetadataResponse.CourseOption(code, name));
                }
            }

            programs.sort(Comparator.comparing(RegistrationMetadataResponse.ProgramOption::name, String.CASE_INSENSITIVE_ORDER));
            courses.sort(Comparator.comparing(RegistrationMetadataResponse.CourseOption::code, String.CASE_INSENSITIVE_ORDER));
            groups.add(new RegistrationMetadataResponse.GroupOption(
                    group.getFaculty(),
                    group.getLevel(),
                    group.getDiscipline(),
                    programs,
                    courses
            ));
        }

        groups.sort(Comparator
                .comparing(RegistrationMetadataResponse.GroupOption::faculty, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(RegistrationMetadataResponse.GroupOption::level, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(RegistrationMetadataResponse.GroupOption::discipline, String.CASE_INSENSITIVE_ORDER));

        List<RegistrationMetadataResponse.CentreOption> centres = centreTable.getCentres().stream()
                .map(centre -> new RegistrationMetadataResponse.CentreOption(
                        centre.getCentreName(),
                        centre.getMaximumCentreLimit()
                ))
                .sorted(Comparator.comparing(RegistrationMetadataResponse.CentreOption::name, String.CASE_INSENSITIVE_ORDER))
                .toList();

        return ResponseEntity.ok(new RegistrationMetadataResponse(groups, centres));
    }

    /** POST /api/data/programs - add a full program with colleges to the database. */
    @PostMapping("/programs")
    public ResponseEntity<?> addProgram(@RequestBody Program program) {
        boolean success = controller.addProgram(program);
        if (!success) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to add program: " + program.getProgramName()));
        }
        return ResponseEntity.ok(Map.of("message", "Program added successfully"));
    }

    @PostMapping("/registrations")
    public ResponseEntity<?> saveRegistration(@RequestBody RegistrationSubmissionRequest request) {
        String faculty = normalize(request.faculty());
        String level = normalize(request.level());
        String discipline = normalize(request.discipline());
        String program = normalize(request.program());
        String college = normalize(request.college());
        String centre = normalize(request.centre());
        String semester = normalize(request.semester());

        if (isBlank(faculty) || isBlank(level) || isBlank(discipline) || isBlank(program)
                || isBlank(college) || isBlank(centre) || isBlank(semester)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Faculty, level, discipline, program, college, centre, and semester are required."));
        }

        if (request.courses() == null || request.courses().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Add at least one course registration row before saving."));
        }

        DatabaseWriter writer = controller.getDatabaseManager();
        GroupList groupList = controller.getGroupList();
        CentreTable centreTable = controller.getCentres();
        if (writer == null || groupList == null || centreTable == null) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Backend data is not initialized."));
        }

        if (centreTable.getCentre(centre) == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Select an existing exam centre for this registration."));
        }

        HashMap<String, String> existingCourses = writer.readCourses();
        if (existingCourses == null) {
            existingCourses = new HashMap<>();
        }
        try {
            if (!writer.removeAutoCommit()) {
                return ResponseEntity.internalServerError().body(Map.of("error", "Could not start a database transaction."));
            }

            if (!groupExists(groupList, faculty, level, discipline)
                    && !writer.addGroup(faculty, level, discipline)) {
                rollback(writer);
                return ResponseEntity.internalServerError().body(Map.of("error", "Failed to create the selected exam group."));
            }

            for (RegistrationSubmissionRequest.CourseRow courseRow : request.courses()) {
                String courseCode = normalize(courseRow.code());
                String courseName = normalize(courseRow.name());
                Integer totalCandidates = courseRow.totalCandidates();

                if (isBlank(courseCode) || isBlank(courseName) || totalCandidates == null || totalCandidates < 0) {
                    rollback(writer);
                    return ResponseEntity.badRequest().body(Map.of("error", "Each course row needs a code, name, and a non-negative candidate count."));
                }

                if (!existingCourses.containsKey(courseCode)) {
                    if (!writer.addCourse(courseCode, courseName, faculty, level, discipline)) {
                        rollback(writer);
                        return ResponseEntity.internalServerError().body(Map.of("error", "Failed to add course " + courseCode + "."));
                    }
                    existingCourses.put(courseCode, courseName);
                } else if (!courseName.equalsIgnoreCase(existingCourses.get(courseCode))) {
                    if (!writer.updateCourseName(courseCode, courseName)) {
                        rollback(writer);
                        return ResponseEntity.internalServerError().body(Map.of("error", "Failed to update the course name for " + courseCode + "."));
                    }
                    existingCourses.put(courseCode, courseName);
                }
            }

            if (!programExists(groupList, faculty, level, discipline, program)
                    && !writer.addProgram(program, faculty, level, discipline)) {
                rollback(writer);
                return ResponseEntity.internalServerError().body(Map.of("error", "Failed to create the selected program."));
            }

            String currentCentre = writer.readCentre(program, college);
            if (currentCentre == null || currentCentre.isBlank()) {
                if (!writer.addCollegeCentre(college, centre, program)) {
                    rollback(writer);
                    return ResponseEntity.internalServerError().body(Map.of("error", "Failed to assign the college to the selected centre."));
                }
            } else if (!centre.equalsIgnoreCase(currentCentre)) {
                rollback(writer);
                return ResponseEntity.badRequest().body(Map.of(
                        "error",
                        "This college/program is already assigned to centre " + currentCentre + ". Update that mapping before changing it here."
                ));
            }

            if (!writer.removeRegularSemester(college, program, semester)) {
                rollback(writer);
                return ResponseEntity.internalServerError().body(Map.of("error", "Failed to replace the existing semester registration."));
            }

            for (RegistrationSubmissionRequest.CourseRow courseRow : request.courses()) {
                String courseCode = normalize(courseRow.code());
                if (!writer.addRegularCourse(courseCode, college, program, semester, courseRow.totalCandidates())) {
                    rollback(writer);
                    return ResponseEntity.internalServerError().body(Map.of("error", "Failed to save registration for course " + courseCode + "."));
                }
            }

            if (!writer.commit()) {
                rollback(writer);
                return ResponseEntity.internalServerError().body(Map.of("error", "Could not commit the registration transaction."));
            }

            if (!writer.setAutoCommit()) {
                return ResponseEntity.internalServerError().body(Map.of("error", "Registration saved, but the database connection could not return to auto-commit mode."));
            }

            if (!controller.reRead()) {
                return ResponseEntity.internalServerError().body(Map.of("error", "Registration saved, but the in-memory data cache could not be refreshed."));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Course registration saved successfully.",
                    "program", program,
                    "college", college,
                    "semester", semester,
                    "courseCount", request.courses().size()
            ));
        } catch (Exception exception) {
            rollback(writer);
            return ResponseEntity.internalServerError().body(Map.of("error", "Registration save failed: " + exception.getMessage()));
        }
    }

    @GetMapping("/backpapers")
    public ResponseEntity<?> getBackPapers(
            @RequestParam String program,
            @RequestParam String college
    ) {
        String normalizedProgram = normalize(program);
        String normalizedCollege = normalize(college);

        if (isBlank(normalizedProgram) || isBlank(normalizedCollege)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Program and college are required to load back-paper candidates."));
        }

        DatabaseWriter writer = controller.getDatabaseManager();
        if (writer == null) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Backend data is not initialized."));
        }

        HashMap<String, Integer> candidates = writer.getBackPaperCandidates(normalizedCollege, normalizedProgram);
        if (candidates == null) {
            candidates = new HashMap<>();
        }

        HashMap<String, String> knownCourses = writer.readCourses();
        if (knownCourses == null) {
            knownCourses = new HashMap<>();
        }

        List<Map<String, String>> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> candidate : candidates.entrySet()) {
            String candidateId = normalize(candidate.getKey());
            String[] backCourses = writer.getCandidateBackPapers(candidateId, normalizedCollege, normalizedProgram);
            if (backCourses == null) {
                continue;
            }

            Arrays.sort(backCourses, String.CASE_INSENSITIVE_ORDER);
            for (String courseCode : backCourses) {
                String normalizedCode = normalize(courseCode);
                rows.add(Map.of(
                        "candidateId", candidateId,
                        "semester", String.valueOf(candidate.getValue()),
                        "code", normalizedCode,
                        "name", knownCourses.getOrDefault(normalizedCode, "")
                ));
            }
        }

        rows.sort(Comparator
                .comparing((Map<String, String> row) -> row.getOrDefault("candidateId", ""), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(row -> row.getOrDefault("semester", ""), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(row -> row.getOrDefault("code", ""), String.CASE_INSENSITIVE_ORDER));

        return ResponseEntity.ok(Map.of(
                "program", normalizedProgram,
                "college", normalizedCollege,
                "backPapers", rows,
                "count", rows.size()
        ));
    }

    @PostMapping("/backpapers")
    public ResponseEntity<?> saveBackPapers(@RequestBody BackPaperSubmissionRequest request) {
        String faculty = normalize(request.faculty());
        String level = normalize(request.level());
        String discipline = normalize(request.discipline());
        String program = normalize(request.program());
        String college = normalize(request.college());

        if (isBlank(faculty) || isBlank(level) || isBlank(discipline) || isBlank(program) || isBlank(college)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Faculty, level, discipline, program, and college are required."));
        }

        DatabaseWriter writer = controller.getDatabaseManager();
        GroupList groupList = controller.getGroupList();
        if (writer == null || groupList == null) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Backend data is not initialized."));
        }

        if (!programExists(groupList, faculty, level, discipline, program)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Selected program does not exist in the selected exam group."));
        }

        HashMap<String, String> existingCourses = writer.readCourses();
        if (existingCourses == null) {
            existingCourses = new HashMap<>();
        }

        List<BackPaperSubmissionRequest.BackPaperRow> rows = request.backPapers() == null
                ? List.of()
                : request.backPapers();

        try {
            if (!writer.removeAutoCommit()) {
                return ResponseEntity.internalServerError().body(Map.of("error", "Could not start a database transaction."));
            }

            if (!writer.removeBackCandidatesForProgramCollege(college, program)) {
                rollback(writer);
                return ResponseEntity.internalServerError().body(Map.of("error", "Failed to replace back-paper candidates for the selected program and college."));
            }

            for (BackPaperSubmissionRequest.BackPaperRow row : rows) {
                String candidateId = normalize(row.candidateId());
                String semesterText = normalize(row.semester());
                String courseCode = normalize(row.code());
                String courseName = row.name() == null ? "" : row.name().trim();

                Integer semester = parseSemesterValue(semesterText);
                if (isBlank(candidateId) || isBlank(courseCode) || semester == null || semester <= 0) {
                    rollback(writer);
                    return ResponseEntity.badRequest().body(Map.of("error", "Each back-paper row needs candidate ID, subject code, and a valid semester (> 0)."));
                }

                if (!existingCourses.containsKey(courseCode)) {
                    if (isBlank(courseName)) {
                        rollback(writer);
                        return ResponseEntity.badRequest().body(Map.of("error", "Provide a subject name for new course code " + courseCode + "."));
                    }

                    if (!writer.addCourse(courseCode, courseName, faculty, level, discipline)) {
                        rollback(writer);
                        return ResponseEntity.internalServerError().body(Map.of("error", "Failed to add course " + courseCode + " for back-paper registration."));
                    }
                    existingCourses.put(courseCode, courseName);
                } else if (!isBlank(courseName) && !courseName.equalsIgnoreCase(existingCourses.get(courseCode))) {
                    if (!writer.updateCourseName(courseCode, courseName)) {
                        rollback(writer);
                        return ResponseEntity.internalServerError().body(Map.of("error", "Failed to update the subject name for " + courseCode + "."));
                    }
                    existingCourses.put(courseCode, courseName);
                }

                if (!writer.addBackCandidate(candidateId, courseCode, program, college, semester)) {
                    rollback(writer);
                    return ResponseEntity.internalServerError().body(Map.of("error", "Failed to save back-paper subject " + courseCode + " for candidate " + candidateId + "."));
                }
            }

            if (!writer.commit()) {
                rollback(writer);
                return ResponseEntity.internalServerError().body(Map.of("error", "Could not commit the back-paper registration transaction."));
            }

            if (!writer.setAutoCommit()) {
                return ResponseEntity.internalServerError().body(Map.of("error", "Back-paper data saved, but the database connection could not return to auto-commit mode."));
            }

            if (!controller.reRead()) {
                return ResponseEntity.internalServerError().body(Map.of("error", "Back-paper data saved, but the in-memory data cache could not be refreshed."));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Back-paper candidates saved successfully.",
                    "program", program,
                    "college", college,
                    "backPaperRowCount", rows.size()
            ));
        } catch (Exception exception) {
            rollback(writer);
            return ResponseEntity.internalServerError().body(Map.of("error", "Back-paper save failed: " + exception.getMessage()));
        }
    }

    /** POST /api/data/reload - re-reads all data from the database. */
    @PostMapping("/reload")
    public ResponseEntity<?> reload() {
        boolean success = controller.reRead();
        if (!success) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to reload data from database"));
        }
        return ResponseEntity.ok(Map.of("message", "Data reloaded"));
    }

    private static boolean groupExists(GroupList groupList, String faculty, String level, String discipline) {
        for (Group group : groupList.getGroups()) {
            if (group.getFaculty().equalsIgnoreCase(faculty)
                    && group.getLevel().equalsIgnoreCase(level)
                    && group.getDiscipline().equalsIgnoreCase(discipline)) {
                return true;
            }
        }
        return false;
    }

    private static boolean programExists(GroupList groupList, String faculty, String level, String discipline, String programName) {
        for (Group group : groupList.getGroups()) {
            if (!group.getFaculty().equalsIgnoreCase(faculty)
                    || !group.getLevel().equalsIgnoreCase(level)
                    || !group.getDiscipline().equalsIgnoreCase(discipline)) {
                continue;
            }

            ProgramList programs = groupList.getPrograms(group);
            if (programs == null) {
                return false;
            }

            for (Program program : programs.getPrograms()) {
                if (program.getProgramName().equalsIgnoreCase(programName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void rollback(DatabaseWriter writer) {
        writer.rollback();
        writer.setAutoCommit();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private static Integer parseSemesterValue(String value) {
        if (isBlank(value)) {
            return null;
        }

        return switch (value) {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            case "V" -> 5;
            case "VI" -> 6;
            case "VII" -> 7;
            case "VIII" -> 8;
            default -> {
                try {
                    yield Integer.parseInt(value);
                } catch (NumberFormatException ignored) {
                    yield null;
                }
            }
        };
    }
}
