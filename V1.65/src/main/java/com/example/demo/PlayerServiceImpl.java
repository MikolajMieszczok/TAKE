package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired private PlayerRepository playerRepository;
    @Autowired private PlayerMapper playerMapper;
    @Autowired private ClubRepository clubRepository;

    @Override
    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(playerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlayerDTO getPlayerById(Long id) {
        return playerMapper.toDTO(playerRepository.findById(id).orElseThrow());
    }

    @Override
    public PlayerDTO createPlayer(PlayerDTO dto) {
        Player player = playerMapper.toEntity(dto);
        Club club = clubRepository.findById(dto.getClubId()).orElseThrow();
        player.setClub(club);
        return playerMapper.toDTO(playerRepository.save(player));
    }

    @Override
    public PlayerDTO updatePlayer(Long id, PlayerDTO dto) {
        Player existing = playerRepository.findById(id).orElseThrow();
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setAssists(dto.getAssists());
        existing.setShirtNr(dto.getShirtNr());
        if (dto.getClubId() != null) {
            Club club = clubRepository.findById(dto.getClubId()).orElseThrow();
            existing.setClub(club);
        }
        return playerMapper.toDTO(playerRepository.save(existing));
    }

    @Override
    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }
}
