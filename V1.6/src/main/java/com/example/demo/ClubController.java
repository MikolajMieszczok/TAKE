package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clubs")
public class ClubController {

    @Autowired
    private ClubRepository ClubRepository;

    @GetMapping
    public List<Club> getAllClubs() {
        return ClubRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Club> getById(@PathVariable("id") Long id) {
        Optional<Club> club = ClubRepository.findById(id);
        return club.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Club createClub(@RequestBody Club club) {
    return ClubRepository.save(club);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Club> updateClub(@PathVariable("id") Long id, @RequestBody Club clubDetails) {
        return ClubRepository.findById(id)
            .map(club -> {
                club.setClubName(clubDetails.getClubName());
                club.setManagerName(clubDetails.getManagerName());
                club.setClubRecord(clubDetails.getClubRecord());
                club.setPlayers(clubDetails.getPlayers());
                return ResponseEntity.ok(ClubRepository.save(club));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteAllClubs() {
        ClubRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClub(@PathVariable("id") Long id) {
        return ClubRepository.findById(id)
            .map(club -> {
                ClubRepository.delete(club);
                return ResponseEntity.noContent().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
