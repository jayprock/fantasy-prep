package com.bitbus.fantasyprep.auction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.bitbus.fantasyprep.player.Player;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"player", "auction"})
@ToString(exclude = {"player", "auction"})
public class PlayerAuction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int playerAuctionId;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    private double auctionValue;

    private Double optimalValue;

}
