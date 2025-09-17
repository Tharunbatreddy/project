package com.example.proj.service;

import com.example.proj.dto.TeamDTO;
import com.example.proj.dto.TeamSearchResponse;

import java.util.List;
import java.util.Map;

public interface TeamService {
    Long createTeam(TeamDTO teamDTO);
    TeamDTO updateTeam(Long teamId, TeamDTO teamDTO);
    TeamDTO getTeamById(Long teamId);
    List<Map<String, Object>> assignTeamUser(Long teamId, List<Long> userIds);

    // Update searchTeams method signature to return TeamSearchResponse instead of Map<String, Object>
    TeamSearchResponse searchTeams(int page, int limit, String search, String description);
    boolean isTeamNameExists(String name);
}

