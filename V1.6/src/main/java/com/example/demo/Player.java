package com.example.demo;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    
    @ManyToOne(fetch= FetchType.LAZY) //nie wiem jak dziala dokonca 
    @JoinColumn(name = "club_id") //kolumna nazywa się club id ale ma postman ma wysrane i trzeba odwoływać się po "club" :))
    @JsonBackReference // wcześniej nic, teraz jest bo nie działało dodawanie graczy z club
    private Club club;
    
    @OneToMany(mappedBy="player")
    @JsonIgnore
    private List<Goal> Goals;
    
    public Player() {}

    public Player(String FirstName, String SecondName, List<Goal> Goals, Integer Assists, Integer ShirtNr, Club _club) {
        this.FirstName = FirstName;
        this.LastName = SecondName;
        this.Goals = Goals;
        this.Assists = Assists;
        this.ShirtNr = ShirtNr;
        this.club = _club;
    }
   
}
