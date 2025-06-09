package com.example.demo;

import com.example.demo.PlayerDTO;
import java.util.List;

public interface PlayerService {
    List<PlayerDTO> getAllPlayers();
    PlayerDTO getPlayerById(Long id);
    PlayerDTO createPlayer(PlayerDTO dto);
    PlayerDTO updatePlayer(Long id, PlayerDTO dto);
    void deletePlayer(Long id);
}
