package ai.preferred.crawler.tutorial.entity;

public class Game {

    private String name;
    private String[] tags;
    private String price;

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String[] getTags() { return tags; }

    public void setTags(String[] tags) { this.tags = tags; }

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price; }

    @Override
    public String toString() {
        String result = "Name: " + name + " Price: " + price + " Tags: [";
        if (tags.length != 0) {
            result += tags[0];
            for (int i = 1; i < tags.length; i++) {
                result += ", " + tags[i];
            }
        }
        return result + "]";
    }
}
