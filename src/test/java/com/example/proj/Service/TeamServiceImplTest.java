package com.example.proj.Service;

import com.example.proj.dto.TeamDTO;
import com.example.proj.dto.TeamSearchResponse;
import com.example.proj.entity.Team;
import com.example.proj.entity.User;
import com.example.proj.exception.TeamException;
import com.example.proj.mapper.TeamMapper;
import com.example.proj.repository.TeamRepository;
import com.example.proj.repository.UserRepository;
import com.example.proj.service.TeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class TeamServiceImplTest {
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMapper teamMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    private TeamDTO teamDTO;
    private Team team;
    private List<Long> userIds;
    private User user;

    @BeforeEach
    void setUp() {
        teamDTO = new TeamDTO();
        teamDTO.setName("Team A");
        teamDTO.setDescription("Description of Team A");
        teamDTO.setTeamSize(5);

        team = new Team();
        team.setId(1L);
        team.setName("Team A");
        team.setDescription("Description of Team A");
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());
        team.setTeamSize(5);

        userIds = Arrays.asList(1L, 2L);
        user = new User();
        user.setId(1L);
    }

    @Test
    void testCreateTeam_Success() {
        TeamDTO newTeamDTO = new TeamDTO();
        newTeamDTO.setName("Test Team");
        newTeamDTO.setDescription("A test team description");
        newTeamDTO.setTeamSize(5);

        Team newTeam = new Team();
        newTeam.setId(1L);
        newTeam.setName("Test Team");
        newTeam.setDescription("A test team description");
        newTeam.setTeamSize(5);
        newTeam.setCreatedAt(LocalDateTime.now());
        newTeam.setUpdatedAt(LocalDateTime.now());

        when(teamRepository.findByName("Test Team")).thenReturn(Optional.empty());
        when(teamMapper.toEntity(newTeamDTO)).thenReturn(newTeam);
        when(teamRepository.save(any(Team.class))).thenReturn(newTeam);

        Long teamId = teamService.createTeam(newTeamDTO);

        assertNotNull(teamId);
        assertEquals(1L, teamId);
        verify(teamRepository).save(any(Team.class));
    }
    @Test
    void testCreateTeam_NameAlreadyExists() {
        when(teamRepository.findByName(teamDTO.getName())).thenReturn(Optional.of(team));

        TeamException exception = assertThrows(TeamException.class, () -> teamService.createTeam(teamDTO));
        assertEquals("Team with this name already exists.", exception.getMessage());
    }

    @Test
    void testCreateTeam_NameIsNull() {
        teamDTO.setName(null);
        TeamException exception = assertThrows(TeamException.class, () -> teamService.createTeam(teamDTO));
        assertEquals("Team name is required.", exception.getMessage());
    }
    @Test
    void testUpdateTeam_Success() {
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamMapper.toDTO(any(Team.class))).thenReturn(teamDTO);

        TeamDTO updatedTeam = teamService.updateTeam(team.getId(), teamDTO);

        assertNotNull(updatedTeam);
        assertEquals(teamDTO.getName(), updatedTeam.getName());
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void testUpdateTeam_TeamNotFound() {
        // Arrange: Pre-fetch the team ID to avoid method calls within the lambda
        Long teamId = team.getId();
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        TeamException exception = assertThrows(TeamException.class, () -> teamService.updateTeam(teamId, teamDTO));
        assertEquals("Team not found.", exception.getMessage());
    }

    @Test
    void testGetTeamById_Success() {
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        when(teamMapper.toDTO(any(Team.class))).thenReturn(teamDTO);

        TeamDTO result = teamService.getTeamById(team.getId());

        assertNotNull(result);
        assertEquals(team.getName(), result.getName());
        verify(teamRepository).findById(team.getId());
    }

    @Test
    void testGetTeamById_NotFound() {
        Long teamId = team.getId();

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        TeamException exception = assertThrows(TeamException.class, () -> teamService.getTeamById(teamId));
        assertEquals("Team not found.", exception.getMessage());
    }
    @Test
    void testAssignTeamUser_Success() {
        Long testTeamId = 1L;
        Long testUserId1 = 101L;
        Long testUserId2 = 102L;
        List<Long> testUserIds = List.of(testUserId1, testUserId2);

        Team testTeam = new Team();
        testTeam.setId(testTeamId);
        testTeam.setName("Test Team");

        User testUser1 = new User("User1", "user1@example.com");
        testUser1.setId(testUserId1);

        User testUser2 = new User("User2", "user2@example.com");
        testUser2.setId(testUserId2);
        when(teamRepository.findById(testTeamId)).thenReturn(Optional.of(testTeam));
        when(userRepository.findAllById(testUserIds)).thenReturn(List.of(testUser1, testUser2));
        List<Map<String, Object>> response = teamService.assignTeamUser(testTeamId, testUserIds);
        assertEquals(2, response.size());
        assertEquals("User successfully added", response.get(0).get("message"));
        assertEquals("User successfully added", response.get(1).get("message"));
        assertEquals("101", response.get(0).get("userId").toString());
        assertEquals("102", response.get(1).get("userId").toString());
        assertTrue(testTeam.getUsers().contains(testUser1));
        assertTrue(testTeam.getUsers().contains(testUser2));
        verify(teamRepository).save(testTeam);
    }

    @Test
    void testAssignTeamUser_SomeUsersNotFound() {
        Long teamId = team.getId();
        List<Long> userIdsList = Arrays.asList(101L, 102L);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        when(userRepository.findAllById(userIdsList)).thenReturn(Collections.singletonList(new User("User1", "user1@example.com")));
        TeamException exception = assertThrows(TeamException.class, () -> teamService.assignTeamUser(teamId, userIdsList));
        assertEquals("Some users not found.", exception.getMessage());
    }
    @Test
    void testSearchTeams_WithSearchCriteria() {
        String searchCriteria = "Test Team";  // Search for teams that contain this name
        String description = "Team for testing";  // Optional description for filtering
        Pageable pageable = PageRequest.of(0, 10);  // First page with a limit of 10

        // Create two teams to match the search criteria
        Team team1 = new Team();
        team1.setName("Test Team 1");
        team1.setDescription("Team for testing purposes");

        Team team2 = new Team();
        team2.setName("Test Team 2");
        team2.setDescription("Another team for testing");

        List<Team> teams = List.of(team1, team2);


        Page<Team> page = new PageImpl<>(teams, pageable, teams.size());


        when(teamRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase((searchCriteria), (searchCriteria), (pageable)))
                .thenReturn(page);

        TeamDTO teamDTO1 = new TeamDTO();
        teamDTO1.setName("Test Team 1");
        teamDTO1.setDescription("Team for testing purposes");

        TeamDTO teamDTO2 = new TeamDTO();
        teamDTO2.setName("Test Team 2");
        teamDTO2.setDescription("Another team for testing");

        when(teamMapper.toDTOList(teams)).thenReturn(List.of(teamDTO1, teamDTO2));


        TeamSearchResponse response = teamService.searchTeams(1, 10, searchCriteria, description);


        assertNotNull(response);
        assertEquals(2, response.getResults().size());
        assertEquals(2, response.getTotalCount());
        assertEquals(1, response.getCurrentPage());

        verify(teamRepository).findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase((searchCriteria), (searchCriteria), (pageable));
    }
    @Test
    void testIsTeamNameExists_ReturnsFalse() {
        when(teamRepository.findByName(teamDTO.getName())).thenReturn(Optional.empty());

        boolean result = teamService.isTeamNameExists(teamDTO.getName());

        assertFalse(result);
    }
    @Test
    void testCreateTeam_NameIsEmpty() {
        teamDTO.setName("");  // Set empty name

        TeamException exception = assertThrows(TeamException.class, () -> teamService.createTeam(teamDTO));
        assertEquals("Team name is required.", exception.getMessage());
    }
    @Test
    void testAssignTeamUser_TeamNotFound() {
        Long teamId = team.getId();
        List<Long> assignedUserIds = userIds;

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        TeamException exception = assertThrows(TeamException.class, () -> teamService.assignTeamUser(teamId, assignedUserIds));

        assertEquals("Team not found.", exception.getMessage());
    }
    @Test
    void testSearchTeams_NoTeamsFound() {
        String searchCriteria = "Nonexistent Team";
        Pageable pageable = PageRequest.of(0, 10);

        Page<Team> page = Page.empty(pageable);
        when(teamRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase((searchCriteria), (searchCriteria), (pageable)))
                .thenReturn(page);

        TeamSearchResponse response = teamService.searchTeams(1, 10, searchCriteria, null);

        assertNotNull(response);
        assertTrue(response.getResults().isEmpty());  // Ensure no results
        assertEquals(0, response.getTotalCount());
        assertEquals(1, response.getCurrentPage());
    }

}