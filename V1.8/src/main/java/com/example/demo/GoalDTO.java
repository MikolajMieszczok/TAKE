package com.example.demo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoalDTO extends RepresentationModel<GoalDTO>{
	public GoalDTO(Goal goal) {
		super();
		this.id = goal.getId();
		this.ownGoal = goal.getOwnGoal();

		if(goal.getPlayer() != null) {
			this.playerId = goal.getPlayer().getId();
			 this.add(linkTo(methodOn(PlayerController.class)
		                .getById(playerId)).withRel("player"));
		}
		
		if(goal.getMatch() != null) {
			this.matchId = goal.getMatch().getId();
			 this.add(linkTo(methodOn(MatchController.class)
		                .getById(matchId)).withRel("match"));
		}

		}

	private Long id;
	private Boolean ownGoal;
	private Long playerId;
	private Long matchId;
}
