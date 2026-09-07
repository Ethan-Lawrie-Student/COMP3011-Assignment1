package comp3011.assignment1.transcription;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class TranscriptionController {

    @PostMapping("/api/v1/transcriptions")
    public ResponseEntity<Map<String, String>> transcribe(@RequestPart("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Audio file is empty"));
        }

        return ResponseEntity.ok(
                Map.of("text", "Audio received")
        );
    }
}