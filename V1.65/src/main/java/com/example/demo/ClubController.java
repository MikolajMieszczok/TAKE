package com.example.demo;

import com.example.demo.ClubDTO;
import com.example.demo.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clubs")
public class ClubController {

    @Autowired private ClubService clubService;

    @GetMapping
    public List<ClubDTO> getAllClubs() {
        return clubService.getAllClubs().stream()
                .map(this::addLinks)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ClubDTO getClubById(@PathVariable Long id) {
        return addLinks(clubService.getClubById(id));
    }

    @PostMapping
    public ClubDTO createClub(@RequestBody ClubDTO dto) {
        return addLinks(clubService.createClub(dto));
    }

    private ClubDTO addLinks(ClubDTO club) {
        club.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(ClubController.class).getClubById(club.getId())).withSelfRel());
        club.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(ClubController.class).getAllClubs()).withRel("all-clubs"));
        return club;
    }
}
