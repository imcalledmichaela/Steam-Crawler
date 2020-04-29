package ai.preferred.crawler.tutorial.master;

import ai.preferred.crawler.tutorial.entity.Game;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameParser.class);

    private GameParser() {
        throw new UnsupportedOperationException();
    }

    static List<Game> parseGames(Document document) {
        ArrayList<Game> result = new ArrayList<>();

        String[] gameHtml = document.html().split("href");
        for (String s : gameHtml) {
            Game store = parseGame(s);
            if (store != null) {
                result.add(store);
            }

        }
        return result;
    }

    private static String textOrNull(String string) {
        return null == string ? null : string;
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

    /*
    String[] gameHtml = docHtml.split("href");

            for (int i = 0; i < gameHtml.length; i++) {
                if (gameHtml[i].contains("tab_item_name")) {
                    String game = gameHtml[i];
                    String name = game.substring(game.indexOf("tab_item_name"), game.indexOf("tab_item_details"));
                    name = name.replace("tab_item_name\\&quot;\">", "")
                            .replace("&lt;\\/div&gt;\\r\\n\\t\\t\\t", "")
                            .replace("<div class=\"\\&quot;", "")
                            .trim();
                    System.out.println(name);

                    String stringTags = game.substring(game.indexOf("<span class=\"\\&quot;top_tag\\&quot;\">"), game.indexOf("<div style=\"\\&quot;clear:\" both;\\\">"));
                    stringTags = stringTags.replace("<span class=\"\\&quot;top_tag\\&quot;\"", "").trim();
                    String[] tags = stringTags.split(">");
                    for (int j = 0; j < tags.length; j++) {
                        tags[j] = tags[j].replace("&lt;\\/span&gt;", "")
                                .replace(",", "")
                                .replace("&lt;\\/div&gt;\\r\\n\\t\\t\\t&lt;\\/div&gt;\\r\\n\\t\\t&lt;\\/div&gt;\\r\\n\\t\\t", "")
                                .trim();
                        System.out.println(tags[j]);
                    }

                    String price = game.substring(game.indexOf("<div class=\"\\&quot;discount_final_price\\&quot;\">"), game.indexOf("&lt;\\/div&gt;&lt;\\/div&gt;&lt;\\/div&gt;\\t\\t"));
                    price = price.replace("<div class=\"\\&quot;discount_final_price\\&quot;\">", "").replace("&lt;\\/div&gt;&lt;\\/div&gt;&lt;\\/div&gt;\\t\\t", "").trim();
                    if (price.contains("Free to Play")) {
                        price = "S$0.00";
                    }
                    System.out.println(price);
                }


            }
     */
    private static Game parseGame(String s) {
        final Game game = new Game();

//        game.setPrice(textOrNull(e.selectFirst(".discount_final_price")));
//        if (game.getPrice().toLowerCase().contains("free")) {
//            game.setPrice("S$0");
//        }
//
//        game.setName(textOrNull(e.selectFirst(".tab_item_name")));
//
//        Element gameTagsElement = e.selectFirst(".tab_item_top_tags");
//        Elements gameTagList = gameTagsElement.getElementsByClass("top_tag");
//        int numTags = gameTagList.size();
//        String[] gameTags = new String[numTags];
//        for (int i = 0; i < numTags; i++) {
//            String tagName = textOrNull(gameTagList.get(i));
//            gameTags[i] = tagName.replace(',', ' ').trim();
//        }
//        game.setTags(gameTags);
//
//        return game;

        if (!s.contains("tab_item_name")) {
            return null;
        }

        String startInd = "tab_item_name\\&quot;\">";
        String endInd = "tab_item_details";
        String name = s.substring(s.indexOf(startInd) + startInd.length(), s.indexOf(endInd));
        name = name.replace("&lt;\\/div&gt;\\r\\n\\t\\t\\t", "")
                .replace("amp;", "")
                .replace("<div class=\"\\&quot;", "");
        game.setName(textOrNull(name.trim()));

        startInd = "<span class=\"\\&quot;top_tag\\&quot;\">";
        endInd = "<div style=\"\\&quot;clear:\" both;\\\">";
        String stringTags = s.substring(s.indexOf(startInd) + startInd.length(), s.indexOf(endInd));
        stringTags = stringTags.replace("<span class=\"\\&quot;top_tag\\&quot;\"", "").trim();
        String[] tags = stringTags.split(">");
        for (int j = 0; j < tags.length; j++) {
            tags[j] = tags[j].replace("&lt;\\/span&gt;", "")
                    .replace(",", "")
                    .replace("&lt;\\/div&gt;\\r\\n\\t\\t\\t&lt;\\/div&gt;\\r\\n\\t\\t&lt;\\/div&gt;\\r\\n\\t\\t", "")
                    .trim();
        }
        game.setTags(tags);

        startInd = "<div class=\"\\&quot;discount_final_price\\&quot;\">";
        endInd = "&lt;\\/div&gt;&lt;\\/div&gt;&lt;\\/div&gt;\\t\\t";
        String price = s.substring(s.indexOf(startInd) + startInd.length(), s.indexOf(endInd));
//        price = price.replace("<div class=\"\\&quot;discount_final_price\\&quot;\">", "").replace("&lt;\\/div&gt;&lt;\\/div&gt;&lt;\\/div&gt;\\t\\t", "").trim();
        if (price.toUpperCase().contains("FREE")) {
            price = "S$0.00";
        }
        game.setPrice(price.trim());

        return game;
    }
}
