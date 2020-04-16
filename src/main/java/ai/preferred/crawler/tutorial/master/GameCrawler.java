package ai.preferred.crawler.tutorial.master;

import ai.preferred.crawler.tutorial.single.SingleCrawler;
import ai.preferred.venom.Crawler;
import ai.preferred.venom.Session;
import ai.preferred.venom.SleepScheduler;
import ai.preferred.venom.fetcher.AsyncFetcher;
import ai.preferred.venom.fetcher.Fetcher;
import ai.preferred.venom.request.VRequest;
import ai.preferred.venom.response.BaseResponse;
import ai.preferred.venom.response.Response;
import ai.preferred.venom.response.StorageResponse;
import ai.preferred.venom.response.VResponse;
import ai.preferred.venom.storage.StorageRecord;
import ai.preferred.venom.validator.EmptyContentValidator;
import ai.preferred.venom.validator.StatusOkValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.preferred.crawler.tutorial.EntityCSVStorage;
import ai.preferred.crawler.tutorial.entity.Game;

import java.io.*;
import java.net.URL;
import java.util.*;

public class GameCrawler {

    // Create session keys for CSV printer to print from handler
    static final Session.Key<EntityCSVStorage<Game>> STORAGE_KEY = new Session.Key<>();

    // You can use this to log to console
    private static final Logger LOGGER = LoggerFactory.getLogger(GameCrawler.class);

    public static void main(String[] args) {

        // Get file to save to
        final String filename = "data/steam.csv";

        // Start CSV printer
        try (EntityCSVStorage<Game> storage = new EntityCSVStorage<>(filename)) {

            // Let's init the session, this allows us to retrieve the array list in the handler
            final Session session = Session.builder()
                    .put(STORAGE_KEY, storage)
                    .build();

            List<String> categoryURL = new ArrayList<>();
            try (Scanner sc = new Scanner(new FileInputStream("data/categories.csv"))) {
                while(sc.hasNext()) {
                    categoryURL.add(sc.nextLine());
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("File not found");
            }

            // Start crawler
            try (Crawler crawler = createCrawler(createFetcher(), session).start()) {
                LOGGER.info("starting crawler...");
                for (String url : categoryURL) {
                    crawler.getScheduler().add(new VRequest(url), new GameHandler());
                }
            } catch (Exception e) {
                LOGGER.error("Could not run crawler: ", e);
            }

        } catch (IOException e) {
            LOGGER.error("unable to open file: {}, {}", filename, e);
        }
    }

    private static Fetcher createFetcher() {
        return AsyncFetcher.builder()
                .setValidator(
                        new EmptyContentValidator(),
                        new StatusOkValidator(),
                        new GameValidator())
                .build();
    }

    private static Crawler createCrawler(Fetcher fetcher, Session session) {
        return Crawler.builder()
                .setFetcher(fetcher)
                .setSession(session)
                // Just to be polite
                .setSleepScheduler(new SleepScheduler(1500, 3000))
                .build();
    }


}



