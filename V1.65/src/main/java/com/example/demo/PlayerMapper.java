package com.example.demo;

import com.example.demo.PlayerDTO;
import com.example.demo.Player;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    @Mapping(source = "club.id", target = "clubId")
    PlayerDTO toDTO(Player player);

    @Mapping(source = "clubId", target = "club.id")
    Player toEntity(PlayerDTO dto);
}