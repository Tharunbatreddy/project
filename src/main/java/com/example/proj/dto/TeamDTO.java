package com.example.proj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TeamDTO {
    private Long teamId;

    @NotBlank(message = "Team name cannot be blank")
    @Size(max = 100, message = "Team name must be at most 100 characters")
    private String name;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    @NotNull(message = "Team size cannot be null")
    private Integer teamSize;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonProperty("userIds")
    private List<Long> userIds = new ArrayList<>();
}