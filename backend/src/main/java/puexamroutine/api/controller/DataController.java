package puexamroutine.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import puexamroutine.control.Controller;
import puexamroutine.control.domain.Program;

import java.util.Map;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataController {

    private final Controller controller;

    public DataController(Controller controller) {
        this.controller = controller;
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
}
