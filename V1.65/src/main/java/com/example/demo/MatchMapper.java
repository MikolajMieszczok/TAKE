package com.example.demo;


import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    @Mapping(source = "clubA.id", target = "clubAId")
    @Mapping(source = "clubB.id", target = "clubBId")
    MatchDTO toDTO(Match match);

    @Mapping(source = "clubAId", target = "clubA.id")
    @Mapping(source = "clubBId", target = "clubB.id")
    Match toEntity(MatchDTO dto);
}
