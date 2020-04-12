package ai.preferred.crawler.tutorial.master;

import ai.preferred.crawler.tutorial.entity.Game;
import ai.preferred.venom.*;
import ai.preferred.venom.fetcher.AsyncFetcher;
import ai.preferred.venom.fetcher.Fetcher;
import ai.preferred.venom.job.FIFOJobQueue;
import ai.preferred.venom.job.Scheduler;
import ai.preferred.venom.request.Request;
import ai.preferred.venom.request.VRequest;
import ai.preferred.venom.response.BaseResponse;
import ai.preferred.venom.response.Response;
import ai.preferred.venom.response.VResponse;
import ai.preferred.venom.utils.InlineExecutorService;
import ai.preferred.venom.validator.EmptyContentValidator;
import ai.preferred.venom.validator.StatusOkValidator;
//import ai.preferred.venom.validator.Validator;
//import jdk.internal.util.xml.impl.Input;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class TutorialCrawler {

    // You can use this to log to console
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TutorialCrawler.class);

    /*
     * After each exercise, you can run the included Junit tests to
     * see whether you have done it right. Most JAVA IDE provide a
     * way to run Junit tests, please consult their respective manual.
     */

    /**
     * Exercise 1: Creating a crawler with default settings.
     * <p>
     * The crawler class is the "brain" behind the fetcher. It
     * checks the queue and tells the fetcher what to crawl, and
     * where should the response be processed. Creating a default
     * crawler will automatically create a default fetcher.
     * </p>
     *
     * @return a new instance of crawler
     */
    public static Crawler createCrawler() {
        // Create a new crawler here
        final Crawler crawler = Crawler.builder().build().start();

        return crawler;
    }

    /**
     * Exercise 2: Creating a fetcher that includes these (3) validators:
     * - EmptyContentValidator,
     * - StatusOkValidator,
     * - TutorialValidator.
     * <p>
     * The fetcher class is like a internet browser, it allows
     * you to fetch a page given a request (URL). You can also constrain
     * the validity of the page by using validators.
     * </p>
     *
     * @return a new instance of fetcher
     */
    public static Fetcher createFetcher() {
        // Create a new fetcher here
        final Fetcher fetcher = AsyncFetcher.builder().setValidator(new TutorialValidator(), new EmptyContentValidator(), new StatusOkValidator()).build();

        return fetcher;
    }

    // Create session keys for things you would like to retrieve in/from handler
    static final Session.Key<List<Game>> GAME_LIST_KEY = new Session.Key<>();

    /**
     * Exercise 3: Creating a session store with PAPER_LIST_KEY.
     * <p>
     * A session store allows you to exchange information/objects between
     * your main method and your handler. As our crawler runs asynchronously,
     * you will not be able to directly pass parameters to or directly
     * return objects from the handler. To overcome this, we will use a session
     * store. We have already created the session key above {@see PAPER_LIST_KEY},
     * your task will be to insert this key into a session.
     * </p>
     *
     * @return a new instance of session
     */
    public static Session createSession(List<Game> games) {
        // Create a session here
        final Session session = Session.builder().put(GAME_LIST_KEY, games).build();

        return session;
    }

    /**
     * Exercise 4: Creating a crawler that uses a specified fetcher and session.
     * <p>
     * The crawler class is the "brain" behind the fetcher. It
     * checks the queue and tells the fetcher what to crawl, and
     * where should the response be processed. To use the fetcher and
     * session you created you should put it into crawler's builder.
     * </p>
     *
     * @return a new instance of fetcher
     */
    public static Crawler createCrawler(Fetcher fetcher, Session session) {
        // Create a new crawler here
        final Crawler crawler = Crawler.builder().setSession(session).setFetcher(fetcher).build().start();

        return crawler;
    }

    /*
     * Looking for Exercise 5?
     * Look in ai.preferred.crawler.tutorial.master.TutorialValidator
     */

    /**
     * Exercise 7: Putting it all together.
     * <p>
     * To run your crawler, you have to put it together into a main
     * method. In this exercise you will be crawling the page
     * {@literal https://preferred.ai/publications/}. Use this space
     * to initialise your crawler and schedule the request. You should use
     * the methods from Exercise 2-4 to help you.
     * </p>
     */

    public static void main(String[] args) throws Exception {
        final List<Game> games = new ArrayList<>();
        String page = "Welcome to Steam - Welcome to Steam.htm.gz";

        InputStream stream = TutorialHandler.class.getResourceAsStream(page);
        System.out.println(stream);
        byte[] content = IOUtils.toByteArray(
                new BufferedInputStream(
                        new GZIPInputStream(stream)
                )
        );

        Session session = createSession(games);
        Fetcher fetcher = createFetcher();

        // try-with block automatically closes the crawler upon completion.
        try (Crawler crawler = createCrawler(fetcher, session)) {
            int statusCode = 200;
            String url = "https://store.steampowered.com/";
            ContentType contentType = ContentType.create("text/html", StandardCharsets.UTF_8);
            Header[] headers = {};
            HttpHost proxy = null;

            Request request = new VRequest(page);
            Response response = new BaseResponse(statusCode, url, content, contentType, headers, proxy);
            Scheduler scheduler = new Scheduler(new FIFOJobQueue());

            Worker worker = new ThreadedWorkerManager(new InlineExecutorService()).getWorker();
            System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
            WebDriver driver = new ChromeDriver();

            VResponse vResponse = new VResponse(response);
            List<String> categoryUrlList = new TutorialHandler().fetchCategoryURL(request, vResponse);

            for (String categoryUrl : categoryUrlList) {

                VResponse pageResponse = crawlUrl(categoryUrl, session, statusCode, contentType, headers, proxy);
                Document pageDoc = pageResponse.getJsoup();
                driver.get(categoryUrl);
                List<WebElement> el = driver.findElements(By.cssSelector("span.paged_items_paging_pagelink"));
                for(int i = 1; i <= 5; i ++) {
                    WebElement nextPage = el.get(i);
                    nextPage.click();
                    driver.get();
                }
            }
        }

        LOGGER.info("You have crawled {} games.", games.size());
        games.forEach(game -> LOGGER.info(game.toString()));
    }

    public static VResponse crawlUrl(String url, Session session, int statusCode, ContentType contentType, Header[] headers, HttpHost proxy)  throws Exception{
        InputStream stream = new URL(url).openStream();
        byte[] content = IOUtils.toByteArray(new BufferedInputStream(stream));
        Request request = new VRequest(url);
        Response response = new BaseResponse(statusCode, url, content, contentType, headers, proxy);
        Scheduler scheduler = new Scheduler(new FIFOJobQueue());
        Worker worker = new ThreadedWorkerManager(new InlineExecutorService()).getWorker();

        VResponse vResponse = new VResponse(response);
        new TutorialHandler().handle(request, vResponse, scheduler, session, worker);

        return vResponse;
    }
    /*
     * To check whether Exercise 7 is done correctly, please run the main
     * method of this class directly. This will crawl our web page in realtime.
     */
}
