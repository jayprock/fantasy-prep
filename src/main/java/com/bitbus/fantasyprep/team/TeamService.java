package com.bitbus.fantasyprep.team;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepo;

    public List<Team> findAll() {
        return teamRepo.findAll();
    }
}
