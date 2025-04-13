package com.example.demo;
import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String FirstName;
    private String LastName;
    private Integer Goals;
    private Integer Assists;
    private Integer ShirtNr;
    
    public Player() {}

    public Player(String FirstName, String SecondName, Integer Goals, Integer Assists, Integer ShirtNr) {
        this.FirstName = FirstName;
        this.LastName = SecondName;
        this.Goals = Goals;
        this.Assists = Assists;
        this.ShirtNr = ShirtNr;
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
        FirstName = firstName;
    }
    public String getLastName() {
        return LastName;
    }
    public void setLastName(String lastName) {
        LastName = lastName;
    }
    public Integer getGoals() {
        return Goals;
    }
    public void setGoals(Integer goals) {
        Goals = goals;
    }
    public Integer getAssists() {
        return Assists;
    }
    public void setAssists(Integer assists) {
        Assists = assists;
    }
    public Integer getShirtNr() {
        return ShirtNr;
    }
    public void setShirtNr(Integer shirtNr) {
        ShirtNr = shirtNr;
    }
}
