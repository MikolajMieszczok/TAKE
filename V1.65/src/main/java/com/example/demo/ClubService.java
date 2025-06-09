package com.example.demo;

import com.example.demo.ClubDTO;
import java.util.List;

public interface ClubService {
    List<ClubDTO> getAllClubs();
    ClubDTO getClubById(Long id);
    ClubDTO createClub(ClubDTO dto);
    ClubDTO updateClub(Long id, ClubDTO dto);
    void deleteClub(Long id);
}
