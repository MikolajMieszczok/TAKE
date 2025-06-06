package com.example.demo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Getter @Setter
public class MatchDTO extends RepresentationModel<MatchDTO> {
    private Long id;
    private Integer goalsClubA;
    private Integer goalsClubB;
    private Integer gameweek;
    private LocalDate dateOfMatches;
    private Long clubAId;
    private Long clubBId;
}
