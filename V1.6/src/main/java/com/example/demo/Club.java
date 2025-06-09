package com.example.demo;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private String clubRecord;
    
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)//, cascade={CascadeType.ALL})
    @JsonBackReference // Wcześniej JSON IGNORE ale nie działało dodawanie klubów z graczami
    private List<Player> players;
    
    public Club() {}

    public Club(String clubName, String managerName, String record, List<Player> players) {
        this.clubName = clubName;
        this.managerName = managerName;
        this.clubRecord = record;
        this.players = players;
    }
}

