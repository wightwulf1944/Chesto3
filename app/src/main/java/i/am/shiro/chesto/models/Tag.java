package i.am.shiro.chesto.models;

import com.squareup.moshi.Json;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 7/29/2016.
 */
public class Tag extends RealmObject {

    @PrimaryKey
    @Json(name = "id")
    private int id;
    @Json(name = "name")
    private String name;
    @Json(name = "post_count")
    private int postCount;

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
        if (postCount < 1000) {
            return "(" + postCount + ")";
        } else {
            String postCountStr = String.valueOf(postCount);
            postCountStr = postCountStr.substring(0, postCountStr.length() - 3);
            return "(" + postCountStr + "k)";
        }
    }
}
