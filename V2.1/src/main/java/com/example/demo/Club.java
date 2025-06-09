package com.example.demo;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotNull;

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

    @NotNull(message = "Club name cannot be null! Please provide a club name.")
    private String clubName;

    @NotNull(message = "Manager name cannot be null! Please provide a manager name.")
    private String managerName;

    @NotNull(message = "Number of wins cannot be null! Please provide the wins count.")
    private Integer wins;

    @NotNull(message = "Number of draws cannot be null! Please provide the draws count.")
    private Integer draws;

    @NotNull(message = "Number of losses cannot be null! Please provide the loses count.")
    private Integer loses;
    
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)//, cascade={CascadeType.ALL})
    private List<Player> players;
    
    public Club() {}

    public Club(String clubName, String managerName, List<Player> players, Integer wins, Integer draws, Integer loses) {
        this.clubName = clubName;
        this.managerName = managerName;
        this.players = players;
        this.wins = wins;
        this.draws = draws;
        this.loses = loses;
    }
}

