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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerRepository PlayerRepository;
    @Autowired
    private ClubRepository ClubRepository;

    @Operation(summary = "Get top scorer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top scorer found",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = PlayerDTO.class))),
        @ApiResponse(responseCode = "404", description = "No players or no goals found",
                     content = @Content)
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
    @GetMapping
    public @ResponseBody CollectionModel<PlayerDTO> getPlayers() {
        List<PlayerDTO> playersDTO = new ArrayList<>();
        for (Player player : PlayerRepository.findAll())
            playersDTO.add(new PlayerDTO(player));
        return CollectionModel.of(playersDTO);
    }

    @Operation(summary = "Create multiple players")
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

    @Operation(summary = "Get player by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getById(@PathVariable("id") Long id) {
        Player player = PlayerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + id));

        return ResponseEntity.ok(new PlayerDTO(player));
    }

    @Operation(summary = "Create a new player")
    @PostMapping
    public ResponseEntity<?> createPlayer(@Valid @RequestBody Player player, BindingResult result) {

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }

        Player saved = PlayerRepository.save(player);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Update existing player")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable("id") Long id, @Valid @RequestBody Player playerDetails, BindingResult result) {

        Player player = PlayerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + id));

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }

        player.setFirstName(playerDetails.getFirstName());
        player.setLastName(playerDetails.getLastName());
        player.setGoals(playerDetails.getGoals());
        player.setShirtNr(playerDetails.getShirtNr());
        player.setClub(playerDetails.getClub());

        return ResponseEntity.ok(PlayerRepository.save(player));
    }

    @Operation(summary = "Delete all players")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllPlayers() {
        PlayerRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a player by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePlayer(@PathVariable("id") Long id) {
        Player player = PlayerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + id));

        PlayerRepository.delete(player);
        return ResponseEntity.noContent().build();
    }
}
