package comp3011.assignment1.admin;

import java.lang.management.ManagementFactory;


import comp3011.assignment1.api.ErrorResponse;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

	
	
    private Instant serverStart = Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime());

    
    private final ShutdownService shutdownService;
    
    public AdminController(ShutdownService shutdownService) {
        this.shutdownService = shutdownService;
    }
    
    
    @GetMapping("/uptime")
    public UptimeResponse getServerUptime() {
        Instant now = Instant.now();
        Duration uptime = Duration.between(serverStart, now);
        double uptimeSeconds = uptime.toNanos() / 1000000000.0;
        return new UptimeResponse(serverStart, now, uptimeSeconds);
    }
    
    @PostMapping("/shutdown")
    public ResponseEntity<?> shutdownTheServer() {
        if(!shutdownService.requestStop()) {
        	ErrorResponse e = new ErrorResponse(Instant.now(), 409, "Conflict", "Shutdown already happening", "/api/v1/admin/shutdown");
        	return ResponseEntity.status(HttpStatus.CONFLICT).body(e);
        } else {
        	return ResponseEntity.accepted().body(new ShutdownResponse("worked"));
        }
    }
}