package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import com.example.demo.MatchRepository;

@RestController
@RequestMapping("/matches")
public class MatchController {
	
	@Autowired
	private MatchRepository MatchRepository;
	
	@GetMapping
    public List<Match> getAllMatchess() {
        return MatchRepository.findAll();
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<Match> getById(@PathVariable("id") Long id) {
        Optional<Match> matches = MatchRepository.findById(id);
        return matches.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
	
    @PostMapping
    public Match createClub(@RequestBody Match match) {
    	return MatchRepository.save(match);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Match> updateClub(@PathVariable("id") Long id, @RequestBody Match matchDetails) {
        return MatchRepository.findById(id)
            .map(match -> {
                match.setGoalsClubA(match.getGoalsClubA());
                match.setGoalsClubB(match.getGoalsClubB());
                match.setDateOfMatches(match.getDateOfMatches());
                match.setGameweek(match.getGameweek());
                match.setClubA(match.getClubA());
                match.setClubB(match.getClubB());
                return ResponseEntity.ok(MatchRepository.save(match));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteAllMatches() {
        MatchRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClub(@PathVariable("id") Long id) {
        return MatchRepository.findById(id)
            .map(club -> {
                MatchRepository.delete(club);
                return ResponseEntity.noContent().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
