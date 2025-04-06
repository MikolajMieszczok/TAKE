package com.example.demo;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
public class PlayerController {
	@Autowired
	private PlayerRepository PlayerRepository;
	
	@GetMapping
    public List<Player> getAllPlayers() {
        return PlayerRepository.findAll();
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<Player> getById(@PathVariable Long id) {
        Optional<Player> player = PlayerRepository.findById(id);
        return player.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
	
	@PostMapping
    public Player createPlayer(@RequestBody Player player) {
    return PlayerRepository.save(player);
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody Player playerDetails) {
        return PlayerRepository.findById(id)
            .map(player -> {
                player.setFirstName(playerDetails.getFirstName());
                player.setLastName(playerDetails.getLastName());
                player.setGoals(playerDetails.getGoals());
                player.setAssists(playerDetails.getAssists());
                player.setShirtNr(playerDetails.getShirtNr());
                return ResponseEntity.ok(PlayerRepository.save(player));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
	
	 @DeleteMapping
	    public ResponseEntity<Void> deleteAllPlayers() {
	        PlayerRepository.deleteAll();
	        return ResponseEntity.noContent().build();
	    }
	    
	    @DeleteMapping("/{id}")
	    public ResponseEntity<Object> deletePlayer(@PathVariable Long id) {
	        return PlayerRepository.findById(id)
	            .map(player -> {
	                PlayerRepository.delete(player);
	                return ResponseEntity.noContent().build();
	            })
	            .orElseGet(() -> ResponseEntity.notFound().build());
	    }
	}
