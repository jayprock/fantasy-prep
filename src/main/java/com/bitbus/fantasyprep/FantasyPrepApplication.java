package com.bitbus.fantasyprep;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.bitbus.fantasyprep.team.Team;
import com.bitbus.fantasyprep.team.TeamRepository;

@SpringBootApplication
public class FantasyPrepApplication {

    @Autowired
    private TeamRepository teamRepo;

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(FantasyPrepApplication.class, args);
        FantasyPrepApplication application = ctx.getBean(FantasyPrepApplication.class);
        application.run();
    }

    private void run() {
        List<Team> teams = teamRepo.findAll();
        System.out.println("Total Teams: " + teams.size());
        teams.forEach(team -> System.out.println(team));
    }
}
