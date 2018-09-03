package com.bitbus.fantasyprep.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepo;

    public Player create(Player player) {
        return playerRepo.save(player);
    }
}
