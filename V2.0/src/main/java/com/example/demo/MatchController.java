package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    public ResponseEntity<?> createMatch(@Valid @RequestBody Match match, BindingResult result) {
    	
    	if(result.hasErrors()){
    		List<String> errors = result.getAllErrors().stream()
    	            .map(ObjectError::getDefaultMessage)
    	            .toList();
    	        return ResponseEntity.badRequest().body(errors);
    	}
    	
    	Match saved = matchRepository.save(match);
    	
        return ResponseEntity.status(HttpStatus.CREATED).body(new MatchDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatch(@PathVariable("id") Long id, @Valid @RequestBody Match matchDetails, BindingResult result) {
        
    	if(result.hasErrors()){
    		List<String> errors = result.getAllErrors().stream()
    	            .map(ObjectError::getDefaultMessage)
    	            .toList();
    	        return ResponseEntity.badRequest().body(errors);
    	}
    	
    	 Match match = matchRepository.findById(id)
    	            .orElse(null);

    	    if (match == null) {
    	        return ResponseEntity.notFound().build();
    	    }

    	    if (!match.getClubA().getId().equals(matchDetails.getClubA().getId()) ||
    	        !match.getClubB().getId().equals(matchDetails.getClubB().getId())) {
    	        return ResponseEntity.badRequest().body("Matches cannot be changed.");
    	    }

    	    match.setGoalsClubA(matchDetails.getGoalsClubA());
    	    match.setGoalsClubB(matchDetails.getGoalsClubB());
    	    match.setDateOfMatches(matchDetails.getDateOfMatches());

    	    Match newMatch = matchRepository.save(match);
    	    
    	    return ResponseEntity.ok(new MatchDTO(newMatch));
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
