package com.example.demo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "goals")
@Getter @Setter
public class Goal {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "ownGoal cannot be null")
	private Boolean ownGoal;
	
	@NotNull(message = "Player cannot be null")
	@ManyToOne
	private Player player;
	
	@NotNull(message = "Match cannot be null")
	@ManyToOne
	private Match match;
	
	@NotNull(message = "Minute cannot be null")
	@Min(value = 1, message = "Minute be 1 or greater")
	@Max(value = 120, message = "Minute be less than 120")
	private Integer minute;
	
	public Goal () {}
	
	public Goal(Boolean _ownGoal, Player _player, Match _match, Integer _minute) {
		this.ownGoal= _ownGoal;
		this.player = _player;
		this.match = _match;
		this.minute = _minute;
	}
}
