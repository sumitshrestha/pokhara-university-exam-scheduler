package puexamroutine.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import puexamroutine.control.Controller;
import puexamroutine.control.schedule.Result;

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
            return ResponseEntity.unprocessableEntity()
                    .body(Map.of("error", result.getError() != null ? result.getError() : "Scheduling failed"));
        }
        return ResponseEntity.ok(result);
    }
}
