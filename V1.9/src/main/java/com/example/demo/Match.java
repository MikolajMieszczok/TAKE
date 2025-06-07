package com.example.demo;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "matches")
@Getter @Setter
public class Match {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    private Integer GoalsClubA;
    private Integer GoalsClubB;
	
    private Integer Gameweek;
    private LocalDate DateOfMatches;
    
    @ManyToOne
    @JoinColumn(name = "clubA_id")
    private Club ClubA;
    
    @ManyToOne
    @JoinColumn(name = "clubB_id")
    private Club ClubB;

    public Match() {}

    public Match(Integer _GoalsClubA, Integer _GoalsClubB, Integer _Gameweek, LocalDate _DateOfMatches, Club _ClubA, Club _ClubB) {
        this.GoalsClubA = 0;
        this.GoalsClubB = 0;
        this.Gameweek = _Gameweek;
        this.DateOfMatches = _DateOfMatches;
        this.ClubA = _ClubA;
        this.ClubB = _ClubB;
    }
}
