package comp3011.assignment1.stats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalStatsControllerTest {

    @Test
    void returnsCurrentStats() {
        UsageStats stats = new UsageStats();
        GlobalStatsController controller =
                new GlobalStatsController(stats);

        stats.add(20, 8);

        GlobalStatsResponse result = controller.getGlobalStats();

        assertEquals(20L, result.inputTokens());
        assertEquals(8L, result.outputTokens());
    }
}