package ai.preferred.crawler.tutorial.master;

import ai.preferred.crawler.tutorial.EntityCSVStorage;
import ai.preferred.crawler.tutorial.entity.Game;
import ai.preferred.venom.Handler;
import ai.preferred.venom.Session;
import ai.preferred.venom.Worker;
import ai.preferred.venom.job.Scheduler;
import ai.preferred.venom.request.Request;
import ai.preferred.venom.request.VRequest;
import ai.preferred.venom.response.VResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class GameHandler implements Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameHandler.class);

    @Override
    public void handle(Request request, VResponse response, Scheduler scheduler, Session session, Worker worker) {
        LOGGER.info("processing: {}", request.getUrl());

        // JSoup
        final Document document = response.getJsoup();

        final List<Game> gameList = GameParser.parseGames(document);

        if (gameList.isEmpty()) {
            LOGGER.info("there is no results on this page, stopping: {}", request.getUrl());
            return;
        }

        // Get the CSV printer we created
        final EntityCSVStorage<Game> storage = session.get(GameCrawler.STORAGE_KEY);

        // Use this wrapper for every IO task, this maintains CPU utilisation to speed up crawling
        worker.executeBlockingIO(() -> {
            for (final Game game : gameList) {
                LOGGER.info("storing game: {} [{}]", game.toString());
                try {
                    storage.append(game);
                } catch (IOException e) {
                    LOGGER.error("Unable to store game.", e);
                }
            }
        });

        // Crawl another page if there's a next page
        final String url = request.getUrl();
        try {
            final URIBuilder builder = new URIBuilder(url);
            int currentPage = 1;
            for (final NameValuePair param : builder.getQueryParams()) {
                if ("page".equals(param.getName())) {
                    currentPage = Integer.parseInt(param.getValue());
                }
            }
            if (currentPage + 1 < 5) {
                builder.setParameter("page", String.valueOf(currentPage + 1));
                final String nextPageUrl = builder.toString();
                // Schedule the next page
                scheduler.add(new VRequest(nextPageUrl), this);
            }
        } catch (URISyntaxException | NumberFormatException e) {
            LOGGER.error("unable to parse url: ", e);
        }
    }
}
