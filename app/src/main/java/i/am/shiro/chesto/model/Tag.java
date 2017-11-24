package i.am.shiro.chesto.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 7/29/2016.
 */
public class Tag extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String postCountString;

    public Tag() {
        // no arg constructor required by Realm
    }

    public Tag(TagJson tagJson) {
        id = tagJson.id;
        name = tagJson.name;
        postCountString = makePostCountStr(tagJson.postCount);
    }

    private static String makePostCountStr(int postCount) {
        String postCountStr = String.valueOf(postCount);
        if (postCount < 1_000) {
            return postCountStr;
        } else {
            postCountStr = postCountStr.charAt(0) + "." + postCountStr.charAt(1);
            if (postCount < 1_000_000) {
                return postCountStr + "k";
            } else {
                return postCountStr + "m";
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPostCountStr() {
        return postCountString;
    }
}
