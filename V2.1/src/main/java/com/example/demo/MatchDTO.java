package com.example.demo;


import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;
import lombok.Setter;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;


@Getter @Setter
public class MatchDTO extends RepresentationModel<MatchDTO>{
public MatchDTO(Match match) {
super();
this.id = match.getId();
this.goalsClubA = match.getGoalsClubA();
this.goalsClubB = match.getGoalsClubB();
this.dateOfMatches = match.getDateOfMatches();

if(match.getClubA() != null) {
		this.clubAId = match.getClubA().getId();
		this.add(linkTo(methodOn(ClubController.class).getById(clubAId)).withRel("clubA"));
	}

if(match.getClubB() != null){
		this.clubBId = match.getClubB().getId();
		this.add(linkTo(methodOn(ClubController.class).getById(clubBId)).withRel("clubB"));	
	}
}

private Long id;
private Integer goalsClubA;
private Integer goalsClubB;
private LocalDate dateOfMatches;

private Long clubAId;
private Long clubBId;
}