package s198.project2.reuse;

import java.util.List;

/**
 * Created by rachelwang1994 on 4/22/15.
 */
public class Item {

    private String user;
    private String name;
    private String description;
    private List<Double> location;
    private String pictureUrl;
    private String code;
    private List<String> tags;

    public Item(String user, String name, String description, List<Double> location,
                String pictureUrl, String code, List<String> tags) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.location = location;
        this.pictureUrl = pictureUrl;
        this.code = code;
        this.tags = tags;
    }

    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Double> getLocation() {
        return location;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getCode() {
        return code;
    }

    public List<String> getTags() {
        return tags;
    }
}
