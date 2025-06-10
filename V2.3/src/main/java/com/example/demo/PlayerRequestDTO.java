package com.example.demo;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerRequestDTO {
    private String firstName;
    private String lastName;
    private Integer shirtNr;
    private Long clubId;
}