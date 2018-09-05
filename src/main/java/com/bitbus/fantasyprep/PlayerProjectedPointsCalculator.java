package com.bitbus.fantasyprep;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.bitbus.fantasyprep.league.League;
import com.bitbus.fantasyprep.player.PlayerProjectedPointsService;
import com.bitbus.fantasyprep.player.PlayerProjection;
import com.bitbus.fantasyprep.player.PlayerProjectionService;

@SpringBootApplication
public class PlayerProjectedPointsCalculator {

    @Autowired
    private PlayerProjectionService playerProjectionService;

    @Autowired
    private PlayerProjectionsLoader playerProjectionsLoader;

    @Autowired
    private PlayerProjectedPointsService pointProjectionService;


    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext ctx = SpringApplication.run(PlayerProjectedPointsCalculator.class, args);
        PlayerProjectedPointsCalculator projectedPointsCalculator = ctx.getBean(PlayerProjectedPointsCalculator.class);
        projectedPointsCalculator.calculate();
    }

    public void calculate() throws IOException {
        List<PlayerProjection> playerProjections = playerProjectionService.findAll();
        if (playerProjections.size() == 0) {
            playerProjectionsLoader.load();
        }
        pointProjectionService.calculateProjectedPoints(League.MERGER);
    }

}
