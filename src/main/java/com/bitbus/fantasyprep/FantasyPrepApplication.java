package com.bitbus.fantasyprep;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FantasyPrepApplication {

    @Autowired
    private PlayerProjectionsLoader playerProjectionsLoader;

    @Autowired
    private DefaultAuctionValuesLoader defaultAuctionValuesLoader;

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext ctx = SpringApplication.run(FantasyPrepApplication.class, args);
        FantasyPrepApplication application = ctx.getBean(FantasyPrepApplication.class);
        application.run();
    }

    private void run() throws IOException {
        playerProjectionsLoader.load();
        defaultAuctionValuesLoader.load();
    }
}
