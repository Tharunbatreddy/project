package com.example.proj.mapper;
import com.example.proj.dto.TeamDTO;
import com.example.proj.entity.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    @Mapping(target = "teamId", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "teamSize", source = "teamSize")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "userIds", expression = "java(team.getUsers().stream().map(user -> user.getId()).toList())")
    TeamDTO toDTO(Team team);

    List<TeamDTO> toDTOList(List<Team> teams);

    // Reverse mapping (from DTO to Entity)
    @Mapping(target = "id", source = "teamId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "teamSize", source = "teamSize")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "users", ignore = true) // Users are managed separately
    Team toEntity(TeamDTO teamDTO);
}