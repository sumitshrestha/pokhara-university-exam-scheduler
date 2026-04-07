package puexamroutine.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import puexamroutine.control.Controller;
import puexamroutine.control.domain.Group;
import puexamroutine.control.domain.list.ProgramList;
import puexamroutine.control.domain.list.GroupList;
import puexamroutine.control.domain.list.CentreTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GroupController {

    private final Controller controller;

    public GroupController(Controller controller) {
        this.controller = controller;
    }

    @GetMapping("/groups")
    public ResponseEntity<?> getGroups() {
        GroupList groups = controller.getGroupList();
        if (groups == null) return ResponseEntity.noContent().build();

        List<GroupSummary> rows = new ArrayList<>();
        for (Group group : groups.getGroups()) {
            ProgramList programs = groups.getPrograms(group);
            List<String> programNames = programs == null
                    ? List.of()
                    : programs.getPrograms().stream()
                    .map(program -> program.getProgramName())
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();

                rows.add(new GroupSummary(
                    group.getFaculty(),
                    group.getLevel(),
                    group.getDiscipline(),
                    programNames
                ));
        }

        rows.sort(Comparator
                .comparing(GroupSummary::faculty, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(GroupSummary::level, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(GroupSummary::discipline, String.CASE_INSENSITIVE_ORDER));

        return ResponseEntity.ok(Map.of("groups", rows));
    }

    @GetMapping("/centres")
    public ResponseEntity<?> getCentres() {
        CentreTable centres = controller.getCentres();
        if (centres == null) return ResponseEntity.noContent().build();

        List<CentreSummary> rows = centres.getCentres().stream()
            .map(c -> new CentreSummary(c.getCentreName(), c.getMaximumCentreLimit()))
            .sorted(Comparator.comparing(CentreSummary::centreName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        return ResponseEntity.ok(Map.of("centreList", rows));
    }

        private record GroupSummary(String faculty, String level, String discipline, List<String> programs) {}

        private record CentreSummary(String centreName, int maxLimit) {}
}
