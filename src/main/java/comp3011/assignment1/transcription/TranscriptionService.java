package comp3011.assignment1.transcription;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import comp3011.assignment1.stats.UsageStats;


@Service
public class TranscriptionService {

    private final RestClient restClient;
    private final UsageStats usageStats;

    public TranscriptionService(
            RestClient.Builder restClientBuilder,
            @Value("${openai.api-key}") String apiKey,
            UsageStats usageStats) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY environment variable is not configured"
            );
        }
        
        this.usageStats = usageStats;

        this.restClient = restClientBuilder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .build();
    }

    public OpenAiTranscriptionResponse transcribe(MultipartFile audioFile) {
        MultiValueMap<String, Object> requestBody =
                new LinkedMultiValueMap<>();

        requestBody.add("file", audioFile.getResource());
        requestBody.add("model", "gpt-4o-mini-transcribe");
        requestBody.add("response_format", "json");

        OpenAiTranscriptionResponse response = restClient.post()
                .uri("/audio/transcriptions")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(requestBody)
                .retrieve()
                .body(OpenAiTranscriptionResponse.class);

        if (response == null || response.text() == null) {
            throw new IllegalStateException(
                    "OpenAI returned an invalid transcription response"
            );
        }
        
        
        usageStats.add(
                response.usage().inputTokens(),
                response.usage().outputTokens()
        );

        return response;
    }
}