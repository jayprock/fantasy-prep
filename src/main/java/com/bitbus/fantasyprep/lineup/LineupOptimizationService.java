package com.bitbus.fantasyprep.lineup;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bitbus.fantasyprep.league.League;
import com.bitbus.fantasyprep.league.LeagueSettings;
import com.bitbus.fantasyprep.league.LeagueSettingsService;
import com.bitbus.fantasyprep.player.Player;
import com.bitbus.fantasyprep.player.PlayerProjectedPoints;
import com.bitbus.fantasyprep.player.PlayerService;

@Service
public class LineupOptimizationService {

    @Autowired
    private LeagueSettingsService leagueSettingsService;

    @Autowired
    private PlayerService playerService;

    @Transactional
    public Lineup optimize(League league, int maxStarterSalary) {
        LeagueSettings settings = leagueSettingsService.find(league);
        System.out.println(settings);

        List<Player> allPlayers = playerService.findAll();
        allPlayers.sort((p1, p2) -> {
            PlayerProjectedPoints projPoints1 = p1.getProjection().getPointProjections().get(0);
            PlayerProjectedPoints projPoints2 = p2.getProjection().getPointProjections().get(0);
            return Double.valueOf(projPoints2.getProjectedPoints())
                    .compareTo(Double.valueOf(projPoints1.getProjectedPoints()));
        });
        List<Player> quarterbacks = new ArrayList<>();
        List<Player> runningBacks = new ArrayList<>();
        List<Player> wideReceivers = new ArrayList<>();
        List<Player> tightEnds = new ArrayList<>();
        for (Player player : allPlayers) {
            switch (player.getPosition()) {
                case QB:
                    quarterbacks.add(player);
                    break;
                case RB:
                    runningBacks.add(player);
                    break;
                case WR:
                    wideReceivers.add(player);
                    break;
                case TE:
                    tightEnds.add(player);
                    break;
                default:
                    break;
            }
        }

        Lineup optimalLineup = new Lineup();
        qbLoop: for (Player qb1 : quarterbacks) {
            Lineup lineup = new Lineup();
            lineup.setQb1(qb1);
            {
                double maxProjectedPointsLeft =
                        getMaxProjectedPoints(runningBacks.get(0), runningBacks.get(1), wideReceivers.get(0),
                                wideReceivers.get(1), tightEnds.get(0), runningBacks.get(2), wideReceivers.get(2));
                if (optimalLineup.getProjectedPoints() >= (lineup.getProjectedPoints() + maxProjectedPointsLeft)) {
                    break qbLoop;
                }
            }
            rb1Loop: for (int rb1Index = 0; rb1Index < runningBacks.size() - 2; rb1Index++) {
                Player rb1 = runningBacks.get(rb1Index);
                lineup.setRb1(rb1);
                lineup.setRb2(null);
                lineup.setWr1(null);
                lineup.setWr2(null);
                lineup.setTe1(null);
                lineup.setFlex(null);
                {
                    double maxProjectedPointsLeft = getMaxProjectedPoints(runningBacks.get(rb1Index + 1),
                            wideReceivers.get(0), wideReceivers.get(1), tightEnds.get(0),
                            runningBacks.get(rb1Index + 2), wideReceivers.get(2));
                    if (optimalLineup.getProjectedPoints() >= (lineup.getProjectedPoints() + maxProjectedPointsLeft)) {
                        break rb1Loop;
                    }
                }
                rb2Loop: for (int rb2Index = rb1Index + 1; rb2Index < runningBacks.size() - 1; rb2Index++) {
                    Player rb2 = runningBacks.get(rb2Index);
                    lineup.setRb2(rb2);
                    lineup.setWr1(null);
                    lineup.setWr2(null);
                    lineup.setTe1(null);
                    lineup.setFlex(null);
                    {
                        double maxProjectedPointsLeft =
                                getMaxProjectedPoints(wideReceivers.get(0), wideReceivers.get(1), tightEnds.get(0),
                                        runningBacks.get(rb2Index + 1), wideReceivers.get(2));
                        if (optimalLineup
                                .getProjectedPoints() >= (lineup.getProjectedPoints() + maxProjectedPointsLeft)) {
                            break rb2Loop;
                        }
                    }
                    wr1Loop: for (int wr1Index = 0; wr1Index < wideReceivers.size() - 2; wr1Index++) {
                        Player wr1 = wideReceivers.get(wr1Index);
                        lineup.setWr1(wr1);
                        lineup.setWr2(null);
                        lineup.setTe1(null);
                        lineup.setFlex(null);
                        if (lineup.getTotalSalary() > maxStarterSalary - 3) {
                            continue wr1Loop;
                        }
                        {
                            double maxProjectedPointsLeft = getMaxProjectedPoints(wideReceivers.get(wr1Index + 1),
                                    tightEnds.get(0), runningBacks.get(rb2Index + 1), wideReceivers.get(wr1Index + 2));
                            if (optimalLineup
                                    .getProjectedPoints() >= (lineup.getProjectedPoints() + maxProjectedPointsLeft)) {
                                break wr1Loop;
                            }
                        }
                        wr2Loop: for (int wr2Index = wr1Index + 1; wr2Index < wideReceivers.size() - 1; wr2Index++) {
                            Player wr2 = wideReceivers.get(wr2Index);
                            lineup.setWr2(wr2);
                            lineup.setTe1(null);
                            lineup.setFlex(null);
                            if (lineup.getTotalSalary() > maxStarterSalary - 2) {
                                continue wr2Loop;
                            }
                            {
                                double maxProjectedPointsLeft = getMaxProjectedPoints(tightEnds.get(0),
                                        runningBacks.get(rb2Index + 1), wideReceivers.get(wr2Index + 1));
                                if (optimalLineup.getProjectedPoints() >= (lineup.getProjectedPoints()
                                        + maxProjectedPointsLeft)) {
                                    break wr2Loop;
                                }
                            }
                            te1Loop: for (int te1Index = 0; te1Index < tightEnds.size(); te1Index++) {
                                Player te1 = tightEnds.get(te1Index);
                                lineup.setTe1(te1);
                                lineup.setFlex(null);
                                if (lineup.getTotalSalary() > maxStarterSalary - 1) {
                                    continue te1Loop;
                                }
                                {
                                    double maxProjectedPointsLeft = getMaxProjectedPoints(
                                            runningBacks.get(rb2Index + 1), wideReceivers.get(wr2Index + 1));
                                    if (optimalLineup.getProjectedPoints() >= (lineup.getProjectedPoints()
                                            + maxProjectedPointsLeft)) {
                                        break te1Loop;
                                    }
                                }
                                flexRbLoop: for (int flexRbIndex = rb2Index + 1; flexRbIndex < runningBacks
                                        .size(); flexRbIndex++) {
                                    Player flex = runningBacks.get(flexRbIndex);
                                    lineup.setFlex(flex);
                                    if (lineup.getTotalSalary() > maxStarterSalary) {
                                        continue flexRbLoop;
                                    }
                                    if (lineup.getProjectedPoints() > optimalLineup.getProjectedPoints()) {
                                        optimalLineup.setQb1(qb1);
                                        optimalLineup.setWr1(wr1);
                                        optimalLineup.setWr2(wr2);
                                        optimalLineup.setRb1(rb1);
                                        optimalLineup.setRb2(rb2);
                                        optimalLineup.setTe1(te1);
                                        optimalLineup.setFlex(flex);
                                        // optimalLineup.prettyPrint();
                                    }
                                    break flexRbLoop;
                                }
                                flexWrLoop: for (int flexWrIndex = wr2Index + 1; flexWrIndex < wideReceivers
                                        .size(); flexWrIndex++) {
                                    Player flex = wideReceivers.get(flexWrIndex);
                                    lineup.setFlex(flex);
                                    if (lineup.getTotalSalary() > maxStarterSalary) {
                                        continue flexWrLoop;
                                    }
                                    if (lineup.getProjectedPoints() > optimalLineup.getProjectedPoints()) {
                                        optimalLineup.setQb1(qb1);
                                        optimalLineup.setWr1(wr1);
                                        optimalLineup.setWr2(wr2);
                                        optimalLineup.setRb1(rb1);
                                        optimalLineup.setRb2(rb2);
                                        optimalLineup.setTe1(te1);
                                        optimalLineup.setFlex(flex);
                                        // optimalLineup.prettyPrint();
                                    }
                                    break flexWrLoop;
                                } // flexWr
                            } // te
                        } // wr2
                    } // wr1
                } // rb2
            } // rb1
        } // qb
        return optimalLineup;
    }

