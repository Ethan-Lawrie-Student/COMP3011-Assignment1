package comp3011.assignment1.stats;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsageStatsTest {

    @Test
    void startsAtZero() {
        UsageStats stats = new UsageStats();

        GlobalStatsResponse result = stats.snapshot();

        assertEquals(0L, result.inputTokens());
        assertEquals(0L, result.outputTokens());
    }

    @Test
    void addsTokens() {
        UsageStats stats = new UsageStats();

        stats.add(10, 4);

        GlobalStatsResponse result = stats.snapshot();

        assertEquals(10L, result.inputTokens());
        assertEquals(4L, result.outputTokens());
    }

    @Test
    void addsMultipleRequestsTogether() {
        UsageStats stats = new UsageStats();

        stats.add(10, 4);
        stats.add(5, 2);

        GlobalStatsResponse result = stats.snapshot();

        assertEquals(15L, result.inputTokens());
        assertEquals(6L, result.outputTokens());
    }
    
    @Test
    void rejectsNegativeTokens() {
        UsageStats stats = new UsageStats();
        stats.add(10, 4);

        assertThrows(
                IllegalArgumentException.class,
                () -> stats.add(-5, 2)
        );

        GlobalStatsResponse result = stats.snapshot();

        assertEquals(10L, result.inputTokens());
        assertEquals(4L, result.outputTokens());
    }
}