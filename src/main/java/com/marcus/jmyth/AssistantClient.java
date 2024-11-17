package com.marcus.jmyth;


import com.marcus.jmyth.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "OpenAiAssistantsClient", url = "https://api.openai.com/v1", configuration = AssistantHeaderConfiguration.class)
public interface AssistantClient {
    @PostMapping("/threads")
    ThreadsResponseDto createThreads();

    @DeleteMapping("/threads/{threadId}")
    ThreadsDeleteResponseDto deleteThreads(@PathVariable String threadId);

    @PostMapping("/threads/{threadId}/messages")
    MessagesResponseDto createMessages(@PathVariable String threadId, @RequestBody MessagesRequestDto messagesRequestDto);

    @GetMapping("/threads/{threadId}/messages")
    MessagesListResponseDto getMessagesList(@PathVariable String threadId, @RequestParam("before") String before);

    @PostMapping("/threads/{threadId}/runs")
    RunsResponseDto createRuns(@PathVariable String threadId, @RequestBody RunsRequestDto runsRequestDto);

    @GetMapping("/threads/{threadId}/runs/{runId}")
    RunsResponseDto getRun(@PathVariable String threadId, @PathVariable String runId);

}
