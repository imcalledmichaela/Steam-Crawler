package ai.preferred.crawler.tutorial.master;

import ai.preferred.crawler.tutorial.entity.Game;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GameParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameParser.class);

    private GameParser() {
        throw new UnsupportedOperationException();
    }

    static List<Game> parseGames(Document document) {
        final Elements games = document.getElementById("NewReleasesRows").getElementsByClass("tab_item");
        final ArrayList<Game> result = new ArrayList<>(games.size());
        for (final Element g : games) {
            result.add(parseGame(g));
        }
        return result;
    }

    private static String textOrNull(Element element) {
        return null == element ? null : element.text();
    }

    private static Integer intOrNull(Element element) {
        if (element == null) {
            return null;
        }
        try {
            return Integer.parseInt(element.text());
        } catch (NumberFormatException e) {
            LOGGER.error("could not parse integer", e);
            return null;
        }
    }

    private static Game parseGame(Element e) {
        final Game game = new Game();

        game.setPrice(textOrNull(e.selectFirst(".discount_final_price")));
        if (game.getPrice().toLowerCase().contains("free")) {
            game.setPrice("S$0");
        }

        game.setName(textOrNull(e.selectFirst(".tab_item_name")));

        Element gameTagsElement = e.selectFirst(".tab_item_top_tags");
        Elements gameTagList = gameTagsElement.getElementsByClass("top_tag");
        int numTags = gameTagList.size();
        String[] gameTags = new String[numTags];
        for (int i = 0; i < numTags; i++) {
            String tagName = textOrNull(gameTagList.get(i));
            gameTags[i] = tagName.replace(',', ' ').trim();
        }
        game.setTags(gameTags);

        return game;
    }

}
