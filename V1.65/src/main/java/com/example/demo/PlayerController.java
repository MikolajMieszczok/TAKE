package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired private PlayerService playerService;

    @GetMapping
    public List<PlayerDTO> getAllPlayers() {
        return playerService.getAllPlayers().stream()
                .map(this::addLinks)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PlayerDTO getPlayerById(@PathVariable Long id) {
        return addLinks(playerService.getPlayerById(id));
    }

    @PostMapping
    public PlayerDTO createPlayer(@RequestBody PlayerDTO dto) {
        return addLinks(playerService.createPlayer(dto));
    }

    @PutMapping("/{id}")
    public PlayerDTO updatePlayer(@PathVariable Long id, @RequestBody PlayerDTO dto) {
        return addLinks(playerService.updatePlayer(id, dto));
    }

    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }

    private PlayerDTO addLinks(PlayerDTO player) {
        player.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(PlayerController.class).getPlayerById(player.getId())).withSelfRel());
        player.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(PlayerController.class).getAllPlayers()).withRel("all-players"));
        return player;
    }
}
