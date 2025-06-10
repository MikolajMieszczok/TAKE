package com.example.demo;


import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;
import lombok.Setter;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Getter @Setter
public class ClubDTO extends RepresentationModel<ClubDTO>{
public ClubDTO(Club club) {
super();
this.id = club.getId();
this.clubName = club.getClubName();
this.managerName = club.getManagerName();
this.wins = club.getWins();
this.draws = club.getDraws();
this.loses = club.getLoses();

if (club.getPlayers() != null && !club.getPlayers().isEmpty()) {
    for (Player player : club.getPlayers()) {
        this.add(linkTo(methodOn(PlayerController.class)
                .getById(player.getId())).withRel("player"));
    	}
	}
}
private Long id;
private String clubName;
private String managerName;
private Integer wins;
private Integer draws;
private Integer loses;
}
