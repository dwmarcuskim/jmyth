package com.marcus.jmyth;

import com.marcus.jmyth.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AssistanceClientService {
    private final AssistantClient assistantsClient;

    public AssistanceClientService(AssistantClient assistantsClient) {
        this.assistantsClient = assistantsClient;
    }

    public ResponseEntity<ThreadsResponseDto> createThreads() {
        try {
            var res = assistantsClient.createThreads();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Error creating threads", e);
            var body = new ThreadsResponseDto();
            body.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(body);
        }
    }

    public ResponseEntity<ThreadsDeleteResponseDto> deleteThreads(String threadId) {
        try {
            var res = assistantsClient.deleteThreads(threadId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Error deleting threads", e);
            var body = new ThreadsDeleteResponseDto();
            body.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(body);
        }
    }

    public ResponseEntity<MessagesResponseDto> createMessages(String threadId, MessagesRequestDto messagesRequestDto) {
        try {
            var res = assistantsClient.createMessages(threadId, messagesRequestDto);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Error creating messages", e);
            var body = new MessagesResponseDto();
            body.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(body);
        }
    }

    public ResponseEntity<MessagesListResponseDto> getMessagesList(String threadId, String before) {
        try {
            var res = assistantsClient.getMessagesList(threadId, before);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Error getting messages list", e);
            var body = new MessagesListResponseDto();
            body.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(body);
        }
    }

    public ResponseEntity<RunsResponseDto> createRuns(String threadId, RunsRequestDto createRunsRequestDto) {
        try {
            var res = assistantsClient.createRuns(threadId, createRunsRequestDto);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Error creating runs", e);
            var body = new RunsResponseDto();
            body.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(body);
        }
    }

    public ResponseEntity<RunsResponseDto> getRuns(String threadId, String runId) {
        try {
            var res = assistantsClient.getRun(threadId, runId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Error getting runs", e);
            var body = new RunsResponseDto();
            body.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(body);
        }
    }
}
