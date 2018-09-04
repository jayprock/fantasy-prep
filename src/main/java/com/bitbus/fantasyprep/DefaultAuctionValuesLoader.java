package com.bitbus.fantasyprep;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.bitbus.fantasyprep.auction.Auction;
import com.bitbus.fantasyprep.auction.AuctionService;
import com.bitbus.fantasyprep.auction.PlayerAuction;
import com.bitbus.fantasyprep.player.Player;
import com.bitbus.fantasyprep.player.PlayerService;
import com.bitbus.fantasyprep.player.Position;
import com.bitbus.fantasyprep.team.TeamAbbreviation;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class DefaultAuctionValuesLoader {

    private static final String AUCTION_CSV = "C:\\Users\\Lisa\\Desktop\\auction.csv";

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerProjectionsLoader playerProjectionsLoader;

    @Autowired
    private AuctionService auctionService;


    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext ctx = SpringApplication.run(DefaultAuctionValuesLoader.class, args);
        DefaultAuctionValuesLoader defaultAuctionValuesLoader = ctx.getBean(DefaultAuctionValuesLoader.class);
        defaultAuctionValuesLoader.load();
    }

    public void load() throws IOException {

        Auction auction = new Auction();
        auction.setDescription("Auction values per Pro Football Focus");
        auction.setCreationTime(LocalDateTime.now());

        List<Player> players = playerService.findAll();
        if (players.size() == 0) {
            log.info("Player projections not loaded yet, running that process first");
            playerProjectionsLoader.load();
            players = playerService.findAll();
        } else {
            log.info("Player projections already loaded, ready to load auction values");
        }

        log.info("Loading the default auction values");
        List<PlayerAuction> playerAuctionValues = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(AUCTION_CSV));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL //
                        .withHeader("Id", "Name", "Team", "Bye", "Position", "Proj Pts", "VoRepl", "VoNext",
                                "Auction $", "PFF Ranking", "Expert Raank", "Projection-Based Ranking", "ADP")
                        .withSkipHeaderRecord());) {
            for (CSVRecord csvRecord : csvParser) {
                PlayerAuction playerAuction = new PlayerAuction();
                String playerName = csvRecord.get(1);
                TeamAbbreviation playerTeam = TeamAbbreviation.valueOf(csvRecord.get(2));
                Position playerPosition = Position.valueOf(csvRecord.get(4).toUpperCase());
                if (playerPosition == Position.K || playerPosition == Position.DST) {
                    continue;
                }
                Player player = players.stream() //
                        .filter(playerPredicate -> playerPredicate.getPlayerName().equals(playerName)) //
                        .filter(playerPredicate -> playerPredicate.getTeam().getAbbreviation() == playerTeam) //
                        .findFirst() //
                        .get();
                playerAuction.setPlayer(player);
                playerAuction.setAuction(auction);
                playerAuction.setAuctionValue(Double.valueOf(csvRecord.get(8)));
                playerAuctionValues.add(playerAuction);
            }
        }
        auction.setPlayerAuctionValues(playerAuctionValues);

        auctionService.create(auction);
        log.info("Done loading the default auction values");

    }

}
