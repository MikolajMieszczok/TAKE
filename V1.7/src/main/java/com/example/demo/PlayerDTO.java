package com.example.demo;


import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;
import lombok.Setter;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Getter @Setter
public class PlayerDTO extends RepresentationModel<PlayerDTO>{
public PlayerDTO(Player player) {
super();
this.id = player.getId();
this.firstName = player.getFirstName();
this.lastName = player.getLastName();
this.shirtNr = player.getShirtNr();

if(player.getClub() != null)
{
	this.clubId = player.getClub().getId();
	this.add(linkTo(methodOn(ClubController.class).getById(clubId)).withRel("club"));
}

if(player.getGoals() != null && !player.getGoals().isEmpty()){
	for(Goal goal : player.getGoals()) {
		this.add(linkTo(methodOn(GoalController.class).getById(goal.getId())).withRel("goals"));
	}
}


//if (club.getPlayers() != null && !club.getPlayers().isEmpty()) {
//    for (Player player : club.getPlayers()) {
//        this.add(linkTo(methodOn(PlayerController.class)
//                .getById(player.getId())).withRel("player"));
//    }
//}


//.getOwnerForCar(car.getIdc())).withRel("owner"));
//this.add(linkTo(methodOn(CarController.class)
//.getEqsForCar(car.getIdc())).withRel("equipments"));



}

private Long id;
private String firstName;
private String lastName;
private Integer assists;
private Integer shirtNr;

private Long clubId;
}
