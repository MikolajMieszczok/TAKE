package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goals")
public class GoalController {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @GetMapping
    public @ResponseBody CollectionModel<GoalDTO> getGoals() {
        List<GoalDTO> goalsDTO = goalRepository.findAll()
                .stream()
                .map(GoalDTO::new)
                .toList();
        return CollectionModel.of(goalsDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalDTO> getById(@PathVariable("id") Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));
        return ResponseEntity.ok(new GoalDTO(goal));
    }

    @PostMapping
    public ResponseEntity<?> createGoal(@RequestBody Goal goal) {
        Player player = playerRepository.findById(goal.getPlayer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + goal.getPlayer().getId()));
        Match match = matchRepository.findById(goal.getMatch().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + goal.getMatch().getId()));

        Club playerClub = player.getClub();
        Club clubA = match.getClubA();
        Club clubB = match.getClubB();
        boolean isOwnGoal = goal.getOwnGoal();

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
            return ResponseEntity.badRequest().body("Player does not belong to either club in the match.");
        }

        matchRepository.save(match);
        Goal savedGoal = goalRepository.save(goal);
        return ResponseEntity.ok(savedGoal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable("id") Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));
        
        Player player = playerRepository.findById(goal.getPlayer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + goal.getPlayer().getId()));
        Match match = matchRepository.findById(goal.getMatch().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + goal.getMatch().getId()));

        Club playerClub = player.getClub();
        Club clubA = match.getClubA();
        Club clubB = match.getClubB();
        boolean isOwnGoal = goal.getOwnGoal();

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
            return ResponseEntity.badRequest().body("Player does not belong to either club in the match.");
        }

        matchRepository.save(match);
        goalRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
