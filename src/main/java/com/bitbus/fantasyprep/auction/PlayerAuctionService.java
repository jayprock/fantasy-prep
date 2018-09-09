package com.bitbus.fantasyprep.auction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerAuctionService {

    @Autowired
    private PlayerAuctionRepository playerAuctionRepo;


    public List<PlayerAuction> findByAuction(Auction auction) {
        return playerAuctionRepo.findByAuction(auction);
    }

}
