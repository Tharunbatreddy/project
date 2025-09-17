package com.example.proj.service;

import com.example.proj.dto.TeamDTO;
import com.example.proj.dto.TeamSearchResponse;
import com.example.proj.entity.Team;
import com.example.proj.entity.User;
import com.example.proj.exception.TeamException;
import com.example.proj.mapper.TeamMapper;
import com.example.proj.repository.TeamRepository;
import com.example.proj.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;



import java.util.*;
import java.time.LocalDateTime;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;  // Inject the MapStruct mapper
    private final UserRepository userRepository;


    private static final String TEAM_NOT_FOUND_MESSAGE = "Team not found.";




    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, TeamMapper teamMapper,
                           UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;  // Use MapStruct mapper here
        this.userRepository = userRepository;
    }

    @Override
    public Long createTeam(TeamDTO teamDTO) {
        if (teamDTO.getName() == null || teamDTO.getName().isEmpty()) {
            throw new TeamException("Team name is required.", HttpStatus.BAD_REQUEST);
        }

        if (teamRepository.findByName(teamDTO.getName()).isPresent()) {
            throw new TeamException("Team with this name already exists.", HttpStatus.CONFLICT);
        }

        Team team = teamMapper.toEntity(teamDTO);  // Map DTO to Entity using MapStruct

        LocalDateTime currentTime = LocalDateTime.now();
        team.setCreatedAt(currentTime);
        team.setUpdatedAt(currentTime);
        team.setTeamSize(teamDTO.getTeamSize() != null ? teamDTO.getTeamSize() : 0);

        team = teamRepository.save(team);

        return team.getId();
    }

    @Override
    public TeamDTO updateTeam(Long teamId, TeamDTO teamDTO) {
        Team existingTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TEAM_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND));
        // Check for conflict: If another team exists with the same name but a different ID
        if (teamDTO.getName() != null && !teamDTO.getName().isEmpty()) {
            boolean isConflict = teamRepository.existsByNameAndIdNot(teamDTO.getName(), teamId);
            if (isConflict) {
                throw new TeamException("Conflict: Team with the same name already exists.", HttpStatus.CONFLICT);
            }
            existingTeam.setName(teamDTO.getName());
        }

        // Update team entity using DTO values
        if (teamDTO.getName() != null && !teamDTO.getName().isEmpty()) {
            existingTeam.setName(teamDTO.getName());
        }
        if (teamDTO.getDescription() != null && !teamDTO.getDescription().isEmpty()) {
            existingTeam.setDescription(teamDTO.getDescription());
        }
        if (teamDTO.getTeamSize() != null) {
            existingTeam.setTeamSize(teamDTO.getTeamSize());
        }

        existingTeam.setUpdatedAt(LocalDateTime.now());
        existingTeam = teamRepository.save(existingTeam);

        return teamMapper.toDTO(existingTeam);  // Map Entity to DTO using MapStruct
    }

    @Override
    public TeamDTO getTeamById(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TEAM_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND));

        Hibernate.initialize(team.getUsers());

        return teamMapper.toDTO(team);  // Map Entity to DTO using MapStruct
    }

    @Override
    public List<Map<String, Object>> assignTeamUser(Long teamId, List<Long> userIds) {
        // 1. Validate that there are no duplicate user IDs in the request
        Set<Long> uniqueUserIds = new HashSet<>(userIds);
        if (uniqueUserIds.size() < userIds.size()) {
            throw new TeamException("Duplicate user IDs detected", HttpStatus.BAD_REQUEST);
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TEAM_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND));

        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new TeamException("Some users not found.", HttpStatus.NOT_FOUND);
        }

        List<User> existingUsers = team.getUsers();
        for (User user : users) {
            if (existingUsers.contains(user)) {
                throw new TeamException("User already assigned to the team", HttpStatus.BAD_REQUEST);
            }
        }

        // Add the users to the team's user list
        team.getUsers().addAll(users);
        teamRepository.save(team);

        return userIds.stream()
                .map(userId -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", "201");
                    response.put("message", "User successfully added");
                    response.put("userId", userId);
                    return response;
                })
                .toList();
    }

    @Override
    public TeamSearchResponse searchTeams(int page, int limit, String search, String description) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Team> teamPage;

        if (search != null && !search.isEmpty()) {
            teamPage = teamRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable);
        } else if (description != null && !description.isEmpty()) {
            teamPage = teamRepository.findByDescriptionContaining(description, pageable);
        } else {
            teamPage = teamRepository.findAll(pageable);
        }

        TeamSearchResponse response = new TeamSearchResponse();
        response.setResults(teamMapper.toDTOList(teamPage.getContent()));  // Map List of Entities to List of DTOs using MapStruct
        response.setTotalCount(teamPage.getTotalElements());
        response.setPageCount(teamPage.getTotalPages());
        response.setCurrentPage(teamPage.getNumber() + 1);

        return response;
    }

    @Override
    public boolean isTeamNameExists(String name) {
        return teamRepository.findByName(name).isPresent();
    }
}