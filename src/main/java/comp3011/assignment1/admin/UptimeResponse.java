package comp3011.assignment1.admin;

import java.time.Instant;

public record UptimeResponse(Instant utcServerStart,
		Instant utcNow,
		double serverUptimeSeconds) {
	
}
