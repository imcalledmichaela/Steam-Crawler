/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.preferred.crawler.tutorial.single;

import ai.preferred.crawler.tutorial.EntityCSVStorage;
import ai.preferred.crawler.tutorial.entity.Game;
import ai.preferred.crawler.tutorial.master.GameHandler;
import ai.preferred.venom.Crawler;
import ai.preferred.venom.Handler;
import ai.preferred.venom.Session;
import ai.preferred.venom.fetcher.AsyncFetcher;
import ai.preferred.venom.fetcher.Fetcher;
import ai.preferred.venom.request.Request;
import ai.preferred.venom.request.VRequest;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Ween Jiann Lee
 */
public class SingleCrawler {

    // You can use this to log to console
    static final Session.Key<EntityCSVStorage<String>> STORAGE_KEY = new Session.Key<>();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SingleCrawler.class);

    private static final String URL = "https://store.steampowered.com/";

    public static void main(String[] args) {
        // Start crawler
        try (Crawler crawler = createCrawler(createFetcher()).start()) {
            LOGGER.info("Starting crawler...");

            // pass in URL and handler
            final Request request = new VRequest(URL);
            final Handler handler = new SingleHandler();

            crawler.getScheduler().add(request, handler);
        } catch (Exception e) {
            LOGGER.error("Could not run crawler: ", e);
        }
}

    private static Fetcher createFetcher() {
        // You can look in builder the different things you can add
        return AsyncFetcher.builder()
                .build();
    }

    private static Crawler createCrawler(Fetcher fetcher) {
        // You can look in builder the different things you can add
        return Crawler.builder()
                .setFetcher(fetcher)
                .build();
    }
}
