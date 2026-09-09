package comp3011.assignment1.stats;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/global")
public class GlobalStatsController {

    private final UsageStats usageStats;

    public GlobalStatsController(UsageStats usageStats) {
        this.usageStats = usageStats;
    }

    @GetMapping("/stats")
    public GlobalStatsResponse getGlobalStats() {
        return usageStats.snapshot();
    }
}