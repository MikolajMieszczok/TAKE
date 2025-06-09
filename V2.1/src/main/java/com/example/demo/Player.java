package com.example.demo;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "players")
@Getter @Setter
public class Player {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull(message = "First name cannot be null")
	private String FirstName;

	@NotNull(message = "Last name cannot be null")
	private String LastName;

	@Min(value = 1, message = "Shirt number must be 1 or greater")
	private Integer ShirtNr;
    
    @ManyToOne
    @JoinColumn(name = "club_id") 
    private Club club;
    
    @OneToMany(mappedBy="player")
    private List<Goal> Goals;
    
    public Player() {}

    public Player(String FirstName, String SecondName, List<Goal> Goals, Integer ShirtNr, Club _club) {
        this.FirstName = FirstName;
        this.LastName = SecondName;
        this.Goals = Goals;
        this.ShirtNr = ShirtNr;
        this.club = _club;
    }
   
}
