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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

// Swagger/OpenAPI Annotations
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/players")
@Tag(name = "Player Management", description = "Operations related to football players") // Tag for the whole controller
public class PlayerController {

    @Autowired
    private PlayerRepository PlayerRepository;
    @Autowired
    private ClubRepository ClubRepository;

    @Operation(summary = "Get the top scorer player") // Summary of the operation
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved top scorer",
                    content = @Content(schema = @Schema(implementation = PlayerDTO.class))),
            @ApiResponse(responseCode = "404", description = "No players or no goals found",
                    content = @Content(schema = @Schema(implementation = ResourceNotFoundException.class)))
    })
    @GetMapping("/stats/topscorer")
    public ResponseEntity<PlayerDTO> getTopScorer() {
        List<Player> players = PlayerRepository.findAll();

        Player topScorer = players.stream()
                .max((p1, p2) -> Integer.compare(p1.getGoals().size(), p2.getGoals().size()))
                .orElseThrow(() -> new ResourceNotFoundException("No players or no goals found."));

        return ResponseEntity.ok(new PlayerDTO(topScorer));
    }

    @Operation(summary = "Get all players")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of players",
                    content = @Content(schema = @Schema(implementation = CollectionModel.class))) // Assuming CollectionModel of PlayerDTO
    })
    @GetMapping
    public @ResponseBody CollectionModel<PlayerDTO> getPlayers() {
        List<PlayerDTO> playersDTO = new ArrayList<>();
        for (Player player : PlayerRepository.findAll())
            playersDTO.add(new PlayerDTO(player));
        return CollectionModel.of(playersDTO);
    }

    @Operation(summary = "Create multiple players in a batch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created players",
                    content = @Content(schema = @Schema(implementation = List.class))), // List of PlayerDTO
            @ApiResponse(responseCode = "404", description = "Club not found for one or more players",
                    content = @Content(schema = @Schema(implementation = ResourceNotFoundException.class)))
    })
    @PostMapping("/batch")
    public ResponseEntity<?> createPlayers(@RequestBody List<PlayerRequestDTO> playerDTOs) {
        List<Player> newPlayers = new ArrayList<>();

        for (PlayerRequestDTO dto : playerDTOs) {
            Club club = ClubRepository.findById(dto.getClubId())
                    .orElseThrow(() -> new ResourceNotFoundException("Club with id " + dto.getClubId() + " not found."));

            Player player = new Player();
            player.setFirstName(dto.getFirstName());
            player.setLastName(dto.getLastName());
            player.setShirtNr(dto.getShirtNr());
            player.setClub(club);

            newPlayers.add(player);
        }

        List<Player> saved = PlayerRepository.saveAll(newPlayers);
        List<PlayerDTO> result = saved.stream().map(PlayerDTO::new).toList();
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Get a player by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved player",
                    content = @Content(schema = @Schema(implementation = PlayerDTO.class))),
            @ApiResponse(responseCode = "404", description = "Player not found",
                    content = @Content(schema = @Schema(implementation = ResourceNotFoundException.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getById(@Parameter(description = "ID of the player to retrieve") @PathVariable("id") Long id) {
        Player player = PlayerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + id));

        return ResponseEntity.ok(new PlayerDTO(player));
    }

    @Operation(summary = "Create a new player")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Player created successfully",
                    content = @Content(schema = @Schema(implementation = PlayerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid player data",
                    content = @Content(schema = @Schema(implementation = List.class))) // List of error messages
    })
    @PostMapping
    public ResponseEntity<?> createPlayer(@Valid @RequestBody Player player, BindingResult result) {

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }

        Player saved = PlayerRepository.save(player);

        return ResponseEntity.status(HttpStatus.CREATED).body(new PlayerDTO(saved));
    }

    @Operation(summary = "Update an existing player")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player updated successfully",
                    content = @Content(schema = @Schema(implementation = PlayerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid player data",
                    content = @Content(schema = @Schema(implementation = List.class))), // List of error messages
            @ApiResponse(responseCode = "404", description = "Player or Club not found",
                    content = @Content(schema = @Schema(implementation = ResourceNotFoundException.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlayer(@Parameter(description = "ID of the player to update") @PathVariable("id") Long id, @Valid @RequestBody Player playerDetails, BindingResult result) {

        Player player = PlayerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + id));

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }

        if (playerDetails.getClub().getId() != null) {

            ClubRepository.findById(playerDetails.getClub().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + playerDetails.getClub().getId()));

        }

        player.setFirstName(playerDetails.getFirstName());
        player.setLastName(playerDetails.getLastName());
        player.setGoals(playerDetails.getGoals());
        player.setShirtNr(playerDetails.getShirtNr());
        player.setClub(playerDetails.getClub());

        Player saved = PlayerRepository.save(player);

        return ResponseEntity.ok(new PlayerDTO(saved));
    }

    @Operation(summary = "Delete all players")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All players deleted successfully")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAllPlayers() {
        PlayerRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a player by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Player deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Player not found",
                    content = @Content(schema = @Schema(implementation = ResourceNotFoundException.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePlayer(@Parameter(description = "ID of the player to delete") @PathVariable("id") Long id) {
        Player player = PlayerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + id));

        PlayerRepository.delete(player);
        return ResponseEntity.noContent().build();
    }
}