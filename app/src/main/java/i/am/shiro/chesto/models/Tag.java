package i.am.shiro.chesto.models;

import com.squareup.moshi.FromJson;

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

        String number;
        String suffix;
        if (postCount < 1_000) {
            number = String.valueOf(postCount);
            suffix = "";
        } else {
            String tempStr = String.valueOf(postCount);
            char first = tempStr.charAt(0);
            char second = tempStr.charAt(1);
            number = first + "." + second;

            if (postCount < 1_000_000) {
                suffix = "k";
            } else {
                suffix = "m";
            }
        }

        postCountString = "(" + number + suffix + ")";
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

    public static class MoshiAdapter {

        @FromJson
        Tag fromJson(TagJson tagJson) {
            return new Tag(tagJson);
        }
    }
}
