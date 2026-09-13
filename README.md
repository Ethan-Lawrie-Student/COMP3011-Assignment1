# COMP3011 Assignment 1

A Spring Boot application that records audio through a web browser, sends it to a Java backend and converts it to text using the OpenAI speech-to-text API.


## API endpoints

GET
- `/api/v1/admin/uptime`
- `/api/v1/global/stats`

POST
- `/api/v1/admin/shutdown`
- `/api/v1/transcriptions`


## Design

### Transcription requests

The backend uses Spring's rest client to send recorded audio to the OpenAI transcription API using the `gpt-4o-mini-transcribe` model.

This is a blocking network operation because the request waits for OpenAI to return a result. Virtual threads are on so many requests can wait without requiring one big thread for every request.


### Token statistics

UsageStats stores the overall aggregated input and output token counts returned by the transcription service.

Its update and snapshot methods are synchronised because multiple transcription requests may end at the same time and break it. Synchronisation prevents updates from being lost and ensures that a response cannot observe a slighlty completed update.

The statistics are stored in memory and restarts when the process restarts.

### Graceful shutdown

An atomic boolean is used instead of a normal boolean to make sure that only the first shutdown request is accepted. Later requests received while shutdown is starting return the 409 conflict error.

Application shutdown was intentionally made to run on a separate thread so the controller can return its 202 response before the application closes.


### Configuration and security

The OpenAI API key is read at runtime so that the same application to run locally and on TITAN without hardcoding the credentials.

## Testing

All the tests are run with 

```powershell
.\mvnw.cmd test
```

The test suite includes:

- Tests for initial, accumulated and invalid token statistics.
- A regression test that performs 500 concurrent statistics updates and verifies that no updates are lost because of race conditions or anything.
- A test for the global statistics controller.
- A concurrency test that simulates 250 simultaneous multipart transcription requests through the Spring controller.

The 250-request test replaces the real transcription service with a mock. Each request is deliberately blocked until all 250 requests have entered the service. The requests are then released and checked for successful responses. It was made this way to not have to use a large amount of actual requests to the openAI api.



## Development assistance

Generative AI was used alongside normal research (and the course slides and pracs) to help explain Spring Boot configuration, virtual threads, graceful shutdown and how I could potentially approach the regression testing.

The suggestions were reviewed, adapted and built upon, and tested against the assignment specification and TITAN. The final implementation and design decisions were checked to ensure they could be understood and explained.
