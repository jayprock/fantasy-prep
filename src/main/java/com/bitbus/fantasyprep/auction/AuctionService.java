package com.bitbus.fantasyprep.auction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepo;

    public Auction create(Auction auction) {
        return auctionRepo.save(auction);
    }
}
