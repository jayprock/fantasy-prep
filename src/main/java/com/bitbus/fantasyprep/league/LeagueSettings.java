package com.bitbus.fantasyprep.league;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class LeagueSettings {

    @Id
    @Enumerated(EnumType.STRING)
    private League leagueId;

    private int passingYardsPerPoint;

    private int passingTdPoints;

    private int interceptionPoints;

    private int rushingYardsPerPoint;

    private int rushingTdPoints;

    private double receptionPoints;

    private int receivingYardsPerPoint;

    private int receivingTdPoints;

}
