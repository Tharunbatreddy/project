package com.example.proj.controller;

import com.example.proj.dto.TeamDTO;
import com.example.proj.dto.TeamSearchResponse;
import com.example.proj.dto.UserDTO;
import com.example.proj.entity.User;
import com.example.proj.exception.TeamException;
import com.example.proj.producer.TeamKafkaProducer;
import com.example.proj.service.TeamService;
import com.example.proj.utils.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teams")
@Validated
public class TeamController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamController.class);

    private final TeamKafkaProducer teamKafkaProducer;

    private final TeamService teamService;

    private static final String TEAM_NOT_FOUND_MSG = "Team not found.";
    private static final String TEAM_ALREADY_EXISTS_MSG = "Conflict: Team with the same name already exists.";
    private static final String UNEXPECTED_ERROR_MSG = "Unexpected error occurred while processing the team.";
    private static final String MESSAGE_KEY = "message";  // Define constant for "message"


    @Autowired
    public TeamController(TeamService teamService, TeamKafkaProducer teamKafkaProducer) {
        this.teamService = teamService;
        this.teamKafkaProducer = teamKafkaProducer;
    }


    @PostMapping
    public ResponseEntity<ApiResponse> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        try {
            if (teamService.isTeamNameExists(teamDTO.getName())) {
                LOGGER.warn("Team with name '{}' already exists", teamDTO.getName());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse.Builder("409", TEAM_ALREADY_EXISTS_MSG).build());
            }

            Long teamId = teamService.createTeam(teamDTO);
            LOGGER.info("Team created successfully with ID: {}", teamId);

            // Return only code, message, and teamId (omit teamSize)
            ApiResponse response = new ApiResponse.Builder("01", "Created: Team created successfully.")
                    .teamId(teamId)  // Only include teamId, exclude teamSize
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred while creating the team: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse.Builder("500", UNEXPECTED_ERROR_MSG).build());
        }
    }


    @PutMapping("/{team_id}")
    public ResponseEntity<ApiResponse> updateTeam(@PathVariable("team_id") Long teamId, @Valid @RequestBody TeamDTO teamDTO) {
        try {
            // Update the team
            TeamDTO updatedTeam = teamService.updateTeam(teamId, teamDTO);

            // Success: Return updated team details
            ApiResponse response = new ApiResponse.Builder("01", "Updated: Team updated successfully.")
                    .teamId(updatedTeam.getTeamId())
                    .name(updatedTeam.getName())
                    .description(updatedTeam.getDescription())
                    .createdAt(updatedTeam.getCreatedAt())
                    .updatedAt(updatedTeam.getUpdatedAt())
                    .teamSize(updatedTeam.getTeamSize())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (TeamException ex) {
            LOGGER.error("Error occurred while updating team: {}", ex.getMessage(), ex);
            if (ex.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse.Builder("409", "Conflict: Team could not be found for update.").build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse.Builder("404", TEAM_NOT_FOUND_MSG).build());
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred while updating the team: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse.Builder("500", UNEXPECTED_ERROR_MSG).build());
        }
    }


    @GetMapping("/{team_id}")
    public ResponseEntity<ApiResponse> getTeamById(@PathVariable("team_id") Long teamId) {
        try {
            TeamDTO teamDTO = teamService.getTeamById(teamId);

            ApiResponse response = new ApiResponse.Builder("01", "Success: Team fetched successfully.")
                    .teamId(teamDTO.getTeamId())
                    .name(teamDTO.getName())
                    .description(teamDTO.getDescription())
                    .createdAt(teamDTO.getCreatedAt())
                    .updatedAt(teamDTO.getUpdatedAt())
                    .teamSize(teamDTO.getTeamSize())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (TeamException ex) {
            LOGGER.error("Error occurred while fetching team: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse.Builder("404", TEAM_NOT_FOUND_MSG).build());
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred while fetching the team: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse.Builder("500", UNEXPECTED_ERROR_MSG).build());
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> addUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            User user = new User(userDTO.getName(), userDTO.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            MESSAGE_KEY, "User created successfully.",
                            "userId", user.getId()
                    ));
        } catch (Exception e) {
            LOGGER.error("Error occurred while creating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            MESSAGE_KEY, "An unexpected error occurred.",
                            "details", e.getMessage()
                    ));
        }
    }

    @PatchMapping("/{team_id}/users")
    public ResponseEntity<List<Map<String, Object>>> assignUsersToTeam(
            @PathVariable("team_id") Long teamId,
            @RequestBody List<Long> userIds){
        try {
            if (userIds == null || userIds.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(List.of(Map.of( "code", 400, MESSAGE_KEY, "User list cannot be empty.")));
            }

            List<Map<String, Object>> response = teamService.assignTeamUser(teamId, userIds);
            return ResponseEntity.status(HttpStatus.OK).body(response); // 200 OK
        } catch (TeamException ex) {
            LOGGER.error("Error occurred while assigning users to team: {}", ex.getMessage(), ex);
            return ResponseEntity.status(ex.getStatus()).body(List.of(
                    Map.of("code", ex.getStatus().value(), MESSAGE_KEY, ex.getMessage())
            ));
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred while assigning users to team: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(
                    Map.of("code", "500", MESSAGE_KEY, "Unexpected server error")
            ));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchTeams(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {


        if (search == null || search.trim().isEmpty()) {
            throw new TeamException("Search query cannot be empty", HttpStatus.BAD_REQUEST);
        }


        TeamSearchResponse response = teamService.searchTeams(page, limit, search, description);


        if (response.getResults().isEmpty()) {
            throw new TeamException("No teams found for the search query", HttpStatus.NOT_FOUND);
        }

        ApiResponse apiResponse = new ApiResponse.Builder("01", "Success: Teams retrieved successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/send-name")
    public ResponseEntity<String> sendTeamName(@RequestBody String teamName) {
        teamKafkaProducer.sendTeamName(teamName);
        return ResponseEntity.ok("Team name sent to Kafka");
    }
}

