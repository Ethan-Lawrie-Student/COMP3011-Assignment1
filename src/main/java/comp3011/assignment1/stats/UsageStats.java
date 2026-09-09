package comp3011.assignment1.stats;

import org.springframework.stereotype.Service;

@Service
public class UsageStats {

    private long inputTokens;
    private long outputTokens;

    public synchronized void add(long input, long output) {
        if (input < 0 || output < 0) {
            throw new IllegalArgumentException(
                    "Token counts cannot be negative"
            );
        }

        inputTokens += input;
        outputTokens += output;
    }

    public synchronized GlobalStatsResponse snapshot() {
        return new GlobalStatsResponse(
                inputTokens,
                outputTokens
        );
    }
}