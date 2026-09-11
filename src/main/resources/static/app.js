const startButton = document.getElementById("startButton");
const stopButton = document.getElementById("stopButton");
const statusElement = document.getElementById("status");
const transcriptionElement = document.getElementById("transcription");

let mediaRecorder;
let audioChunks = [];
let mediaStream;




startButton.addEventListener("click", startListening);

async function startListening() {
    try {
        mediaStream = await navigator.mediaDevices.getUserMedia({
            audio: true
        });

        audioChunks = [];

        mediaRecorder = new MediaRecorder(mediaStream);

        mediaRecorder.addEventListener("dataavailable", event => {
            if (event.data.size > 0) {
                audioChunks.push(event.data);
            }
        });

        mediaRecorder.addEventListener("stop", handleRecordingStopped);

        mediaRecorder.start();

        statusElement.textContent = "Recording...";
        startButton.disabled = true;
        stopButton.disabled = false;

    } catch (error) {
        console.error("Can't access your microphone:", error);
        statusElement.textContent =
            "Unable to access the microphone";
    }
}






stopButton.addEventListener("click", stopRecording);

function stopRecording() {
    if (!mediaRecorder || mediaRecorder.state !== "recording") {
        return;
    }

    mediaRecorder.stop();

    statusElement.textContent = "Processing...";
    stopButton.disabled = true;

    if (mediaStream) {
        mediaStream.getTracks().forEach(track => track.stop());
    }
}



async function handleRecordingStopped() {
    try {
        const mimeType = mediaRecorder.mimeType || "audio/webm";

        const audioBlob = new Blob(audioChunks, {
            type: mimeType
        });

        await uploadAudio(audioBlob);

    } catch (error) {
        console.error("FAILS :", error);
        statusElement.textContent = "Can't process recording.";
        resetRecorder();
    }
}






async function uploadAudio(audioBlob) {
    statusElement.textContent = "Transcribing...";

    const formData = new FormData();

    formData.append(
        "file",
        audioBlob,
        "test.webm"
    );

    try {
        const response = await fetch("/api/v1/transcriptions", {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error(`Server returned ${response.status}`);
        }

        const result = await response.json();

        transcriptionElement.textContent = result.text;
        statusElement.textContent = "Ready";

    } catch (error) {
        console.error("Upload failed:", error);

        statusElement.textContent =
            "Cant transcribe recording";

    } finally {
        resetRecorder();
    }
}




function resetRecorder() {
    mediaRecorder = null;
    mediaStream = null;
    audioChunks = [];

    startButton.disabled = false;
    stopButton.disabled = true;

    if (!statusElement.textContent.startsWith("Unable")) {
        statusElement.textContent = "Ready";
    }
}
