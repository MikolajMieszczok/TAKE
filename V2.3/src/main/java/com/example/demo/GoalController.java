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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/goals")
@Tag(name = "Goal Management", description = "Endpoints for managing match goals")
public class GoalController {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Operation(summary = "Get all goals", description = "Retrieves a list of all goals scored in matches")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of goals",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = GoalDTO.class)))
    @GetMapping
    public @ResponseBody CollectionModel<GoalDTO> getGoals() {
        List<GoalDTO> goalsDTO = goalRepository.findAll()
                .stream()
                .map(GoalDTO::new)
                .toList();
        return CollectionModel.of(goalsDTO);
    }

    @Operation(summary = "Get goal by ID", description = "Retrieves a specific goal by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved goal",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = GoalDTO.class))),
        @ApiResponse(responseCode = "404", description = "Goal not found",
                content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<GoalDTO> getById(
            @Parameter(description = "ID of the goal to be retrieved", required = true)
            @PathVariable("id") Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));
        return ResponseEntity.ok(new GoalDTO(goal));
    }

    @Operation(summary = "Create a new goal", 
            description = "Creates a new goal record and updates the corresponding match score")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goal created successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = GoalDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or player not in match",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Player or match not found",
                content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> createGoal(
            @Parameter(description = "Goal object to be created", required = true,
                    schema = @Schema(implementation = Goal.class))
            @Valid @RequestBody Goal goal, BindingResult result) {
        
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
    
    @Operation(summary = "Update goal details", 
            description = "Updates the minute and ownGoal flag of a goal. Player and match cannot be changed.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goal updated successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = GoalDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or attempt to change player/match",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Goal not found",
                content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(
            @Parameter(description = "ID of the goal to be updated", required = true)
            @PathVariable("id") Long id, 
            @Parameter(description = "Updated goal details (only minute and ownGoal can be changed)", required = true,
                    schema = @Schema(implementation = Goal.class))
            @Valid @RequestBody Goal goalDetails, BindingResult result) {
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
        Player player = existingGoal.getPlayer();
        Match match = existingGoal.getMatch();
        Club playerClub = player.getClub();
        Club clubA = match.getClubA();
        Club clubB = match.getClubB();

        boolean oldOwnGoal = existingGoal.getOwnGoal();
        boolean newOwnGoal = goalDetails.getOwnGoal();

        if (oldOwnGoal != newOwnGoal) {
            if (playerClub.getId().equals(clubA.getId())) {
                if (oldOwnGoal) {
                    match.setGoalsClubB(Math.max(0, match.getGoalsClubB() - 1));
                    match.setGoalsClubA(match.getGoalsClubA() + 1);
                } else {
                    match.setGoalsClubA(Math.max(0, match.getGoalsClubA() - 1));
                    match.setGoalsClubB(match.getGoalsClubB() + 1);
                }
            } else if (playerClub.getId().equals(clubB.getId())) {
                if (oldOwnGoal) {
                    match.setGoalsClubA(Math.max(0, match.getGoalsClubA() - 1));
                    match.setGoalsClubB(match.getGoalsClubB() + 1); 
                } else {
                    match.setGoalsClubB(Math.max(0, match.getGoalsClubB() - 1));
                    match.setGoalsClubA(match.getGoalsClubA() + 1); 
                }
            } else {
                return ResponseEntity.badRequest().body("Player does not belong to either club in the match.");
            }
        }
        existingGoal.setMinute(goalDetails.getMinute());
        existingGoal.setOwnGoal(newOwnGoal);
        matchRepository.save(match);
        Goal updatedGoal = goalRepository.save(existingGoal);
        return ResponseEntity.ok(new GoalDTO(updatedGoal));
    }
    

    @Operation(summary = "Delete goal", 
            description = "Deletes a goal and updates the corresponding match score")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Goal deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Player does not belong to match clubs",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Goal, player or match not found",
                content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(
            @Parameter(description = "ID of the goal to be deleted", required = true)
            @PathVariable("id") Long id) {
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
    
    @Operation(summary = "Delete all goals", 
            description = "Deletes all goals and resets all match scores to 0-0")
    @ApiResponse(responseCode = "204", description = "All goals deleted and match scores reset")
    @DeleteMapping
    public ResponseEntity<?> deleteAllGoals() {
        for(Match match : matchRepository.findAll()) {
            match.setGoalsClubA(0);
            match.setGoalsClubB(0);
            matchRepository.save(match);
        }
        
        goalRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
}