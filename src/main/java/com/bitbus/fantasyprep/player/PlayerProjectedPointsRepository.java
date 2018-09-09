package com.bitbus.fantasyprep.player;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bitbus.fantasyprep.league.LeagueSettings;

public interface PlayerProjectedPointsRepository extends JpaRepository<PlayerProjectedPoints, Integer> {

    List<PlayerProjectedPoints> findByLeagueSettings(LeagueSettings leagueSettings);

}
