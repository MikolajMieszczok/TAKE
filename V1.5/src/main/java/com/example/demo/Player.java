package com.example.demo;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String FirstName;
    private String LastName;
    private Integer Assists;
    private Integer ShirtNr;
    private Club club;
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
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFirstName() {
        return FirstName;
    }
    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }
    public String getLastName() {
        return LastName;
    }
    public void setLastName(String lastName) {
        this.LastName = lastName;
    }
    public List<Goal> getGoals() {
        return Goals;
    }
    public void setGoals(List<Goal> goals) {
        this.Goals = goals;
    }
    public Integer getAssists() {
        return Assists;
    }
    public void setAssists(Integer assists) {
        this.Assists = assists;
    }
    public Integer getShirtNr() {
        return ShirtNr;
    }
    public void setShirtNr(Integer shirtNr) {
        this.ShirtNr = shirtNr;
    }
    public Club getClub() {
    	return club;
    }
    public void setClub(Club club) {
    	this.club = club;
    }
    
}
