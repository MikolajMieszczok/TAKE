package com.example.demo;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "matches")
public class Match {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private List<Goal> GoalsClubA;
    private List<Goal> GoalsClubB;
    private Integer Gameweek;
    private LocalDate DateOfMatches;
    private Club ClubA;
    private Club ClubB;

    public Match() {}

    public Match(List<Goal> _GoalsClubA, List<Goal> _GoalsClubB, Integer _Gameweek, LocalDate _DateOfMatches, Club _ClubA, Club _ClubB) {
        this.GoalsClubA = _GoalsClubA;
        this.GoalsClubB = _GoalsClubB;
        this.Gameweek = _Gameweek;
        this.DateOfMatches = _DateOfMatches;
        this.ClubA = _ClubA;
        this.ClubB = _ClubB;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Goal> getGoalsClubA() {
        return GoalsClubA;
    }

    public void setGoalsClubA(List<Goal> goalsClubA) {
        this.GoalsClubA = goalsClubA;
    }

    public List<Goal> getGoalsClubB() {
        return GoalsClubB;
    }

    public void setGoalsClubB(List<Goal> goalsClubB) {
        this.GoalsClubB = goalsClubB;
    }

    public Integer getGameweek() {
        return Gameweek;
    }

    public void setGameweek(Integer gameweek) {
        this.Gameweek = gameweek;
    }

    public LocalDate getDateOfMatches() {
        return DateOfMatches;
    }

    public void setDateOfMatches(LocalDate dateOfMatches) {
        this.DateOfMatches = dateOfMatches;
    }
    
    public Club getClubA() {
        return ClubA;
    }

    public void setClubA(Club ClubA) {
        this.ClubA = ClubA;
    }
    
    public Club getClubB() {
        return ClubB;
    }

    public void setClubB(Club ClubB) {
        this.ClubB = ClubB;
    }
    
}
