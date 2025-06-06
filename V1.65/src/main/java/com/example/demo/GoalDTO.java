package com.example.demo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter @Setter
public class GoalDTO extends RepresentationModel<GoalDTO> {
    private Long id;
    private Boolean ownGoal;
    private Long playerId;
    private Long matchId;
}
