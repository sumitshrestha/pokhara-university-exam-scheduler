package puexamroutine.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import puexamroutine.control.Controller;
import puexamroutine.control.domain.list.GroupList;
import puexamroutine.control.domain.list.CentreTable;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GroupController {

    private final Controller controller;

    public GroupController(Controller controller) {
        this.controller = controller;
    }

    @GetMapping("/groups")
    public ResponseEntity<GroupList> getGroups() {
        GroupList groups = controller.getGroupList();
        if (groups == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/centres")
    public ResponseEntity<CentreTable> getCentres() {
        CentreTable centres = controller.getCentres();
        if (centres == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(centres);
    }
}
