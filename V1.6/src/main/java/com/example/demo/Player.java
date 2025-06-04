package com.example.demo;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "players")
@Getter @Setter
public class Player {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String FirstName;
    private String LastName;
    private Integer Assists;
    private Integer ShirtNr;
    
    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;
    
    @OneToMany(mappedBy="player")
    @JsonIgnore
    private List<Goal> Goals;
    
    public Player() {}

    public Player(String FirstName, String SecondName, List<Goal> Goals, Integer Assists, Integer ShirtNr, Club club) {
        this.FirstName = FirstName;
        this.LastName = SecondName;
        this.Goals = Goals;
        this.Assists = Assists;
        this.ShirtNr = ShirtNr;
        this.club = club;
    }
   
}
