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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/matches")
@Tag(name = "Match Management", description = "Endpoints for managing football matches")
public class MatchController {

    private GoalRepository goalRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ClubRepository clubRepository;
    

    MatchController(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }
    
    @Operation(summary = "Get all matches", description = "Retrieves a list of all football matches")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of matches",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = MatchDTO.class)))
    @GetMapping
    public @ResponseBody CollectionModel<MatchDTO> getAllMatches() {
        List<MatchDTO> matchesDTO = matchRepository.findAll()
                .stream()
                .map(MatchDTO::new)
                .toList();
        return CollectionModel.of(matchesDTO);
    }

    @Operation(summary = "Get match by ID", description = "Retrieves a specific match by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved match",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = MatchDTO.class))),
        @ApiResponse(responseCode = "404", description = "Match not found",
                content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getById(
            @Parameter(description = "ID of the match to be retrieved", required = true)
            @PathVariable("id") Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        return ResponseEntity.ok(new MatchDTO(match));
    }

    @Operation(summary = "Create a new match", 
            description = "Creates a new football match between two different clubs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Match created successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = MatchDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or same team match",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Club not found",
                content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> createMatch(
            @Parameter(description = "Match object to be created", required = true,
                    schema = @Schema(implementation = Match.class))
            @Valid @RequestBody Match match, BindingResult result) {
        
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

    @Operation(summary = "Update match details", 
            description = "Updates match score and date (clubs cannot be changed)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Match updated successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = MatchDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or attempt to change clubs",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Match not found",
                content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatch(
            @Parameter(description = "ID of the match to be updated", required = true)
            @PathVariable("id") Long id, 
            @Parameter(description = "Updated match details (only score and date can be changed)", required = true,
                    schema = @Schema(implementation = Match.class))
            @Valid @RequestBody Match matchDetails, BindingResult result) {
        
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

    @Operation(summary = "Delete all matches", 
            description = "Deletes all matches. Use unsafe=true to also delete related goals")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "All matches deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot delete due to references in goals",
                content = @Content)
    })
    @DeleteMapping
    public ResponseEntity<?> deleteAllMatches(
            @Parameter(description = "Set to 'true' to force delete with related goals")
            @RequestParam(name="unsafe", required = false) String unsafe) {
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

    @Operation(summary = "Delete match by ID", 
            description = "Deletes a specific match. Use unsafe=true to also delete related goals")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Match deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot delete due to references in goals",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Match not found",
                content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(
            @Parameter(description = "ID of the match to be deleted", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Set to 'true' to force delete with related goals")
            @RequestParam(name="unsafe", required = false) String unsafe) {
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