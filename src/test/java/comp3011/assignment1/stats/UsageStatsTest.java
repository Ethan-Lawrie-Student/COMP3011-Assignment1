package comp3011.assignment1.stats;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


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
    
    
    
    @Test
    void keepsAllUpdatesWhenRunConcurrently() throws Exception {
        UsageStats stats = new UsageStats();

        ExecutorService workers = Executors.newFixedThreadPool(20);
        List<Future<?>> tasks = new ArrayList<>();

        try {
            for (int taskNumber = 0; taskNumber < 500; taskNumber++) {
                tasks.add(
                        workers.submit(() -> stats.add(3, 2))
                );
            }

            for (Future<?> task : tasks) {
                task.get(10, TimeUnit.SECONDS);
            }

            GlobalStatsResponse result = stats.snapshot();

            assertEquals(1500L, result.inputTokens());
            assertEquals(1000L, result.outputTokens());

        } finally {
            workers.shutdownNow();
        }
    }
    
    
}