package ai.preferred.crawler.tutorial.master;

import ai.preferred.crawler.tutorial.entity.Game;
import ai.preferred.venom.Handler;
import ai.preferred.venom.Session;
import ai.preferred.venom.Worker;
import ai.preferred.venom.job.Scheduler;
import ai.preferred.venom.request.Request;
import ai.preferred.venom.response.VResponse;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TutorialHandler implements Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TutorialHandler.class);

    /**
     * Exercise 6: Parsing the response from the crawl.
     * <p>
     * The handle method will be called once the page has been successfully
     * crawled by our fetcher. You should use this space to extract the
     * relevant information you require. For this exercise you are required
     * to extract all paper name and url and use it to create {@code Paper}
     * object. Lastly, add all the {@code Paper} objects into papers array
     * list for the test to pass.
     * </p>
     *
     * @param request   request fetched.
     * @param response  venom response received.
     * @param scheduler scheduler used for this request.
     * @param session   session variables defined when the crawler is initiated.
     * @param worker    provides the ability to run code in a separate thread.
     */
    @Override
    public void handle(Request request, VResponse response, Scheduler scheduler, Session session, Worker worker) {
        // Log when there's activity
        LOGGER.info("Processing {}", request.getUrl());

        // The array list to put your results
        final List<Game> games = session.get(TutorialCrawler.GAME_LIST_KEY);

        // Some vars you may need
        final String html = response.getHtml();
        final Document document = response.getJsoup();
        System.out.println(document.toString());
        Element allContent = document.getElementById("NewReleasesRows");
        Elements listOfGames = allContent.getElementsByClass("tab_item");

        for (Element e : listOfGames) {
            String gameUrl = e.selectFirst("a").attr("href");

            String gamePrice = e.selectFirst(".discount_final_price").text();
            if (gamePrice.toLowerCase().contains("free")) {
                gamePrice = "S$0.00";
            }

            String gameName = e.selectFirst(".tab_item_name").text();

            Element gameTagsElement = e.selectFirst(".tab_item_top_tags");
            Elements gameTagList = gameTagsElement.getElementsByClass("top_tag");
            int numTags = gameTagList.size();
            String[] gameTags = new String[numTags];
            for (int i = 0; i < numTags; i++) {
                String tagName = gameTagList.get(i).text();
                tagName = tagName.replace(',', ' ').trim();
                gameTags[i] = tagName;
            }

            Game game = new Game(gameName, gameUrl, gameTags, gamePrice);
            games.add(game);
        }
    }


    public List<String> fetchCategoryURL(Request request, VResponse response) {
        LOGGER.info("Processing {}", request.getUrl());

        List<String> urlList = new ArrayList<>();
        final String html = response.getHtml();
        final Document document = response.getJsoup();

        // #genre_flyout > div > div:nth-child(2) > div.popup_menu_subheader
        // #genre_flyout > div > div:nth-child(2) > a:nth-child(2)

        for (int i = 2; i < 12; i++) {
            Elements elements = document.select("#genre_flyout > div > div:nth-child(2) > a:nth-child(" + i + ")");
            for (Element e : elements) {
                Element element = e.selectFirst("a");
                String url = element.attr("href");
                urlList.add(url);
            }
        }

        return urlList;
    }
}