    public Lineup optimizeUsingPlayerSalary(League league, int maxStarterSalary, Player playerWithCustomSalary) {
        List<Player> allPlayers = playerService.findAllFetchChildren();
        for (Player player : allPlayers) {
            if (player.getPlayerId() == playerWithCustomSalary.getPlayerId()) {
                player.getPlayerAuctions().get(0).setAuctionValue(playerWithCustomSalary.getAuctionCost());
            }
        }
        allPlayers.sort((p1, p2) -> {
            PlayerProjectedPoints projPoints1 = p1.getProjection().getPointProjections().get(0);
            PlayerProjectedPoints projPoints2 = p2.getProjection().getPointProjections().get(0);
            return Double.valueOf(projPoints2.getProjectedPoints())
                    .compareTo(Double.valueOf(projPoints1.getProjectedPoints()));
        });
        List<Player> quarterbacks = new ArrayList<>();
        List<Player> runningBacks = new ArrayList<>();
        List<Player> wideReceivers = new ArrayList<>();
        List<Player> tightEnds = new ArrayList<>();
        for (Player player : allPlayers) {
            switch (player.getPosition()) {
                case QB:
                    quarterbacks.add(player);
                    break;
                case RB:
                    runningBacks.add(player);
                    break;
                case WR:
                    wideReceivers.add(player);
                    break;
                case TE:
                    tightEnds.add(player);
                    break;
                default:
                    break;
            }
        }

        Lineup optimalLineup = new Lineup();
        qbLoop: for (Player qb1 : quarterbacks) {
            Lineup lineup = new Lineup();
            lineup.setQb1(qb1);
            {
                double maxProjectedPointsLeft =
                        getMaxProjectedPoints(runningBacks.get(0), runningBacks.get(1), wideReceivers.get(0),
                                wideReceivers.get(1), tightEnds.get(0), runningBacks.get(2), wideReceivers.get(2));
                if (optimalLineup.getProjectedPoints() >= (lineup.getProjectedPoints() + maxProjectedPointsLeft)) {
                    break qbLoop;
                }
            }
            rb1Loop: for (int rb1Index = 0; rb1Index < runningBacks.size() - 2; rb1Index++) {
                Player rb1 = runningBacks.get(rb1Index);
                lineup.setRb1(rb1);
                lineup.setRb2(null);
                lineup.setWr1(null);
                lineup.setWr2(null);
                lineup.setTe1(null);
                lineup.setFlex(null);
                {
                    double maxProjectedPointsLeft = getMaxProjectedPoints(runningBacks.get(rb1Index + 1),
                            wideReceivers.get(0), wideReceivers.get(1), tightEnds.get(0),
                            runningBacks.get(rb1Index + 2), wideReceivers.get(2));
                    if (optimalLineup.getProjectedPoints() >= (lineup.getProjectedPoints() + maxProjectedPointsLeft)) {
                        break rb1Loop;
                    }
                }
                rb2Loop: for (int rb2Index = rb1Index + 1; rb2Index < runningBacks.size() - 1; rb2Index++) {
                    Player rb2 = runningBacks.get(rb2Index);
                    lineup.setRb2(rb2);
                    lineup.setWr1(null);
                    lineup.setWr2(null);
                    lineup.setTe1(null);
                    lineup.setFlex(null);
                    {
                        double maxProjectedPointsLeft =
                                getMaxProjectedPoints(wideReceivers.get(0), wideReceivers.get(1), tightEnds.get(0),
                                        runningBacks.get(rb2Index + 1), wideReceivers.get(2));
                        if (optimalLineup
                                .getProjectedPoints() >= (lineup.getProjectedPoints() + maxProjectedPointsLeft)) {
                            break rb2Loop;
                        }
                    }
                    wr1Loop: for (int wr1Index = 0; wr1Index < wideReceivers.size() - 2; wr1Index++) {
                        Player wr1 = wideReceivers.get(wr1Index);
                        lineup.setWr1(wr1);
                        lineup.setWr2(null);
                        lineup.setTe1(null);
                        lineup.setFlex(null);
                        if (lineup.getTotalSalary() > maxStarterSalary - 3) {
                            continue wr1Loop;
                        }
                        {
                            double maxProjectedPointsLeft = getMaxProjectedPoints(wideReceivers.get(wr1Index + 1),
                                    tightEnds.get(0), runningBacks.get(rb2Index + 1), wideReceivers.get(wr1Index + 2));
                            if (optimalLineup
                                    .getProjectedPoints() >= (lineup.getProjectedPoints() + maxProjectedPointsLeft)) {
                                break wr1Loop;
                            }
                        }
                        wr2Loop: for (int wr2Index = wr1Index + 1; wr2Index < wideReceivers.size() - 1; wr2Index++) {
                            Player wr2 = wideReceivers.get(wr2Index);
                            lineup.setWr2(wr2);
                            lineup.setTe1(null);
                            lineup.setFlex(null);
                            if (lineup.getTotalSalary() > maxStarterSalary - 2) {
                                continue wr2Loop;
                            }
                            {
                                double maxProjectedPointsLeft = getMaxProjectedPoints(tightEnds.get(0),
                                        runningBacks.get(rb2Index + 1), wideReceivers.get(wr2Index + 1));
                                if (optimalLineup.getProjectedPoints() >= (lineup.getProjectedPoints()
                                        + maxProjectedPointsLeft)) {
                                    break wr2Loop;
                                }
                            }
                            te1Loop: for (int te1Index = 0; te1Index < tightEnds.size(); te1Index++) {
                                Player te1 = tightEnds.get(te1Index);
                                lineup.setTe1(te1);
                                lineup.setFlex(null);
                                if (lineup.getTotalSalary() > maxStarterSalary - 1) {
                                    continue te1Loop;
                                }
                                {
                                    double maxProjectedPointsLeft = getMaxProjectedPoints(
                                            runningBacks.get(rb2Index + 1), wideReceivers.get(wr2Index + 1));
                                    if (optimalLineup.getProjectedPoints() >= (lineup.getProjectedPoints()
                                            + maxProjectedPointsLeft)) {
                                        break te1Loop;
                                    }
                                }
                                flexRbLoop: for (int flexRbIndex = rb2Index + 1; flexRbIndex < runningBacks
                                        .size(); flexRbIndex++) {
                                    Player flex = runningBacks.get(flexRbIndex);
                                    lineup.setFlex(flex);
                                    if (lineup.getTotalSalary() > maxStarterSalary) {
                                        continue flexRbLoop;
                                    }
                                    if (lineup.getProjectedPoints() > optimalLineup.getProjectedPoints()) {
                                        optimalLineup.setQb1(qb1);
                                        optimalLineup.setWr1(wr1);
                                        optimalLineup.setWr2(wr2);
                                        optimalLineup.setRb1(rb1);
                                        optimalLineup.setRb2(rb2);
                                        optimalLineup.setTe1(te1);
                                        optimalLineup.setFlex(flex);
                                        // optimalLineup.prettyPrint();
                                    }
                                    break flexRbLoop;
                                }
                                flexWrLoop: for (int flexWrIndex = wr2Index + 1; flexWrIndex < wideReceivers
                                        .size(); flexWrIndex++) {
                                    Player flex = wideReceivers.get(flexWrIndex);
                                    lineup.setFlex(flex);
                                    if (lineup.getTotalSalary() > maxStarterSalary) {
                                        continue flexWrLoop;
                                    }
                                    if (lineup.getProjectedPoints() > optimalLineup.getProjectedPoints()) {
                                        optimalLineup.setQb1(qb1);
                                        optimalLineup.setWr1(wr1);
                                        optimalLineup.setWr2(wr2);
                                        optimalLineup.setRb1(rb1);
                                        optimalLineup.setRb2(rb2);
                                        optimalLineup.setTe1(te1);
                                        optimalLineup.setFlex(flex);
                                        // optimalLineup.prettyPrint();
                                    }
                                    break flexWrLoop;
                                } // flexWr
                            } // te
                        } // wr2
                    } // wr1
                } // rb2
            } // rb1
        } // qb

        return optimalLineup;
    }

