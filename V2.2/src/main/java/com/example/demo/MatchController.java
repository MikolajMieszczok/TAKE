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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/matches")
@Tag(name = "Matches", description = "Endpoints for managing matches")
public class MatchController {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Operation(summary = "Get all matches", description = "Returns a list of all matches with their details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    })
    @GetMapping
    public @ResponseBody CollectionModel<MatchDTO> getAllMatches() {
        List<MatchDTO> matchesDTO = matchRepository.findAll()
                .stream()
                .map(MatchDTO::new)
                .toList();
        return CollectionModel.of(matchesDTO);
    }

    @Operation(summary = "Get a match by ID", description = "Returns a single match by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Match found"),
        @ApiResponse(responseCode = "404", description = "Match not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getById(@PathVariable("id") Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        return ResponseEntity.ok(new MatchDTO(match));
    }

    @Operation(summary = "Create a new match", description = "Creates a match between two existing clubs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Match created"),
        @ApiResponse(responseCode = "400", description = "Invalid input or same club used twice")
    })
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

    @Operation(summary = "Update an existing match", description = "Updates match goals and date. Changing teams is not allowed")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Match updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input or teams changed"),
        @ApiResponse(responseCode = "404", description = "Match not found")
    })
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

    @Operation(summary = "Delete all matches", description = "Deletes all matches from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "All matches deleted")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAllMatches() {
        matchRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a match by ID", description = "Deletes a specific match by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Match deleted"),
        @ApiResponse(responseCode = "404", description = "Match not found")
    })
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
