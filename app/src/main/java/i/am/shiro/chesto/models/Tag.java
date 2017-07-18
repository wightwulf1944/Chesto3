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

    private Tag(TagJson tagJson) {
        id = tagJson.id;
        name = tagJson.name;
        postCount = tagJson.postCount;
        category = tagJson.category;

        if (postCount < 1000) {
            postCountString = "(" + postCount + ")";
        } else {
            postCountString = String.valueOf(postCount);
            postCountString = postCountString.substring(0, postCountString.length() - 3);
            postCountString = "(" + postCountString + "k)";
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

    public static class MoshiAdapter {

        @FromJson
        Tag fromJson(TagJson tagJson) {
            return new Tag(tagJson);
        }
    }
}
