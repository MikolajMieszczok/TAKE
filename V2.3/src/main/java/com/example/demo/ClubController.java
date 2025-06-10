package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.parser.Part.IgnoreCaseType;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/clubs")
@Tag(name = "Club Management", description = "Endpoints for managing football clubs")
public class ClubController {

    @Autowired
    private ClubRepository ClubRepository;
    @Autowired
    private MatchRepository MatchRepository;
    @Autowired
    private PlayerRepository PlayerRepository;
    @Autowired
    private GoalRepository goalRepository;

    @Operation(summary = "Get all clubs", description = "Retrieves a list of all football clubs")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of clubs",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = ClubDTO.class)))
    @GetMapping
    public @ResponseBody CollectionModel<ClubDTO> getAllClubs() {
        List<ClubDTO> clubsDTO = new ArrayList<>();
        for(Club club : ClubRepository.findAll())
            clubsDTO.add(new ClubDTO(club));
        return CollectionModel.of(clubsDTO);
    }

    @Operation(summary = "Get club by ID", description = "Retrieves a specific club by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved club",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ClubDTO.class))),
        @ApiResponse(responseCode = "404", description = "Club not found",
                content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClubDTO> getById(
            @Parameter(description = "ID of the club to be retrieved", required = true)
            @PathVariable("id") Long id) {
        Club club = ClubRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));
        return ResponseEntity.ok(new ClubDTO(club));
    }

    @Operation(summary = "Create a new club", description = "Creates a new football club with players")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Club created successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ClubDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> createClub(
            @Parameter(description = "Club object to be created", required = true,
                    schema = @Schema(implementation = Club.class))
            @Valid @RequestBody Club club, BindingResult result) {
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

    @Operation(summary = "Calculate match results", 
            description = "Calculates match results and updates club statistics (wins, draws, loses)")
    @ApiResponse(responseCode = "200", description = "Club records updated successfully",
            content = @Content(mediaType = "text/plain"))
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

    @Operation(summary = "Update club", description = "Updates an existing football club")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Club updated successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ClubDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Club not found",
                content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClub(
            @Parameter(description = "ID of the club to be updated", required = true)
            @PathVariable("id") Long id, 
            @Parameter(description = "Updated club object", required = true,
                    schema = @Schema(implementation = Club.class))
            @Valid @RequestBody Club clubDetails, BindingResult result) {
       
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }
        
        Club club = ClubRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));

        club.setClubName(clubDetails.getClubName());
        club.setManagerName(clubDetails.getManagerName());
        club.setPlayers(clubDetails.getPlayers());
        club.setWins(clubDetails.getWins());
        club.setDraws(clubDetails.getDraws());
        club.setLoses(clubDetails.getLoses());

        Club newClub = ClubRepository.save(club);
        
        return ResponseEntity.ok(new ClubDTO(newClub));
    }

    @Operation(summary = "Delete all clubs", 
            description = "Deletes all clubs. Use unsafe=true to also delete related matches and goals")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "All clubs deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot delete due to references in matches",
                content = @Content)
    })
    @DeleteMapping()
    public ResponseEntity<?> deleteAllClubs(
            @Parameter(description = "Set to 'true' to force delete with related matches and goals")
            @RequestParam(name="unsafe", required = false) String unsafe) {
        
        if ("true".equalsIgnoreCase(unsafe)) {
            goalRepository.deleteAll();
            MatchRepository.deleteAll();
            ClubRepository.deleteAll();
        }
        else {
            try {
                ClubRepository.deleteAll();
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("There are some references in matches, use unsafe=true or delete matches manually.");
            }
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete club by ID", 
            description = "Deletes a specific club. Use unsafe=true to also delete related matches and goals")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Club deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot delete due to references in matches",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Club not found",
                content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClub(
            @Parameter(description = "ID of the club to be deleted", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Set to 'true' to force delete with related matches and goals")
            @RequestParam(name="unsafe", required = false) String unsafe) {
        Club club = ClubRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));

        if ("true".equalsIgnoreCase(unsafe)) {
            List<Long> matchesToDelete = new ArrayList<>();
            
            for(Match match : MatchRepository.findAll()) {
                
                if(match.getClubA().getId() == id || match.getClubB().getId() == id) {
                    
                    List<Long> goalsToDelete = new ArrayList<>();
                    
                    for(Goal goal : goalRepository.findAll()) {
                        
                        if(goal.getMatch().getId() == match.getId()) {
                            goalsToDelete.add(goal.getId());
                        }
                    }
                    goalRepository.deleteAllById(goalsToDelete);
                    matchesToDelete.add(match.getId());
                }
            }
            
            MatchRepository.deleteAllById(matchesToDelete);
            ClubRepository.delete(club);
            return ResponseEntity.noContent().build();
        }
        
        try {
            ClubRepository.delete(club);
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("There are some references in matches, use unsafe=true or delete matches manually.");
        }
        
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get top scoring club", 
            description = "Retrieves the club that has scored the most goals in matches")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved top scoring club",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = ClubDTO.class)))
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

    @Operation(summary = "Get top conceding club", 
            description = "Retrieves the club that has conceded the most goals in matches")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved top conceding club",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = ClubDTO.class)))
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