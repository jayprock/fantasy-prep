package com.bitbus.fantasyprep;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.bitbus.fantasyprep.player.Player;
import com.bitbus.fantasyprep.player.PlayerProjection;
import com.bitbus.fantasyprep.player.PlayerService;
import com.bitbus.fantasyprep.player.Position;
import com.bitbus.fantasyprep.team.Team;
import com.bitbus.fantasyprep.team.TeamService;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class PlayerProjectionsLoader {

    private static final String PROJECTIONS_CSV = "C:\\Users\\Lisa\\Desktop\\projections-joe.csv";

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;


    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext ctx = SpringApplication.run(PlayerProjectionsLoader.class, args);
        PlayerProjectionsLoader loader = ctx.getBean(PlayerProjectionsLoader.class);
        loader.load();
    }

    public void load() throws IOException {
        List<Team> teams = teamService.findAll();
        log.info("Loaded {} teams", teams.size());
        log.info("About to parse the player projection CSV");
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(PROJECTIONS_CSV));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL//
                        .withHeader("Rank", "Name", "Team", "Position", "Bye", "Games", "Points", "AV", "PassingYards",
                                "PassingTds", "Interceptions", "RushingYards", "RushingTds", "Receptions",
                                "ReceivingYards", "ReceivingTds", "FG", "XP") //
                        .withSkipHeaderRecord());) {
            for (CSVRecord csvRecord : csvParser) {
                Player player = new Player();

                String name = csvRecord.get(1);
                player.setPlayerName(name);

                String teamAbbreviation = csvRecord.get(2);
                Team team = teams.stream() //
                        .filter(tm -> tm.getAbbreviation().toString().equals(teamAbbreviation)) //
                        .findFirst() //
                        .get();
                player.setTeam(team);

                String positionLabel = csvRecord.get(3);
                player.setPosition(Position.valueOf(positionLabel.toUpperCase()));

                PlayerProjection projection = new PlayerProjection();
                projection.setPlayer(player);
                player.setProjection(projection);

                String passingYards = csvRecord.get(8);
                projection.setPassingYards(Double.valueOf(passingYards));

                String passingTds = csvRecord.get(9);
                projection.setPassingTds(Double.valueOf(passingTds));

                String interceptions = csvRecord.get(10);
                projection.setInterceptions(Double.valueOf(interceptions));

                String rushingYards = csvRecord.get(11);
                projection.setRushingYards(Double.valueOf(rushingYards));

                String rushingTds = csvRecord.get(12);
                projection.setRushingTds(Double.valueOf(rushingTds));

                String receptions = csvRecord.get(13);
                projection.setReceptions(Double.valueOf(receptions));

                String receivingYards = csvRecord.get(14);
                projection.setReceivingYards(Double.valueOf(receivingYards));

                String receivingTds = csvRecord.get(15);
                projection.setReceivingTds(Double.valueOf(receivingTds));

                if (player.getPosition() == Position.K || player.getPosition() == Position.DST) {
                    continue;
                }

                playerService.save(player);

            }
        }
        log.info("Done parsing player projections");
    }

}
