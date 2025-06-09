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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
public class PlayerController {
	
	@Autowired
	private PlayerRepository PlayerRepository;
	@Autowired
	private ClubRepository ClubRepository;
//	@GetMapping
//    public List<Player> getAllPlayers() {
//        return PlayerRepository.findAll();
//    }
    @GetMapping("/stats/topscorer")
    public ResponseEntity<PlayerDTO> getTopScorer() {
        List<Player> players = PlayerRepository.findAll();

        Player topScorer = players.stream()
                .max((p1, p2) -> Integer.compare(p1.getGoals().size(), p2.getGoals().size()))
                .orElse(null);

        if (topScorer == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new PlayerDTO(topScorer));
    }
	@GetMapping
	public @ResponseBody CollectionModel<PlayerDTO> getCars() {
	List<PlayerDTO> playersDTO = new ArrayList<>();
	for(Player player:PlayerRepository.findAll())
	playersDTO.add(new PlayerDTO(player));
	return CollectionModel.of(playersDTO);
	}
	
//	@GetMapping("/{id}")
//    public ResponseEntity<Player> getById(@PathVariable("id") Long id) {
//        Optional<Player> player = PlayerRepository.findById(id);
//        return player.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }
	@PostMapping("/batch")
	public ResponseEntity<?> createPlayers(@RequestBody List<PlayerRequestDTO> playerDTOs) {
	    List<Player> newPlayers = new ArrayList<>();

	    for (PlayerRequestDTO dto : playerDTOs) {
	        Optional<Club> clubOpt = ClubRepository.findById(dto.getClubId());
	        if (clubOpt.isEmpty()) {
	            return ResponseEntity.badRequest().body("Club with id " + dto.getClubId() + " not found.");
	        }

	        Player player = new Player();
	        player.setFirstName(dto.getFirstName());
	        player.setLastName(dto.getLastName());
	        player.setAssists(dto.getAssists());
	        player.setShirtNr(dto.getShirtNr());
	        player.setClub(clubOpt.get());

	        newPlayers.add(player);
	    }

	    List<Player> saved = PlayerRepository.saveAll(newPlayers);
	    List<PlayerDTO> result = saved.stream().map(PlayerDTO::new).toList();
	    return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PlayerDTO> getById(@PathVariable("id") Long id){
		Optional<Player> player = PlayerRepository.findById(id);
		
		if(player.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		PlayerDTO playerDTO = new PlayerDTO(player.get());
		
		return ResponseEntity.ok(playerDTO);
		
	}
	
	@PostMapping
    public Player createPlayer(@RequestBody Player player) {
    return PlayerRepository.save(player);
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") Long id, @RequestBody Player playerDetails) {
        return PlayerRepository.findById(id)
            .map(player -> {
                player.setFirstName(playerDetails.getFirstName());
                player.setLastName(playerDetails.getLastName());
                player.setGoals(playerDetails.getGoals());
                player.setAssists(playerDetails.getAssists());
                player.setShirtNr(playerDetails.getShirtNr());
                player.setClub(playerDetails.getClub());
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
	    public ResponseEntity<Object> deletePlayer(@PathVariable("id") Long id) {
	        return PlayerRepository.findById(id)
	            .map(player -> {
	                PlayerRepository.delete(player);
	                return ResponseEntity.noContent().build();
	            })
	            .orElseGet(() -> ResponseEntity.notFound().build());
	    }

}
