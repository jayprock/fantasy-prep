package com.bitbus.fantasyprep.player;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.bitbus.fantasyprep.auction.PlayerAuction;
import com.bitbus.fantasyprep.team.Team;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"team", "projection", "playerAuctions"})
@ToString(exclude = {"team", "projection", "playerAuctions"})
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int playerId;

    private String playerName;

    @Enumerated(EnumType.STRING)
    private Position position;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "player")
    private PlayerProjection projection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player")
    private List<PlayerAuction> playerAuctions;

    @Transient
    public double getProjectedPoints() {
        return projection.getPointProjections().get(0).getProjectedPoints();
    }

    @Transient
    public double getAuctionCost() {
        return playerAuctions.get(0).getAuctionValue();
    }

}
