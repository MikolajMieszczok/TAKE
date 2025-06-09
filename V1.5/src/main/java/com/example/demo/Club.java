package com.example.demo;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clubName;
    private String managerName;
    private String clubRecord;
    private List<Player> players;
    private List<Match> matches;

    public Club() {}

    public Club(String clubName, String managerName, String record, List<Player> players, List<Match> matches) {
        this.clubName = clubName;
        this.managerName = managerName;
        this.clubRecord = record;
        this.players = players;
        this.matches = matches;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getClubRecord() { return clubRecord; }
    public void setClubRecord(String clubRecord) { this.clubRecord = clubRecord; }
    
    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }
    
    public List<Match> getMatches() { return matches; }
    public void setMatches(List<Match> matchess) { this.matches = matches; }
}

