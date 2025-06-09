package com.example.demo;

import com.example.demo.ClubDTO;
import com.example.demo.Club;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class})
public interface ClubMapper {
    ClubDTO toDTO(Club club);
    Club toEntity(ClubDTO dto);
}
