package com.example.proj.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private final String code;
    private final String message;
    private final Object data;
    private final Long teamId;
    private final String name;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Integer teamSize;
    private final List<String> messages;

    // Private constructor to enforce Builder pattern
    private ApiResponse(Builder builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.data = builder.data;
        this.teamId = builder.teamId;
        this.name = builder.name;
        this.description = builder.description;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.teamSize = builder.teamSize;
        this.messages = builder.messages;
    }

    public static class Builder {
        private final String code;
        private final String message;
        private Object data = null;
        private Long teamId = null;
        private String name = null;
        private String description = null;
        private LocalDateTime createdAt = null;
        private LocalDateTime updatedAt = null;
        private Integer teamSize = null;
        private List<String> messages = null;

        public Builder(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder teamId(Long teamId) {
            this.teamId = teamId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder teamSize(Integer teamSize) {
            this.teamSize = teamSize;
            return this;
        }

        public Builder messages(List<String> messages) {
            this.messages = messages;
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(this);
        }
    }

    // Getters for all fields
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Integer getTeamSize() {
        return teamSize;
    }

    public List<String> getMessages() {
        return messages;
    }
}

