package com.bitbus.fantasyprep;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.bitbus.fantasyprep.auction.Auction;
import com.bitbus.fantasyprep.auction.AuctionService;
import com.bitbus.fantasyprep.league.League;
import com.bitbus.fantasyprep.lineup.Lineup;
import com.bitbus.fantasyprep.lineup.LineupOptimizationService;
import com.bitbus.fantasyprep.player.PlayerProjectedPoints;
import com.bitbus.fantasyprep.player.PlayerProjectedPointsService;

@SpringBootApplication
public class OptimalLineupPrinter {

    @Autowired
    private LineupOptimizationService lineupOptimizationService;

    @Autowired
    private PlayerProjectedPointsService playerProjectedPointsService;

    @Autowired
    private PlayerProjectedPointsCalculator projectedPointsCalculator;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private DefaultAuctionValuesLoader auctionValuesLoader;


    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext ctx = SpringApplication.run(OptimalLineupPrinter.class, args);
        OptimalLineupPrinter main = ctx.getBean(OptimalLineupPrinter.class);
        main.optimize();
    }

    public void optimize() throws IOException {
        List<PlayerProjectedPoints> playerPointProjections = playerProjectedPointsService.findByLeague(League.MERGER);
        if (playerPointProjections.size() == 0) {
            projectedPointsCalculator.calculate();
        }
        Optional<Auction> auction = auctionService.findById(DefaultAuctionValuesLoader.AUCTION_ID);
        if (!auction.isPresent()) {
            auctionValuesLoader.load();
        }

        Lineup optimalLineup = lineupOptimizationService.optimize(League.MERGER, 180);
        optimalLineup.prettyPrint();

    }

}
