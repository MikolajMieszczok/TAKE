package com.example.demo;
import jakarta.persistence.*;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clubName;
    private String managerName;
    private String clubRecord;

    public Club() {}

    public Club(String clubName, String managerName, String record) {
        this.clubName = clubName;
        this.managerName = managerName;
        this.clubRecord = record;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getClubRecord() { return clubRecord; }
    public void setClubRecord(String clubRecord) { this.clubRecord = clubRecord; }
}

