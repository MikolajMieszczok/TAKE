package com.example.demo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/goals")
public class GoalController {
	@Autowired
    private GoalRepository GoalRepository;
	@Autowired
	private PlayerRepository playerRepository;
	@Autowired
	private MatchRepository matchRepository;
	
//	@GetMapping
//    public List<Goal> getAllGoals() {
//        return GoalRepository.findAll();
//    }

	@GetMapping
	public @ResponseBody CollectionModel<GoalDTO> getCars() {
	List<GoalDTO> goalsDTO = new ArrayList<>();
	for(Goal goal:GoalRepository.findAll())
	goalsDTO.add(new GoalDTO(goal));
	return CollectionModel.of(goalsDTO);
	}
	
//    @GetMapping("/{id}")
//    public ResponseEntity<Goal> getById(@PathVariable("id") Long id) {
//        Optional<Goal> goal = GoalRepository.findById(id);
//        return goal.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }
	
	
	@GetMapping("/{id}")
	public ResponseEntity<GoalDTO> getById(@PathVariable("id") Long id){
		Optional<Goal> goal = GoalRepository.findById(id);
		
		if(goal.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		GoalDTO goalDTO = new GoalDTO(goal.get());
		
		return ResponseEntity.ok(goalDTO);
		
	}
	
	
	@PostMapping
	public ResponseEntity<?> createGoal(@RequestBody Goal goal) {
	    Optional<Player> playerOpt = playerRepository.findById(goal.getPlayer().getId());
	    Optional<Match> matchOpt = matchRepository.findById(goal.getMatch().getId());

	    if (playerOpt.isEmpty() || matchOpt.isEmpty()) {
	        return ResponseEntity.badRequest().body("Invalid player or match ID");
	    }

	    Player player = playerOpt.get();
	    Match match = matchOpt.get();
	    
	    Club playerClub = player.getClub();
	    Club clubA = match.getClubA();
	    Club clubB = match.getClubB();

	    boolean isOwnGoal = goal.getOwnGoal();

	    // W przypadku gola samobójczego inkrementujemy gole drużyny przeciwnej, widzę Cię Maguire
	    if (playerClub.getId().equals(clubA.getId())) {
	        if (isOwnGoal) {
	            match.setGoalsClubB(match.getGoalsClubB() + 1);
	        } else {
	            match.setGoalsClubA(match.getGoalsClubA() + 1);
	        }
	    } else if (playerClub.getId().equals(clubB.getId())) {
	        if (isOwnGoal) {
	            match.setGoalsClubA(match.getGoalsClubA() + 1);
	        } else {
	            match.setGoalsClubB(match.getGoalsClubB() + 1);
	        }
	    } else {
	        return ResponseEntity.badRequest().body("Player does not belong to either club in the match");
	    }

	    matchRepository.save(match);
	    Goal savedGoal = GoalRepository.save(goal);
	    return ResponseEntity.ok(savedGoal);
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteGoal(@PathVariable("id") Long id) {
	    Optional<Goal> goalOpt = GoalRepository.findById(id);

	    if (goalOpt.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }

	    Goal goal = goalOpt.get();
	    Optional<Player> playerOpt = playerRepository.findById(goal.getPlayer().getId());
	    Optional<Match> matchOpt = matchRepository.findById(goal.getMatch().getId());

	    if (playerOpt.isEmpty() || matchOpt.isEmpty()) {
	        return ResponseEntity.badRequest().body("Invalid player or match ID");
	    }

	    Player player = playerOpt.get();
	    Match match = matchOpt.get();
	    Club playerClub = player.getClub();
	    Club clubA = match.getClubA();
	    Club clubB = match.getClubB();
	    boolean isOwnGoal = goal.getOwnGoal();

	    // Analogiczna logika, tylko dekrementujemy
	    if (playerClub.getId().equals(clubA.getId())) {
	        if (isOwnGoal) {
	            match.setGoalsClubB(Math.max(0, match.getGoalsClubB() - 1));
	        } else {
	            match.setGoalsClubA(Math.max(0, match.getGoalsClubA() - 1));
	        }
	    } else if (playerClub.getId().equals(clubB.getId())) {
	        if (isOwnGoal) {
	            match.setGoalsClubA(Math.max(0, match.getGoalsClubA() - 1));
	        } else {
	            match.setGoalsClubB(Math.max(0, match.getGoalsClubB() - 1));
	        }
	    } else {
	        return ResponseEntity.badRequest().body("Player does not belong to either club in the match");
	    }

	    matchRepository.save(match);
	    GoalRepository.deleteById(id);
	    return ResponseEntity.noContent().build();
	}
}
