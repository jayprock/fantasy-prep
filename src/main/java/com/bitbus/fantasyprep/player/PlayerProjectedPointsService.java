package com.bitbus.fantasyprep.player;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bitbus.fantasyprep.league.League;
import com.bitbus.fantasyprep.league.LeagueSettings;
import com.bitbus.fantasyprep.league.LeagueSettingsService;

@Service
public class PlayerProjectedPointsService {

    @Autowired
    private LeagueSettingsService leagueSettingsService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerProjectedPointsRepository playerProjectedPointsRepo;

    public void calculateProjectedPoints(League league) {
        LeagueSettings leagueSettings = leagueSettingsService.find(league);
        List<Player> players = playerService.findAll();

        for (Player player : players) {
            PlayerProjection projection = player.getProjection();
            double projectedPoints = 0;
            projectedPoints += projection.getPassingYards() / leagueSettings.getPassingYardsPerPoint();
            projectedPoints += projection.getPassingTds() * leagueSettings.getPassingTdPoints();
            projectedPoints += projection.getInterceptions() * leagueSettings.getInterceptionPoints();
            projectedPoints += projection.getRushingYards() / leagueSettings.getRushingYardsPerPoint();
            projectedPoints += projection.getRushingTds() * leagueSettings.getRushingTdPoints();
            projectedPoints += projection.getReceptions() * leagueSettings.getReceptionPoints();
            projectedPoints += projection.getReceivingYards() / leagueSettings.getReceivingYardsPerPoint();
            projectedPoints += projection.getReceivingTds() * leagueSettings.getReceivingTdPoints();
            PlayerProjectedPoints playerProjectedPoints = new PlayerProjectedPoints();
            playerProjectedPoints.setLeagueSettings(leagueSettings);
            playerProjectedPoints.setProjection(projection);
            playerProjectedPoints.setProjectedPoints(projectedPoints);
            playerProjectedPointsRepo.save(playerProjectedPoints);
        }

    }

    public List<PlayerProjectedPoints> findByLeague(League league) {
        LeagueSettings leagueSettings = leagueSettingsService.find(league);
        List<PlayerProjectedPoints> playerPointProjections =
                playerProjectedPointsRepo.findByLeagueSettings(leagueSettings);
        System.out.println("Found " + playerPointProjections.size());
        return playerPointProjections;
    }
}
