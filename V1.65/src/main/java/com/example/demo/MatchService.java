package com.example.demo;

import java.util.List;

public interface MatchService {
    List<MatchDTO> getAllMatches();
    MatchDTO getMatchById(Long id);
    MatchDTO createMatch(MatchDTO dto);
    MatchDTO updateMatch(Long id, MatchDTO dto);
    void deleteMatch(Long id);
}
