package com.bitbus.fantasyprep.player;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerProjectionService {

    @Autowired
    private PlayerProjectionRepository playerProjectionRepo;


    public List<PlayerProjection> findAll() {
        return playerProjectionRepo.findAll();
    }
}
