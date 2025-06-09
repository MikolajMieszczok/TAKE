package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/matches")
public class MatchController {

    @Autowired private MatchService matchService;

    @GetMapping
    public List<MatchDTO> getAllMatches() {
        return matchService.getAllMatches().stream()
                .map(this::addLinks)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MatchDTO getMatchById(@PathVariable Long id) {
        return addLinks(matchService.getMatchById(id));
    }

    @PostMapping
    public MatchDTO createMatch(@RequestBody MatchDTO dto) {
        return addLinks(matchService.createMatch(dto));
    }

    @PutMapping("/{id}")
    public MatchDTO updateMatch(@PathVariable Long id, @RequestBody MatchDTO dto) {
        return addLinks(matchService.updateMatch(id, dto));
    }

    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
    }

    private MatchDTO addLinks(MatchDTO match) {
        match.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(MatchController.class).getMatchById(match.getId())).withSelfRel());
        match.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(MatchController.class).getAllMatches()).withRel("all-matches"));
        return match;
    }
}
