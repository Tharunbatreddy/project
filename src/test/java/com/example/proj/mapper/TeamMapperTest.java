package com.example.proj.mapper;

import com.example.proj.dto.TeamDTO;
import com.example.proj.entity.Team;
import com.example.proj.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class TeamMapperTest {
    @Autowired
    private TeamMapper teamMapper; // Let Spring inject the mapper

    @Test
    void testToDTO() {
        // Create Team instance and set properties
        Team team = new Team();
        team.setId(1L);
        team.setName("Test Team");
        team.setDescription("A test team");
        team.setTeamSize(10);
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());

        User user1 = new User();
        user1.setId(101L);

        User user2 = new User();
        user2.setId(102L);

        team.setUsers(List.of(user1, user2));

        // Call toDTO
        TeamDTO teamDTO = teamMapper.toDTO(team);

        // Assertions
        assertNotNull(teamDTO);
        assertEquals(1L, teamDTO.getTeamId());
        assertEquals("Test Team", teamDTO.getName());
        assertEquals("A test team", teamDTO.getDescription());
        assertEquals(10, teamDTO.getTeamSize());
        assertEquals(2, teamDTO.getUserIds().size());
        assertEquals(101L, teamDTO.getUserIds().get(0));
    }

    @Test
    void testToEntity() {
        TeamDTO teamDTO = new TeamDTO(1L, "Test Team", "A test team", 10, LocalDateTime.now(), LocalDateTime.now(), List.of(101L, 102L));
        Team team = teamMapper.toEntity(teamDTO);
        assertNotNull(team);
        assertEquals(1L, team.getId());
        assertEquals("Test Team", team.getName());
        assertEquals("A test team", team.getDescription());
        assertEquals(10, team.getTeamSize());
    }

    @Test
    void testToDTOList() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team 1");
        team1.setDescription("First team");
        team1.setTeamSize(5);
        team1.setUsers(List.of());

        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team 2");
        team2.setDescription("Second team");
        team2.setTeamSize(10);
        team2.setUsers(List.of());

        List<Team> teams = List.of(team1, team2);
        List<TeamDTO> teamDTOs = teamMapper.toDTOList(teams);
        assertNotNull(teamDTOs);
        assertEquals(2, teamDTOs.size());
        assertEquals(1L, teamDTOs.get(0).getTeamId());
        assertEquals("Team 1", teamDTOs.get(0).getName());
        assertEquals(2L, teamDTOs.get(1).getTeamId());
        assertEquals("Team 2", teamDTOs.get(1).getName());
    }
}