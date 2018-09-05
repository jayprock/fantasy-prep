package com.bitbus.fantasyprep.player;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.bitbus.fantasyprep.league.LeagueSettings;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"projection", "leagueSettings"})
@ToString(exclude = {"projection", "leagueSettings"})
public class PlayerProjectedPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int projectedPointsId;

    @ManyToOne
    @JoinColumn(name = "projection_id")
    private PlayerProjection projection;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private LeagueSettings leagueSettings;

    private double projectedPoints;

}
