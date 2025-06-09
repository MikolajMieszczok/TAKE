package com.example.demo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter @Setter
public class ClubDTO extends RepresentationModel<ClubDTO> {
    private Long id;
    private String clubName;
    private String managerName;
    private String clubRecord;
    private List<PlayerDTO> players;
}
