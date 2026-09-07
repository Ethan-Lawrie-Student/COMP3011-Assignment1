package comp3011.assignment1.admin;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private Instant serverStart = Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime());

    @GetMapping("/uptime")
    public UptimeResponse getServerUptime() {
        Instant now = Instant.now();
        Duration uptime = Duration.between(serverStart, now);
        double uptimeSeconds = uptime.toNanos() / 1000000000.0;
        return new UptimeResponse(serverStart, now, uptimeSeconds);
    }
}