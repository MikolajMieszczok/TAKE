package com.example.demo;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "matches")
@Getter @Setter
public class Match {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull(message = "Goals for Club A cannot be null")
	@Min(value = 0, message = "Goals must be 0 or above")
    private Integer GoalsClubA;
	
	@NotNull(message = "Goals for Club B cannot be null")
	@Min(value = 0, message = "Goals must be 0 or above")
    private Integer GoalsClubB;
	
	@NotNull(message = "Match date cannot be null")
    private LocalDate DateOfMatches;
    
	@NotNull(message = "Club A cannot be null")
    @ManyToOne
    @JoinColumn(name = "clubA_id")
    private Club ClubA;
    
	@NotNull(message = "Club B cannot be null")
	@ManyToOne
    @JoinColumn(name = "clubB_id")
    private Club ClubB;

    public Match() {}

    public Match(Integer _GoalsClubA, Integer _GoalsClubB, LocalDate _DateOfMatches, Club _ClubA, Club _ClubB) {
        this.GoalsClubA = _GoalsClubA;
        this.GoalsClubB = _GoalsClubB;
        this.DateOfMatches = _DateOfMatches;
        this.ClubA = _ClubA;
        this.ClubB = _ClubB;
    }
}
