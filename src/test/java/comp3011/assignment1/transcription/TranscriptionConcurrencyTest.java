package comp3011.assignment1.transcription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(TranscriptionController.class)
class TranscriptionConcurrencyTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TranscriptionService transcriptionService;

    @Test
    void handles250RequestsAtTheSameTime() throws Exception {
        int requestCount = 250;

        CountDownLatch requestsStarted =
                new CountDownLatch(requestCount);

        CountDownLatch letRequestsFinish =
                new CountDownLatch(1);

        when(transcriptionService.transcribe(
                any(MultipartFile.class)
        )).thenAnswer(call -> {
            requestsStarted.countDown();

            letRequestsFinish.await();

            return new OpenAiTranscriptionResponse(
                    "Test transcription",
                    null
            );
        });

        ExecutorService executor =
                Executors.newVirtualThreadPerTaskExecutor();

        List<Future<Integer>> results = new ArrayList<>();

        try {
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < requestCount; i++) {
                results.add(executor.submit(() -> {
                    MockMultipartFile audio =
                            new MockMultipartFile(
                                    "file",
                                    "recording.webm",
                                    "audio/webm",
                                    new byte[] {1, 2, 3}
                            );

                    return mockMvc.perform(
                            multipart("/api/v1/transcriptions")
                                    .file(audio)
                    ).andReturn().getResponse().getStatus();
                }));
            }

            boolean allRequestsStarted =
                    requestsStarted.await(
                            10,
                            TimeUnit.SECONDS
                    );

            assertTrue(
                    allRequestsStarted,
                    "Not all 250 requests ran concurrently"
            );

            letRequestsFinish.countDown();

            for (Future<Integer> result : results) {
                assertEquals(
                        200,
                        result.get(10, TimeUnit.SECONDS)
                );
            }

            long timeTaken =
                    System.currentTimeMillis() - startTime;

            assertTrue(
                    timeTaken < 10_000,
                    "The requests took too long: "
                            + timeTaken
                            + " milliseconds"
            );

        } finally {
            letRequestsFinish.countDown();
            executor.shutdownNow();
        }
    }
}