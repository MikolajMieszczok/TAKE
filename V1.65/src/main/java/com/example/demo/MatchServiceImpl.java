package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    @Autowired private MatchRepository matchRepository;
    @Autowired private MatchMapper matchMapper;
    @Autowired private ClubRepository clubRepository;

    @Override
    public List<MatchDTO> getAllMatches() {
        return matchRepository.findAll().stream().map(matchMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public MatchDTO getMatchById(Long id) {
        return matchMapper.toDTO(matchRepository.findById(id).orElseThrow());
    }

    @Override
    public MatchDTO createMatch(MatchDTO dto) {
        Match match = matchMapper.toEntity(dto);
        match.setClubA(clubRepository.findById(dto.getClubAId()).orElseThrow());
        match.setClubB(clubRepository.findById(dto.getClubBId()).orElseThrow());
        return matchMapper.toDTO(matchRepository.save(match));
    }

    @Override
    public MatchDTO updateMatch(Long id, MatchDTO dto) {
        Match match = matchRepository.findById(id).orElseThrow();
        match.setGoalsClubA(dto.getGoalsClubA());
        match.setGoalsClubB(dto.getGoalsClubB());
        match.setGameweek(dto.getGameweek());
        match.setDateOfMatches(dto.getDateOfMatches());
        match.setClubA(clubRepository.findById(dto.getClubAId()).orElseThrow());
        match.setClubB(clubRepository.findById(dto.getClubBId()).orElseThrow());
        return matchMapper.toDTO(matchRepository.save(match));
    }

    @Override
    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }
}
