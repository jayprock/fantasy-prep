package com.bitbus.fantasyprep.lineup;

import com.bitbus.fantasyprep.player.Player;

import lombok.Data;

@Data
public class Lineup {

    private Player qb1;
    private Player rb1;
    private Player rb2;
    private Player wr1;
    private Player wr2;
    private Player te1;
    private Player flex;

    public double getProjectedPoints() {
        double qb1Points = qb1 == null ? 0 : qb1.getProjection().getPointProjections().get(0).getProjectedPoints();
        double rb1Points = rb1 == null ? 0 : rb1.getProjection().getPointProjections().get(0).getProjectedPoints();
        double rb2Points = rb2 == null ? 0 : rb2.getProjection().getPointProjections().get(0).getProjectedPoints();
        double wr1Points = wr1 == null ? 0 : wr1.getProjection().getPointProjections().get(0).getProjectedPoints();
        double wr2Points = wr2 == null ? 0 : wr2.getProjection().getPointProjections().get(0).getProjectedPoints();
        double te1Points = te1 == null ? 0 : te1.getProjection().getPointProjections().get(0).getProjectedPoints();
        double flexPoints = flex == null ? 0 : flex.getProjection().getPointProjections().get(0).getProjectedPoints();

        return qb1Points + rb1Points + rb2Points + wr1Points + wr2Points + te1Points + flexPoints;
    }

    public double getTotalSalary() {
        double qb1Dollars = qb1 == null ? 0 : qb1.getPlayerAuctions().get(0).getAuctionValue();
        double rb1Dollars = rb1 == null ? 0 : rb1.getPlayerAuctions().get(0).getAuctionValue();
        double rb2Dollars = rb2 == null ? 0 : rb2.getPlayerAuctions().get(0).getAuctionValue();
        double wr1Dollars = wr1 == null ? 0 : wr1.getPlayerAuctions().get(0).getAuctionValue();
        double wr2Dollars = wr2 == null ? 0 : wr2.getPlayerAuctions().get(0).getAuctionValue();
        double te1Dollars = te1 == null ? 0 : te1.getPlayerAuctions().get(0).getAuctionValue();
        double flexDollars = flex == null ? 0 : flex.getPlayerAuctions().get(0).getAuctionValue();

        return qb1Dollars + rb1Dollars + rb2Dollars + wr1Dollars + wr2Dollars + te1Dollars + flexDollars;
    }

    public void prettyPrint() {
        System.out.println();
        String separator = "------------------------------------------";
        System.out.println(separator);
        System.out.println("Optimal Lineup, Total Cost: $" + getTotalSalary());
        System.out.println(separator);
        System.out.println("QB: " + qb1.getPlayerName() + ", Points: " + qb1.getProjectedPoints() + ", Cost: $"
                + qb1.getAuctionCost());
        System.out.println("WR1: " + wr1.getPlayerName() + ", Points: " + wr1.getProjectedPoints() + ", Cost: $"
                + wr1.getAuctionCost());
        System.out.println("WR2: " + wr2.getPlayerName() + ", Points: " + wr2.getProjectedPoints() + ", Cost: $"
                + wr2.getAuctionCost());
        System.out.println("RB1: " + rb1.getPlayerName() + ", Points: " + rb1.getProjectedPoints() + ", Cost: $"
                + rb1.getAuctionCost());
        System.out.println("RB2: " + rb2.getPlayerName() + ", Points: " + rb2.getProjectedPoints() + ", Cost: $"
                + rb2.getAuctionCost());
        System.out.println("TE: " + te1.getPlayerName() + ", Points: " + te1.getProjectedPoints() + ", Cost: $"
                + te1.getAuctionCost());
        System.out.println("FLEX: " + flex.getPlayerName() + ", Points: " + flex.getProjectedPoints() + ", Cost: $"
                + flex.getAuctionCost());
        System.out.println(separator);
        System.out.println("Total Points: " + getProjectedPoints());
        System.out.println(separator);
        System.out.println();
    }
}
