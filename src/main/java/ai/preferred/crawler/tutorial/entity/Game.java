package ai.preferred.crawler.tutorial.entity;

public class Game {

    private final String name;
    private final String url;
    private final String[] tags;
    private final String price;

    public Game(String name, String url, String[] tags, String price) {
        this.name = name;
        this.url = url;
        this.tags = tags;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String[] getTags() {
        return tags;
    }

    public String getPrice() {
        return price;
    }

    public String toString() {
        String result = "Name: " + name + " Price: " + price + " URL: " + url + " Tags: [";
        if (tags.length != 0) {
            result += tags[0];
            for (int i = 1; i < tags.length; i++) {
                result += ", " + tags[i];
            }
        }
        return result + "]";
    }
}