    // @Transactional
    // public Lineup optimizeAroundRb(League league, int maxStarterSalary, Player lockedPlayer) {
    // if (lockedPlayer.getPosition() != Position.RB) {
    // throw new RuntimeException("Unexpected lock player position");
    // }
    // List<Player> allPlayers = playerService.findAll();
    // allPlayers.sort((p1, p2) -> {
    // PlayerProjectedPoints projPoints1 = p1.getProjection().getPointProjections().get(0);
    // PlayerProjectedPoints projPoints2 = p2.getProjection().getPointProjections().get(0);
    // return Double.valueOf(projPoints2.getProjectedPoints())
    // .compareTo(Double.valueOf(projPoints1.getProjectedPoints()));
    // });
    // List<Player> quarterbacks = new ArrayList<>();
    // List<Player> runningBacks = new ArrayList<>();
    // List<Player> wideReceivers = new ArrayList<>();
    // List<Player> tightEnds = new ArrayList<>();
    // for (Player player : allPlayers) {
    // switch (player.getPosition()) {
    // case QB:
    // quarterbacks.add(player);
    // break;
    // case RB:
    // runningBacks.add(player);
    // break;
    // case WR:
    // wideReceivers.add(player);
    // break;
    // case TE:
    // tightEnds.add(player);
    // break;
    // default:
    // break;
    // }
    // }
    //
    // Lineup optimalLineup = new Lineup();
    // qbLoop: for (Player qb1 : quarterbacks) {
    // Lineup lineup = new Lineup();
    // lineup.setQb1(qb1);
    // lineup.setRb1(lockedPlayer);
    // {
    // double maxProjectedPointsLeft =
    // getMaxProjectedPoints(runningBacks.get(0), runningBacks.get(1), wideReceivers.get(0),
    // wideReceivers.get(1), tightEnds.get(0), runningBacks.get(2), wideReceivers.get(2));
    // if (optimalLineup.getProjectedPoints() >= (lineup.getProjectedPoints() +
    // maxProjectedPointsLeft)) {
    // break qbLoop;
    // }
    // }
    // rbLoop: for (int rbIndex = 0; rbIndex < runningBacks.size() - 2; rbIndex++) {
    // Player rb2 = runningBacks.get(rbIndex);
    // Player nextRb = runningBacks.get(rbIndex + 1);
    // if (rb2.getPlayerId() == lockedPlayer.getPlayerId()) {
    // continue rbLoop;
    // } else if (nextRb.getPlayerId() == lockedPlayer.getPlayerId()) {
    // nextRb = runningBacks.get(rbIndex + 2);
    // }
    // lineup.setRb2(rb2);
    // lineup.setWr1(null);
    // lineup.setWr2(null);
    // lineup.setTe1(null);
    // lineup.setFlex(null);
    // {
    // double maxProjectedPointsLeft = getMaxProjectedPoints(wideReceivers.get(0),
    // wideReceivers.get(1),
    // tightEnds.get(0), nextRb, wideReceivers.get(2));
    // if (optimalLineup.getProjectedPoints() >= (lineup.getProjectedPoints() +
    // maxProjectedPointsLeft)) {
    // break rbLoop;
    // }
    // }
    // wr1Loop: for (int wr1Index = 0; wr1Index < wideReceivers.size() - 2; wr1Index++) {
    // Player wr1 = wideReceivers.get(wr1Index);
    // lineup.setWr1(wr1);
    // lineup.setWr2(null);
    // lineup.setTe1(null);
    // lineup.setFlex(null);
    // if (lineup.getTotalSalary() > maxStarterSalary - 3) {
    // continue wr1Loop;
    // }
    // {
    // double maxProjectedPointsLeft = getMaxProjectedPoints(wideReceivers.get(wr1Index + 1),
    // tightEnds.get(0), nextRb, wideReceivers.get(wr1Index + 2));
    // if (optimalLineup
    // .getProjectedPoints() >= (lineup.getProjectedPoints() + maxProjectedPointsLeft)) {
    // break wr1Loop;
    // }
    // }
    // wr2Loop: for (int wr2Index = wr1Index + 1; wr2Index < wideReceivers.size() - 1; wr2Index++) {
    // Player wr2 = wideReceivers.get(wr2Index);
    // lineup.setWr2(wr2);
    // lineup.setTe1(null);
    // lineup.setFlex(null);
    // if (lineup.getTotalSalary() > maxStarterSalary - 2) {
    // continue wr2Loop;
    // }
    // {
    // double maxProjectedPointsLeft =
    // getMaxProjectedPoints(tightEnds.get(0), nextRb, wideReceivers.get(wr2Index + 1));
    // if (optimalLineup
    // .getProjectedPoints() >= (lineup.getProjectedPoints() + maxProjectedPointsLeft)) {
    // break wr2Loop;
    // }
    // }
    // te1Loop: for (int te1Index = 0; te1Index < tightEnds.size(); te1Index++) {
    // Player te1 = tightEnds.get(te1Index);
    // lineup.setTe1(te1);
    // lineup.setFlex(null);
    // if (lineup.getTotalSalary() > maxStarterSalary - 1) {
    // continue te1Loop;
    // }
    // {
    // double maxProjectedPointsLeft =
    // getMaxProjectedPoints(nextRb, wideReceivers.get(wr2Index + 1));
    // if (optimalLineup.getProjectedPoints() >= (lineup.getProjectedPoints()
    // + maxProjectedPointsLeft)) {
    // break te1Loop;
    // }
    // }
    // flexRbLoop: for (int flexRbIndex = rbIndex + 1; flexRbIndex < runningBacks
    // .size(); flexRbIndex++) {
    // Player flex = runningBacks.get(flexRbIndex);
    // if (flex.getPlayerId() == lockedPlayer.getPlayerId()) {
    // continue flexRbLoop;
    // }
    // lineup.setFlex(flex);
    // if (lineup.getTotalSalary() > maxStarterSalary) {
    // continue flexRbLoop;
    // }
    // if (lineup.getProjectedPoints() > optimalLineup.getProjectedPoints()) {
    // optimalLineup.setQb1(qb1);
    // optimalLineup.setWr1(wr1);
    // optimalLineup.setWr2(wr2);
    // optimalLineup.setRb1(lockedPlayer);
    // optimalLineup.setRb2(rb2);
    // optimalLineup.setTe1(te1);
    // optimalLineup.setFlex(flex);
    // optimalLineup.prettyPrint();
    // }
    // break flexRbLoop;
    // }
    // }
    // flexWrLoop: for (int flexWrIndex = wr2Index + 1; flexWrIndex < wideReceivers
    // .size(); flexWrIndex++) {
    // Player flex = wideReceivers.get(flexWrIndex);
    // lineup.setFlex(flex);
    // if (lineup.getTotalSalary() > maxStarterSalary) {
    // continue flexWrLoop;
    // }
    // if (lineup.getProjectedPoints() > optimalLineup.getProjectedPoints()) {
    // optimalLineup.setQb1(qb1);
    // optimalLineup.setWr1(wr1);
    // optimalLineup.setWr2(wr2);
    // optimalLineup.setRb1(lockedPlayer);
    // optimalLineup.setRb2(rb2);
    // optimalLineup.setTe1(te1);
    // optimalLineup.setFlex(flex);
    // optimalLineup.prettyPrint();
    // }
    // break flexWrLoop;
    // } // flexWr
    // } // te
    // } // wr2
    // } // wr1
    // } // rb2
    // } // qb
    // return optimalLineup;
    //
    // }

    private double getMaxProjectedPoints(Player... players) {
        double max = 0;
        for (int i = 0; i < players.length - 2; i++) {
            max += players[i].getProjectedPoints();
        }
        max += Math.max(players[players.length - 1].getProjectedPoints(),
                players[players.length - 2].getProjectedPoints());

        return max;
    }

}
