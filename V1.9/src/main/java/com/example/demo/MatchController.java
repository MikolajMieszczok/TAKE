package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matches")
public class MatchController {

    @Autowired
    private MatchRepository matchRepository;

    @GetMapping
    public @ResponseBody CollectionModel<MatchDTO> getAllMatches() {
        List<MatchDTO> matchesDTO = matchRepository.findAll()
                .stream()
                .map(MatchDTO::new)
                .toList();
        return CollectionModel.of(matchesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getById(@PathVariable("id") Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        return ResponseEntity.ok(new MatchDTO(match));
    }

    @PostMapping
    public Match createMatch(@RequestBody Match match) {
        return matchRepository.save(match);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> updateMatch(@PathVariable("id") Long id, @RequestBody Match matchDetails) {
        return matchRepository.findById(id)
                .map(match -> {
                    match.setGoalsClubA(matchDetails.getGoalsClubA());
                    match.setGoalsClubB(matchDetails.getGoalsClubB());
                    match.setDateOfMatches(matchDetails.getDateOfMatches());
                    match.setGameweek(matchDetails.getGameweek());
                    match.setClubA(matchDetails.getClubA());
                    match.setClubB(matchDetails.getClubB());
                    return ResponseEntity.ok(matchRepository.save(match));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllMatches() {
        matchRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMatch(@PathVariable("id") Long id) {
        return matchRepository.findById(id)
                .map(match -> {
                    matchRepository.delete(match);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
