package i.am.shiro.chesto.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 7/29/2016.
 */
public class Tag extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private int postCount;
    private String postCountString;
    private int category;

    public Tag() {
        // no arg constructor required by Realm
    }

    public Tag(TagJson tagJson) {
        id = tagJson.id;
        name = tagJson.name;
        postCount = tagJson.postCount;
        category = tagJson.category;
        postCountString = "(" + getNumber() + getSuffix() + ")";
    }

    private String getNumber() {
        String s = String.valueOf(postCount);
        if (postCount < 1_000) {
            return s;
        } else {
            return s.charAt(0) + "." + s.charAt(1);
        }
    }

    private String getSuffix() {
        if (postCount < 1_000) {
            return "";
        } else if (postCount < 1_000_000) {
            return "k";
        } else {
            return "m";
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPostCount() {
        return postCount;
    }

    public String getPostCountStr() {
        return postCountString;
    }

    public int getCategory() {
        return category;
    }
}
