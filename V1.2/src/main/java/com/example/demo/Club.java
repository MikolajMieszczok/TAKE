package com.example.demo;
import jakarta.persistence.*;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ClubName;
    private String ManagerName;
    private String Record;

    public Club() {}

    public Club(String ClubName, String ManagerName, String Record) {
        this.ClubName = ClubName;
        this.ManagerName = ManagerName;
        this.Record = Record;
    }

    public Long getID_Club() { return id; }
    public void setID_Club(Long ID_Club) { this.id = ID_Club; }

    public String getClubName() { return ClubName; }
    public void setClubName(String ClubName) { this.ClubName = ClubName; }

    public String getManagerName() { return ManagerName; }
    public void setManagerName(String ManagerName) { this.ManagerName = ManagerName; }
    
    public String getRecord() { return Record; }
    public void setRecord(String Record) { this.Record = Record; }
}
