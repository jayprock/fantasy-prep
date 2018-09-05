package com.bitbus.fantasyprep.player;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"player", "pointProjections"})
@ToString(exclude = {"player", "pointProjections"})
public class PlayerProjection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int projectionId;

    @OneToOne
    @JoinColumn(name = "player_id")
    private Player player;

    private double passingYards;

    private double passingTds;

    private double interceptions;

    private double rushingYards;

    private double rushingTds;

    private double receptions;

    private double receivingYards;

    private double receivingTds;

    @OneToMany(mappedBy = "projection")
    private List<PlayerProjectedPoints> pointProjections;

}
