package s198.project2.reuse;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import com.firebase.client.Firebase;
import java.util.List;
import java.util.Map;

/**
 * Created by rachelwang1994 on 4/22/15.
 */
public class Item implements Parcelable {

    private String user;
    private String name;
    private String description;
    private List<Double> location;
    private String pictureUrl;
    private String code;
    private List<String> tags;
    private boolean claimed;
    private String key;

    public Item(String user, String name, String description, List<Double> location,
                String pictureUrl, String code, List<String> tags, boolean claimed) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.location = location;
        this.pictureUrl = pictureUrl;
        this.code = code;
        this.tags = tags;
        this.claimed = claimed;
    }

    public Item(Map<String, Object> itemMap, String modelName) {
        // this.user
        this.name = (String) itemMap.get("name");
        this.description = (String) itemMap.get("description");
        this.location = (List<Double>) itemMap.get("location");
        this.pictureUrl = (String) itemMap.get("pictureUrl");
        this.code = (String) itemMap.get("code");
        this.key = modelName;
        // this.tags
//        this.claimed = (boolean) itemMap.get("claimed");
    }

    public Item(Parcel in) {
        this.user = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        // this.location
        this.pictureUrl = in.readString();
        this.code = in.readString();
        // this.tags
        this.claimed = in.readByte() != 0;
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
        //return location;
        List<Double> locationTemp = new ArrayList<>();
        locationTemp.add(42.3598);
        locationTemp.add(-71.0921);
        return locationTemp;
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

    public String getKey() { return key; }

    public void setKey(String key) {this.key = key;}

    public boolean isClaimed() {
        return claimed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user);
        dest.writeString(name);
        dest.writeString(description);
        // location
        dest.writeString(pictureUrl);
        dest.writeString(code);
        dest.writeByte((byte) (claimed ? 1 : 0));
        // tags
        // boolean
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
