package com.bitbus.fantasyprep.auction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerAuctionRepository extends JpaRepository<PlayerAuction, Integer> {

    List<PlayerAuction> findByAuction(Auction auction);

}
