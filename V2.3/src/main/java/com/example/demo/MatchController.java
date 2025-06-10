package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/matches")
public class MatchController {

	
    private GoalRepository goalRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ClubRepository clubRepository;
    

    MatchController(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }
       
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
    	
    	if(match.getClubA().getId() == match.getClubB().getId()) {
    		return ResponseEntity.badRequest().body("Match cannot be played between same team");
    	}
    	
    	List<String> nonExistingClubs = new ArrayList<>();
    	
    	Optional<Club> clubAOpt = clubRepository.findById(match.getClubA().getId());
    	if (clubAOpt.isEmpty()) {
    		nonExistingClubs.add("Club A with id " + match.getClubA().getId() + " does not exist");
    	}
    	
    	Optional<Club> clubBOpt = clubRepository.findById(match.getClubB().getId());
    	if (clubBOpt.isEmpty()) {
    		nonExistingClubs.add("Club B with id " + match.getClubB().getId() + " does not exist");
    	}
    	
    	if(!nonExistingClubs.isEmpty()) {
    		return ResponseEntity.badRequest().body(nonExistingClubs);
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
    	        return ResponseEntity.badRequest().body("Clubs cannot be changed.");
    	    }

    	    match.setGoalsClubA(matchDetails.getGoalsClubA());
    	    match.setGoalsClubB(matchDetails.getGoalsClubB());
    	    match.setDateOfMatches(matchDetails.getDateOfMatches());

    	    Match newMatch = matchRepository.save(match);
    	    
    	    return ResponseEntity.ok(new MatchDTO(newMatch));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllMatches(@RequestParam(name="unsafe", required = false) String unsafe) {
        if ("true".equalsIgnoreCase(unsafe)) {
        	goalRepository.deleteAll();
            matchRepository.deleteAll();
            return ResponseEntity.noContent().build();
        } else {
        	
        	try {
        		matchRepository.deleteAll();
        	}catch(Exception ex) {
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are some references in goals, use unsafe=true or delete goals manually.");
        	}
        
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable("id") Long id, @RequestParam(name="unsafe", required = false) String unsafe) {
    	Match match = matchRepository.findById(id)
    			.orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
         
    	if ("true".equalsIgnoreCase(unsafe)) {
        	
    		List<Long> goalsToDelete = new ArrayList<>();
    		
    		for(Goal goal : goalRepository.findAll()) {
    			if(goal.getMatch().getId() == id) {
    				goalsToDelete.add(goal.getId());
    			}
    		}
    		
    		goalRepository.deleteAllById(goalsToDelete);
            matchRepository.deleteById(id);
            
            return ResponseEntity.noContent().build();
        }
    	
    	try {
    		matchRepository.delete(match);
    	} catch (Exception ex){
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are some references in goals, use unsafe=true or delete goals manually.");
    	}
        return ResponseEntity.noContent().build();
    }
}
