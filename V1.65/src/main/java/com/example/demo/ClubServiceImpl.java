package com.example.demo;

import com.example.demo.ClubDTO;
import com.example.demo.ClubMapper;
import com.example.demo.Club;
import com.example.demo.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubServiceImpl implements ClubService {

    @Autowired private ClubRepository clubRepository;
    @Autowired private ClubMapper clubMapper;

    @Override
    public List<ClubDTO> getAllClubs() {
        return clubRepository.findAll()
                .stream()
                .map(clubMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClubDTO getClubById(Long id) {
        return clubMapper.toDTO(clubRepository.findById(id).orElseThrow());
    }

    @Override
    public ClubDTO createClub(ClubDTO dto) {
        return clubMapper.toDTO(clubRepository.save(clubMapper.toEntity(dto)));
    }

    @Override
    public ClubDTO updateClub(Long id, ClubDTO dto) {
        Club club = clubRepository.findById(id).orElseThrow();
        club.setClubName(dto.getClubName());
        club.setManagerName(dto.getManagerName());
        club.setClubRecord(dto.getClubRecord());
        return clubMapper.toDTO(clubRepository.save(club));
    }

    @Override
    public void deleteClub(Long id) {
        clubRepository.deleteById(id);
    }
}
