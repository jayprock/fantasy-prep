package com.bitbus.fantasyprep.auction;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepo;

    public Auction create(Auction auction) {
        return auctionRepo.save(auction);
    }

    public Optional<Auction> findById(String auctionId) {
        return auctionRepo.findById(auctionId);
    }
}
