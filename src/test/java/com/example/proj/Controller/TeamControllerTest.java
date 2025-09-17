package com.example.proj.Controller;
import com.example.proj.controller.TeamController;
import com.example.proj.dto.TeamDTO;
import com.example.proj.dto.TeamSearchResponse;
import com.example.proj.exception.TeamException;
import com.example.proj.service.TeamService;
import com.example.proj.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;


class TeamControllerTest {
    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTeam() {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setName("Test Team");

        when(teamService.isTeamNameExists(anyString())).thenReturn(false);
        when(teamService.createTeam(any(TeamDTO.class))).thenReturn(1L);

        ResponseEntity<ApiResponse> response = teamController.createTeam(teamDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(true, response.getBody().getMessage().contains("Team created successfully"));
        verify(teamService).createTeam(any(TeamDTO.class));
    }

    @Test
    void testCreateTeamConflict() {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setName("Existing Team");

        when(teamService.isTeamNameExists(anyString())).thenReturn(true);

        ResponseEntity<ApiResponse> response = teamController.createTeam(teamDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(true, response.getBody().getMessage().contains("Team with the same name already exists"));
    }

    @Test
    void testUpdateTeam() {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setName("Updated Team");

        TeamDTO updatedTeam = new TeamDTO();
        updatedTeam.setTeamId(1L);
        updatedTeam.setName("Updated Team");
        updatedTeam.setDescription("Updated description");

        when(teamService.updateTeam(anyLong(), any(TeamDTO.class))).thenReturn(updatedTeam);

        ResponseEntity<ApiResponse> response = teamController.updateTeam(1L, teamDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Team", response.getBody().getName());
        assertEquals("Updated description", response.getBody().getDescription());
        verify(teamService).updateTeam(anyLong(), any(TeamDTO.class));
    }
    @Test
    void testGetTeamById() {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamId(1L);
        teamDTO.setName("Test Team");

        when(teamService.getTeamById(anyLong())).thenReturn(teamDTO);

        ResponseEntity<ApiResponse> response = teamController.getTeamById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Team", response.getBody().getName());
    }
    @Test
    void testGetTeamByIdNotFound() {
        when(teamService.getTeamById(anyLong())).thenThrow(new TeamException("Team not found", HttpStatus.NOT_FOUND));

        ResponseEntity<ApiResponse> response = teamController.getTeamById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(true, response.getBody().getMessage().contains("Team not found"));
    }

    @Test
    void testAssignUsersToTeamBadRequest() {
        List<Long> userIds = List.of();

        ResponseEntity<List<Map<String, Object>>> response = teamController.assignUsersToTeam(1L, userIds);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User list cannot be empty.", response.getBody().get(0).get("message"));
    }

    @Test
    void testSearchTeams() {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamId(1L);
        teamDTO.setName("Test Team");

        TeamSearchResponse mockResponse = new TeamSearchResponse();
        mockResponse.setResults(List.of(teamDTO));
        mockResponse.setTotalCount(1);
        mockResponse.setPageCount(1);
        mockResponse.setCurrentPage(1);

        when(teamService.searchTeams(1, 10, "test", "description")).thenReturn(mockResponse);

        ResponseEntity<ApiResponse> response = teamController.searchTeams("test", "description", 1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Success: Teams retrieved successfully", response.getBody().getMessage());

        assertTrue(response.getBody().getData() instanceof TeamSearchResponse);
        TeamSearchResponse responseData = (TeamSearchResponse) response.getBody().getData();

        assertNotNull(responseData);
        assertEquals(1, responseData.getTotalCount());
        assertEquals(1, responseData.getPageCount());
        assertEquals(1, responseData.getCurrentPage());
        assertEquals(1, responseData.getResults().size());
        assertEquals("Test Team", responseData.getResults().get(0).getName());
    }

    @Test
    void testSearchTeamsNoResults() {
        TeamSearchResponse mockResponse = new TeamSearchResponse();
        mockResponse.setResults(List.of());

        when(teamService.searchTeams(anyInt(), anyInt(), anyString(), anyString())).thenReturn(mockResponse);

        TeamException exception = assertThrows(
                TeamException.class,
                () -> teamController.searchTeams("test", "description", 1, 10)
        );

        assertEquals("No teams found for the search query", exception.getMessage());
    }

    @Test
    void testCreateTeamUnexpectedError() {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setName("Test Team");

        when(teamService.isTeamNameExists(anyString())).thenReturn(false);
        when(teamService.createTeam(any(TeamDTO.class))).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<ApiResponse> response = teamController.createTeam(teamDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(true, response.getBody().getMessage().contains("Unexpected error occurred while processing the team"));
    }

    @Test
    void testUpdateTeamNotFound() {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setName("Updated Team");

        when(teamService.updateTeam(anyLong(), any(TeamDTO.class)))
                .thenThrow(new TeamException("Team not found", HttpStatus.NOT_FOUND));

        ResponseEntity<ApiResponse> response = teamController.updateTeam(999L, teamDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(true, response.getBody().getMessage().contains("Team not found"));
    }

    @Test
    void testAssignUsersToTeamTeamNotFound() {
        List<Long> userIds = List.of(1L, 2L);

        when(teamService.assignTeamUser(anyLong(), anyList()))
                .thenThrow(new TeamException("Team not found", HttpStatus.NOT_FOUND));

        ResponseEntity<List<Map<String, Object>>> response = teamController.assignUsersToTeam(999L, userIds);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Team not found", response.getBody().get(0).get("message"));
    }
    @Test
    void testSearchTeamsInvalidQuery() {
        TeamException exception = assertThrows(
                TeamException.class,
                () -> teamController.searchTeams("", null, 1, 10)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Search query cannot be empty", exception.getMessage());
    }
    @Test
    void testAssignUsersToTeamSuccess() {
        List<Long> userIds = List.of(1L, 2L);
        List<Map<String, Object>> mockResponse = List.of(
                Map.of("userId", 1L, "message", "User 1 successfully added"),
                Map.of("userId", 2L, "message", "User 2 successfully added")
        );

        when(teamService.assignTeamUser(anyLong(), anyList())).thenReturn(mockResponse);

        ResponseEntity<List<Map<String, Object>>> response = teamController.assignUsersToTeam(1L, userIds);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("User 1 successfully added", response.getBody().get(0).get("message"));
        verify(teamService).assignTeamUser(1L, userIds);
    }
    @Test
    void testAssignUsersToTeamDuplicateIds() {
        List<Long> userIds = List.of(1L, 1L, 2L);
        when(teamService.assignTeamUser(anyLong(), anyList()))
                .thenThrow(new TeamException("Duplicate user IDs detected", HttpStatus.BAD_REQUEST));

        ResponseEntity<List<Map<String, Object>>> response = teamController.assignUsersToTeam(1L, userIds);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Duplicate user IDs detected", response.getBody().get(0).get("message"));
    }

}
