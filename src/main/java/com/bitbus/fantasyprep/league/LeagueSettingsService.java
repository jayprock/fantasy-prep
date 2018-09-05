package com.bitbus.fantasyprep.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeagueSettingsService {

    @Autowired
    private LeagueSettingsRepository leagueSettingsRepo;

    public LeagueSettings find(League league) {
        return leagueSettingsRepo.findById(league).get();
    }

}
