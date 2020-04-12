package ai.preferred.crawler.tutorial.master;

import ai.preferred.crawler.tutorial.entity.Game;
import ai.preferred.venom.Session;
import ai.preferred.venom.ThreadedWorkerManager;
import ai.preferred.venom.Worker;
import ai.preferred.venom.job.FIFOJobQueue;
import ai.preferred.venom.job.Scheduler;
import ai.preferred.venom.request.Request;
import ai.preferred.venom.request.VRequest;
import ai.preferred.venom.response.BaseResponse;
import ai.preferred.venom.response.Response;
import ai.preferred.venom.response.VResponse;
import ai.preferred.venom.utils.InlineExecutorService;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class TutorialHandlerTest {

    private Map<String, String> getPapers(Document document) {
        final Map<String, String> games = new HashMap<>();

        for (int i = 2; i < 13; i++) {
            final Elements elements = document.select("#genre_flyout > div > div:nth-child(2) > a:nth-child(" + i + ")");
            for (Element element : elements) {
                final Element aEl = element.selectFirst("a");
                final String url = aEl.attr("abs:href");
                final String name = aEl.text().trim();
                games.put(url, name);
            }
        }

        return games;
    }

    @Test
    public void testEx06Handler() throws IOException {
        final String page = "Welcome to Steam - Welcome to Steam.htm.gz";

        final InputStream stream = getClass().getClassLoader().getResourceAsStream(page);
        System.out.println(stream);
        final byte[] content = IOUtils.toByteArray(
                new BufferedInputStream(
                        new GZIPInputStream(stream)
                )
        );

        final int statusCode = 200;
        final String url = "https://store.steampowered.com/";
        final ContentType contentType = ContentType.create("text/html", StandardCharsets.UTF_8);
        final Header[] headers = {};
        final HttpHost proxy = null;

        final ArrayList<Game> games = new ArrayList<>();

        final Request request = new VRequest(page);
        final Response response = new BaseResponse(statusCode, url, content, contentType, headers, proxy);
        final Scheduler scheduler = new Scheduler(new FIFOJobQueue());
        final Session session = Session.builder().put(TutorialCrawler.GAME_LIST_KEY, games).build();
        final Worker worker = new ThreadedWorkerManager(new InlineExecutorService()).getWorker();

        final VResponse vResponse = new VResponse(response);
        final Map<String, String> papersTest = getPapers(vResponse.getJsoup());

        new TutorialHandler().handle(request, vResponse, scheduler, session, worker);

        Assertions.assertTrue(games.size() >= papersTest.size(),
                "Not all the papers are found! Did you pick the right selector?");

        final Set<String> urls = new HashSet<>();
        for (Game game : games) {
            final String gameUrl = game.getUrl();
//            System.out.println("im here!! \n", +  gameUrl);
            Assertions.assertTrue(papersTest.containsKey(gameUrl),
                    "No such paper URL! Are you sure '" + game.getUrl() + "' is a full url? (Try it on your browser)");

            final String name = papersTest.get(gameUrl);
            Assertions.assertEquals(name, game.getName().trim(),
                    "Paper name different! Are you sure you selected the right text?");
            Assertions.assertEquals(name, game.getName(),
                    "Paper name different! Did you trim whitespaces from your text?");
            urls.add(gameUrl);
        }

        Assertions.assertEquals(papersTest.size(), urls.size(), "Not all the papers are found! There might be duplicates.");
    }

}
