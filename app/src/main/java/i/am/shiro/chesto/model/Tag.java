package i.am.shiro.chesto.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 7/29/2016.
 */
public class Tag extends RealmObject {

    @PrimaryKey private int id;

    @Index private int postCount;

    private String name;

    public Tag() {
        // no arg constructor required by Realm
    }

    public Tag(TagJson tagJson) {
        id = tagJson.id;
        postCount = tagJson.postCount;
        name = tagJson.name;
    }

    public int getId() {
        return id;
    }

    public int getPostCount() {
        return postCount;
    }

    public String getName() {
        return name;
    }
}
