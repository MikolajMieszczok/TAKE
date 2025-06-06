package com.example.demo;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(source = "player.id", target = "playerId")
    @Mapping(source = "match.id", target = "matchId")
    GoalDTO toDTO(Goal goal);

    @Mapping(source = "playerId", target = "player.id")
    @Mapping(source = "matchId", target = "match.id")
    Goal toEntity(GoalDTO dto);
}
