package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/goals")
@Tag(name = "Goals", description = "Endpoints for managing goals scored in matches")
public class GoalController {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @GetMapping
    @Operation(summary = "Get all goals", description = "Returns a list of all goals")
    public @ResponseBody CollectionModel<GoalDTO> getGoals() {
        List<GoalDTO> goalsDTO = goalRepository.findAll()
                .stream()
                .map(GoalDTO::new)
                .toList();
        return CollectionModel.of(goalsDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID", description = "Returns details of a goal by its ID")
    public ResponseEntity<GoalDTO> getById(@PathVariable("id") Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));
        return ResponseEntity.ok(new GoalDTO(goal));
    }

    @PostMapping
    @Operation(summary = "Create a new goal", description = "Adds a new goal and updates the match score accordingly")
    public ResponseEntity<?> createGoal(@Valid @RequestBody Goal goal, BindingResult result) {
        if(result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }

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
        return ResponseEntity.ok(new GoalDTO(savedGoal));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal details", description = "Updates the minute of the goal (player and match cannot be changed)")
    public ResponseEntity<?> updateGoal(@PathVariable("id") Long id, @Valid @RequestBody Goal goalDetails, BindingResult result) {
        Goal existingGoal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }

        if (goalDetails.getPlayer() == null || goalDetails.getMatch() == null ||
            !existingGoal.getPlayer().getId().equals(goalDetails.getPlayer().getId()) ||
            !existingGoal.getMatch().getId().equals(goalDetails.getMatch().getId())) {
            return ResponseEntity.badRequest().body("Changing player or match of a goal is not allowed");
        }

        existingGoal.setMinute(goalDetails.getMinute());
        Goal updatedGoal = goalRepository.save(existingGoal);
        return ResponseEntity.ok(new GoalDTO(updatedGoal));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a goal", description = "Deletes a goal and updates the match score accordingly")
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
