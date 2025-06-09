package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
//    @GetMapping
//    public List<Club> getAllClubs() {
//        return ClubRepository.findAll();
//    }

    
	@GetMapping
	public @ResponseBody CollectionModel<ClubDTO> getAllClubs() {
	List<ClubDTO> clubsDTO = new ArrayList<>();
	for(Club club : ClubRepository.findAll())
	clubsDTO.add(new ClubDTO(club));
	return CollectionModel.of(clubsDTO);
	}
    
//    @GetMapping("/{id}")
//    public ResponseEntity<Club> getById(@PathVariable("id") Long id) {
//        Optional<Club> club = ClubRepository.findById(id);
//        return club.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }

	@GetMapping("/{id}")
	public ResponseEntity<ClubDTO> getById(@PathVariable("id") Long id){
		Optional<Club> club = ClubRepository.findById(id);
		if(club.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		ClubDTO clubDTO = new ClubDTO(club.get());
		return ResponseEntity.ok(clubDTO);
	}
    
    @PostMapping
    public Club createClub(@RequestBody Club club) {
    return ClubRepository.save(club);
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
    public ResponseEntity<Club> updateClub(@PathVariable("id") Long id, @RequestBody Club clubDetails) {
        return ClubRepository.findById(id)
            .map(club -> {
                club.setClubName(clubDetails.getClubName());
                club.setManagerName(clubDetails.getManagerName());
                club.setClubRecord(clubDetails.getClubRecord());
                club.setPlayers(clubDetails.getPlayers());
                return ResponseEntity.ok(ClubRepository.save(club));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteAllClubs() {
        ClubRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClub(@PathVariable("id") Long id) {
        return ClubRepository.findById(id)
            .map(club -> {
                ClubRepository.delete(club);
                return ResponseEntity.noContent().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
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
                .orElse(null);

        if (topClub == null) {
            return ResponseEntity.notFound().build();
        }

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
                .orElse(null);

        if (topClub == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ClubDTO(topClub));
    }

}
