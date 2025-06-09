package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/clubs")
public class ClubController {

    @Autowired
    private ClubRepository ClubRepository;
    @Autowired
    private MatchRepository MatchRepository;
    @Autowired
    private PlayerRepository PlayerRepository;

    
	@GetMapping
	public @ResponseBody CollectionModel<ClubDTO> getAllClubs() {
	List<ClubDTO> clubsDTO = new ArrayList<>();
	for(Club club : ClubRepository.findAll())
	clubsDTO.add(new ClubDTO(club));
	return CollectionModel.of(clubsDTO);
	}
    
	@GetMapping("/{id}")
	public ResponseEntity<ClubDTO> getById(@PathVariable("id") Long id){
	    Club club = ClubRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));

	    return ResponseEntity.ok(new ClubDTO(club));
	}
	
//    @PostMapping
//    public ResponseEntity<?> createClub(@Valid @RequestBody Club club, BindingResult result) {
//        if (result.hasErrors()) {
//            List<String> errors = result.getAllErrors().stream()
//                    .map(ObjectError::getDefaultMessage)
//                    .toList();
//            return ResponseEntity.badRequest().body(errors);
//        }
//
//        Club saved = ClubRepository.save(club);
//        return ResponseEntity.status(HttpStatus.CREATED).body(new ClubDTO(saved));
//    }
	
	@PostMapping
	public ResponseEntity<?> createClub(@Valid @RequestBody Club club, BindingResult result) {
		List<String> errors = new ArrayList<>();

		if (result.hasErrors()) {
		    errors.addAll(result.getAllErrors().stream()
		            .map(ObjectError::getDefaultMessage)
		            .toList());
		}

		List<Player> processedPlayers = new ArrayList<>();
		List<Player> players = club.getPlayers();

		if (players != null) {
		    for (Player p : players) {
		        if (p.getId() != null) {
		            Optional<Player> existingOpt = PlayerRepository.findById(p.getId());
		            if (existingOpt.isPresent()) {
		                Player existing = existingOpt.get();
		                existing.setClub(club);
		                processedPlayers.add(existing);
		            } else {
		                errors.add("Player with id " + p.getId() + " does not exist");
		            }
		        } else {
		            if (p.getFirstName() == null) {
		                errors.add("First name is required for new players");
		            }
		            if (p.getLastName() == null) {
		                errors.add("Last name is required for new players");
		            }

		            if (errors.isEmpty()) {
		                p.setClub(club);
		                processedPlayers.add(p);
		            }
		        }
		    }
		}

		if (!errors.isEmpty()) {
		    return ResponseEntity.badRequest().body(errors);
		}

		club.setPlayers(processedPlayers);
		Club saved = ClubRepository.save(club);

		return ResponseEntity.status(HttpStatus.CREATED).body(new ClubDTO(saved));
	}
	
    
    @PostMapping("/calculateResults")
    public ResponseEntity<?> calculateMatchResultsAndUpdateClubs() {
        List<Match> matches = MatchRepository.findAll();
        List<Club> allClubs = ClubRepository.findAll();

        for (Club club : allClubs) {
            club.setWins(0);
            club.setDraws(0);
            club.setLoses(0);
        }

        for (Match match : matches) {
            Club clubA = match.getClubA();
            Club clubB = match.getClubB();
            int goalsA = match.getGoalsClubA();
            int goalsB = match.getGoalsClubB();

            if (goalsA > goalsB) {
                clubA.setWins(clubA.getWins() + 1);
                clubB.setLoses(clubB.getLoses() + 1);
            } else if (goalsB > goalsA) {
                clubB.setWins(clubB.getWins() + 1);
                clubA.setLoses(clubA.getLoses() + 1);
            } else {
                clubA.setDraws(clubA.getDraws() + 1);
                clubB.setDraws(clubB.getDraws() + 1);
            }
        }

        ClubRepository.saveAll(allClubs);

        return ResponseEntity.ok("Club records updated based on match results.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClub(@PathVariable("id") Long id, @RequestBody Club clubDetails, BindingResult result) {
        Club club = ClubRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }
        
        club.setClubName(clubDetails.getClubName());
        club.setManagerName(clubDetails.getManagerName());
        club.setPlayers(clubDetails.getPlayers());
        club.setWins(clubDetails.getWins());
        club.setDraws(clubDetails.getDraws());
        club.setLoses(clubDetails.getLoses());

        Club newClub = ClubRepository.save(club);
        
        return ResponseEntity.ok(new ClubDTO(newClub));
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteAllClubs() {
        ClubRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClub(@PathVariable("id") Long id) {
        Club club = ClubRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));

        ClubRepository.delete(club);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/topScoringClub")
    public ResponseEntity<?> getTopScoringClub() {
        List<Match> matches = MatchRepository.findAll();
        Map<Club, Integer> goalsScored = new HashMap<>();

        for (Match match : matches) {
            Club clubA = match.getClubA();
            Club clubB = match.getClubB();

            goalsScored.put(clubA, goalsScored.getOrDefault(clubA, 0) + match.getGoalsClubA());
            goalsScored.put(clubB, goalsScored.getOrDefault(clubB, 0) + match.getGoalsClubB());
        }

        Club topClub = goalsScored.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new ResourceNotFoundException("No top scoring club found."));

        return ResponseEntity.ok(new ClubDTO(topClub));
    }

    @GetMapping("/topConcedingClub")
    public ResponseEntity<?> getTopConcedingClub() {
        List<Match> matches = MatchRepository.findAll();
        Map<Club, Integer> goalsConceded = new HashMap<>();

        for (Match match : matches) {
            Club clubA = match.getClubA();
            Club clubB = match.getClubB();

            goalsConceded.put(clubA, goalsConceded.getOrDefault(clubA, 0) + match.getGoalsClubB());
            goalsConceded.put(clubB, goalsConceded.getOrDefault(clubB, 0) + match.getGoalsClubA());
        }

        Club topClub = goalsConceded.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new ResourceNotFoundException("No top conceding club found."));

        return ResponseEntity.ok(new ClubDTO(topClub));
    }
}
