package com.example.demo;
import java.time.LocalDate;

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
    private Integer GoalsClubA;
    private Integer GoalsClubB;
    private Integer Gameweek;
    private LocalDate DateOfMatches;

    public Match() {}

    public Match(Integer _GoalsClubA, Integer _GoalsClubB, Integer _Gameweek, LocalDate _DateOfMatches) {
        this.GoalsClubA = _GoalsClubA;
        this.GoalsClubB = _GoalsClubB;
        this.Gameweek = _Gameweek;
        this.DateOfMatches = _DateOfMatches;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGoalsClubA() {
        return GoalsClubA;
    }

    public void setGoalsClubA(Integer goalsClubA) {
        this.GoalsClubA = goalsClubA;
    }

    public Integer getGoalsClubB() {
        return GoalsClubB;
    }

    public void setGoalsClubB(Integer goalsClubB) {
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
}
