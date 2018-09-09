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
import com.bitbus.fantasyprep.player.Player;
import com.bitbus.fantasyprep.player.PlayerProjectedPoints;
import com.bitbus.fantasyprep.player.PlayerProjectedPointsService;
import com.bitbus.fantasyprep.player.PlayerService;

@SpringBootApplication
public class OptimalSalaryCalculator {

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

    @Autowired
    private PlayerService playerService;


    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext ctx = SpringApplication.run(OptimalSalaryCalculator.class, args);
        OptimalSalaryCalculator main = ctx.getBean(OptimalSalaryCalculator.class);
        main.calculate();
    }

    public void calculate() throws IOException {
        List<PlayerProjectedPoints> playerPointProjections = playerProjectedPointsService.findByLeague(League.MERGER);
        if (playerPointProjections.size() == 0) {
            projectedPointsCalculator.calculate();
        }
        Optional<Auction> auction = auctionService.findById(DefaultAuctionValuesLoader.AUCTION_ID);
        if (!auction.isPresent()) {
            auctionValuesLoader.load();
        }
        Lineup originalOptimalLineup = lineupOptimizationService.optimize(League.MERGER, 180);
        originalOptimalLineup.prettyPrint();

        List<Player> players = playerService.findAllFetchChildren();
        for (Player player : players) {
            double originalCost = player.getAuctionCost();
            if (originalCost < 2) {
                System.out.println(player.getPlayerName() + ", Cost: $" + player.getAuctionCost() + ", Optimal: $"
                        + player.getPlayerAuctions().get(0).getOptimalValue());
                player.getPlayerAuctions().get(0).setOptimalValue(originalCost);
                playerService.save(player);
                continue;
            }
            double optimalSalary;
            if (isPlayerInOptimalLineup(originalOptimalLineup, player)) {
                optimalSalary = calculateMaxSalaryInOptimalLineup(player, originalCost);
            } else {
                optimalSalary = calculateMinSalaryInOptimalSalary(player, originalCost);
            }
            player.getPlayerAuctions().get(0).setAuctionValue(originalCost);
            player.getPlayerAuctions().get(0).setOptimalValue(optimalSalary);
            System.out.println(player.getPlayerName() + ", Cost: $" + player.getAuctionCost() + ", Optimal: $"
                    + player.getPlayerAuctions().get(0).getOptimalValue());
            playerService.save(player);
        }

    }

    private double calculateMinSalaryInOptimalSalary(Player player, double nonOptimalAuctionCost) {
        player.getPlayerAuctions().get(0).setAuctionValue(nonOptimalAuctionCost - 1);
        Lineup optimalLineup = lineupOptimizationService.optimizeUsingPlayerSalary(League.MERGER, 180, player);
        if (isPlayerInOptimalLineup(optimalLineup, player) || player.getAuctionCost() < 2) {
            return player.getAuctionCost();
        } else {
            return calculateMinSalaryInOptimalSalary(player, nonOptimalAuctionCost - 1);
        }
    }

    private double calculateMaxSalaryInOptimalLineup(Player player, double costBeforeIncrease) {
        player.getPlayerAuctions().get(0).setAuctionValue(costBeforeIncrease + 1);
        Lineup optimalLineup = lineupOptimizationService.optimizeUsingPlayerSalary(League.MERGER, 180, player);
        if (isPlayerInOptimalLineup(optimalLineup, player)) {
            return calculateMaxSalaryInOptimalLineup(player, costBeforeIncrease + 1);
        } else {
            return costBeforeIncrease;
        }
    }

    private boolean isPlayerInOptimalLineup(Lineup optimalLineup, Player player) {
        if (optimalLineup.getQb1().getPlayerId() == player.getPlayerId()) {
            return true;
        }
        if (optimalLineup.getRb1().getPlayerId() == player.getPlayerId()) {
            return true;
        }
        if (optimalLineup.getRb2().getPlayerId() == player.getPlayerId()) {
            return true;
        }
        if (optimalLineup.getWr1().getPlayerId() == player.getPlayerId()) {
            return true;
        }
        if (optimalLineup.getWr2().getPlayerId() == player.getPlayerId()) {
            return true;
        }
        if (optimalLineup.getTe1().getPlayerId() == player.getPlayerId()) {
            return true;
        }
        if (optimalLineup.getFlex().getPlayerId() == player.getPlayerId()) {
            return true;
        }
        return false;
    }

}
