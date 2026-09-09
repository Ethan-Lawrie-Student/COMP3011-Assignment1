package comp3011.assignment1.transcription;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TranscriptionController {

    private final TranscriptionService transcriptionService;

    public TranscriptionController(
            TranscriptionService transcriptionService) {
        this.transcriptionService = transcriptionService;
    }

    @PostMapping("/api/v1/transcriptions")
    public ResponseEntity<Map<String, String>> transcribe(
            @RequestPart("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Audio file is empty"));
        }

        OpenAiTranscriptionResponse response =
                transcriptionService.transcribe(file);

        return ResponseEntity.ok(
                Map.of("text", response.text())
        );
    }
}