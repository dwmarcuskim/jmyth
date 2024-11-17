package com.marcus.jmyth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MessagesListResponseDto {
    private String object;
    private List<MessageData> data;
    private String firstId;
    private String lastId;
    private boolean hasMore;

    private String error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class MessageData {
        private String id;
        private String object;
        private Long createdAt;
        private String threadId;
        private String role;
        private List<Content> content;
        private String assistantId;
        private String runId;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Content {
            private String type;
            private Text text;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
            public static class Text {
                private String value;
            }
        }
    }
}
