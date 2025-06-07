package com.example.demo;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clubs")
@Getter @Setter
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clubName;
    private String managerName;
    private Integer wins;
    private Integer draws;
    private Integer loses;
    
    //@JsonManagedReference  // Wcześniej JSON IGNORE ale nie działało dodawanie klubów z graczami
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)//, cascade={CascadeType.ALL})
    private List<Player> players;
    
    public Club() {}

    public Club(String clubName, String managerName, List<Player> players, Integer wins, Integer draws, Integer loses) {
        this.clubName = clubName;
        this.managerName = managerName;
        this.players = players;
        this.wins = 0;
        this.draws = 0;
        this.loses = 0;
    }
}

