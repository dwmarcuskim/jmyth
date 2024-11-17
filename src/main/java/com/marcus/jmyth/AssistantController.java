package com.marcus.jmyth;

import com.marcus.jmyth.dto.MessagesRequestDto;
import com.marcus.jmyth.dto.RunsRequestDto;
import com.marcus.jmyth.dto.RunsResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/assistant")
@CrossOrigin(origins = {"http://localhost:5173", "https://dwmarcuskim.github.io"})
public class AssistantController {
    private final AtomicInteger threadCounter = new AtomicInteger(0);
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(0, r -> Thread.ofVirtual()
            .name("assistant-thread-%d".formatted(threadCounter.getAndIncrement()))
            .factory()
            .newThread(r));

    private final AssistanceClientService assistanceClientService;
    private final OpenAIProperties openAIProperties;

    public AssistantController(AssistanceClientService assistanceClientService, OpenAIProperties openAIProperties) {
        this.assistanceClientService = assistanceClientService;
        this.openAIProperties = openAIProperties;
    }

    @PostMapping("/thread")
    public ResponseEntity<Object> getThread() {
        var threads = assistanceClientService.createThreads();
        if (threads.getStatusCode().isError()) {
            if (threads.getBody() == null) {
                return ResponseEntity.status(threads.getStatusCode()).build();
            }
            return ResponseEntity.status(threads.getStatusCode()).body(threads.getBody());
        }
        if (threads.getBody() == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(threads.getBody().getId());
    }

    @DeleteMapping("/thread/{threadId}")
    public ResponseEntity<Object> deleteThread(@PathVariable("threadId") String threadId) {
        var threads = assistanceClientService.deleteThreads(threadId);
        if (threads.getStatusCode().isError()) {
            if (threads.getBody() == null) {
                return ResponseEntity.status(threads.getStatusCode()).build();
            }
            return ResponseEntity.status(threads.getStatusCode()).body(threads.getBody());
        }
        if (threads.getBody() == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(threads.getBody().getId());
    }

    @GetMapping("/thread/{threadId}/message")
    public ResponseEntity<Object> getMessagesList(@PathVariable("threadId") String threadId) {
        var mList = assistanceClientService.getMessagesList(threadId, null);
        if (mList.getStatusCode().isError()) {
            if (mList.getBody() == null) {
                return ResponseEntity.status(mList.getStatusCode()).build();
            }
            return ResponseEntity.status(mList.getStatusCode()).body(mList.getBody());
        }

        if (mList.getBody() == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(mList.getBody().getData());
    }

    @PostMapping("/thread/{threadId}/message")
    public ResponseEntity<Object> createMessage(@PathVariable("threadId") String threadId, @RequestBody String message) {
        try {
            var messageId = postAndGetMessageId(threadId, message);
            log.info("Message created: {}", messageId);

            var runId = postAndGetRunId(threadId);
            log.info("Run created: {}", runId);

            var future = asyncRunCheck(threadId, runId);
            var run = future.get();
            log.info("Run completed: {}", run.getId());

            var mList = assistanceClientService.getMessagesList(threadId, messageId);
            if (mList.getStatusCode().isError()) {
                if (mList.getBody() == null) {
                    return ResponseEntity.status(mList.getStatusCode()).build();
                }
                return ResponseEntity.status(mList.getStatusCode()).body(mList.getBody());
            }

            if (mList.getBody() == null) {
                log.error("Error getting messages list: Empty body.");
                return ResponseEntity.internalServerError().build();
            }

            log.info("Messages list: {}", mList.getBody().getData());
            return ResponseEntity.ok(mList.getBody().getData());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            log.error("Error creating message: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private String postAndGetMessageId(String threadId, String message) {
        var messages = assistanceClientService.createMessages(threadId, new MessagesRequestDto("user", message));
        if (messages.getStatusCode().isError()) {
            if (messages.getBody() == null) {
                log.error("Error creating message: {}", messages.getStatusCode());
                throw new ResponseStatusException(messages.getStatusCode(), "Error creating message");
            }
            log.error("Error creating message: {} - {}", messages.getStatusCode(), messages.getBody().getError());
            throw new ResponseStatusException(messages.getStatusCode(), messages.getBody().getError());
        }
        if (messages.getBody() == null) {
            log.error("Error creating message: Empty body.");
            throw new ResponseStatusException(messages.getStatusCode(), "Error creating message");
        }

        return messages.getBody().getId();
    }

    private String postAndGetRunId(String threadId) {
        var runRequest = new RunsRequestDto();
        runRequest.setAssistantId(openAIProperties.getAssistantId());
        var run = assistanceClientService.createRuns(threadId, runRequest);
        if (run.getStatusCode().isError()) {
            if (run.getBody() == null) {
                log.error("Error creating run: {}", run.getStatusCode());
                throw new ResponseStatusException(run.getStatusCode(), "Error creating run");
            }
            log.error("Error creating run: {} - {}", run.getStatusCode(), run.getBody().getError());
            throw new ResponseStatusException(run.getStatusCode(), run.getBody().getError());
        }

        if (run.getBody() == null) {
            log.error("Error creating run: Empty body.");
            throw new ResponseStatusException(run.getStatusCode(), "Error creating run");
        }

        log.info("Run created: {}", run.getBody().getId());
        return run.getBody().getId();
    }

    private CompletableFuture<RunsResponseDto> asyncRunCheck(String threadId, String runId) {
        var future = new CompletableFuture<RunsResponseDto>();
        var scheduled = executorService.scheduleWithFixedDelay(() -> {
            try {
                var response = assistanceClientService.getRuns(threadId, runId);
                if (response.getStatusCode().isError()) {
                    if (response.getBody() == null) {
                        future.completeExceptionally(new ResponseStatusException(response.getStatusCode()));
                        return;
                    }
                    future.completeExceptionally(new ResponseStatusException(response.getStatusCode(), response.getBody().getError()));
                    return;
                }
                var run = response.getBody();
                if (run == null) {
                    future.completeExceptionally(new ResponseStatusException(response.getStatusCode(), "Empty body"));
                    return;
                }
                if (run.getStatus().equals("completed")) {
                    future.complete(run);
                    return;
                }
                log.info("Run status: {}. Retrying after 1sec...", run.getStatus());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        future.whenComplete((r, e) -> scheduled.cancel(true));
        return future;
    }
}
