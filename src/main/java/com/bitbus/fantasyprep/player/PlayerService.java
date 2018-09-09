package com.bitbus.fantasyprep.player;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepo;

    public Player save(Player player) {
        return playerRepo.save(player);
    }

    public List<Player> findAll() {
        return playerRepo.findAll();
    }

    @Transactional
    public List<Player> findAllFetchChildren() {
        List<Player> players = playerRepo.findAll();
        for (Player player : players) {
            player.getPlayerAuctions().size();
            player.getProjection().getPointProjections().size();
        }
        return players;
    }
}
