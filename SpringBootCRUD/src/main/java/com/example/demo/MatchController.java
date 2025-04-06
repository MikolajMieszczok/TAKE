package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Match> getById(@PathVariable Long id) {
        Optional<Match> matches = MatchRepository.findById(id);
        return matches.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
	
    @PostMapping
    public Match createClub(@RequestBody Match match) {
    	return MatchRepository.save(match);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Match> updateClub(@PathVariable Long id, @RequestBody Match matchDetails) {
        return MatchRepository.findById(id)
            .map(match -> {
                match.setGoalsClubA(match.getGoalsClubA());
                match.setGoalsClubB(match.getGoalsClubB());
                match.setDateOfMatches(match.getDateOfMatches());
                match.setGameweek(match.getGameweek());
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
    public ResponseEntity<Object> deleteClub(@PathVariable Long id) {
        return MatchRepository.findById(id)
            .map(club -> {
                MatchRepository.delete(club);
                return ResponseEntity.noContent().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
	
}
