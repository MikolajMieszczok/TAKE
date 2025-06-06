package com.example.demo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter @Setter
public class PlayerDTO extends RepresentationModel<PlayerDTO> {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer assists;
    private Integer shirtNr;
    private Long clubId; // tylko ID klubu — relacje uproszczone
}