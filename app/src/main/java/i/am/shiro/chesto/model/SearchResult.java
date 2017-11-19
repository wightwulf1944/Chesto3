package i.am.shiro.chesto.model;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Shiro on 11/5/2017.
 */

public class SearchResult extends RealmObject {

    private String id = UUID.randomUUID().toString();

    private String query;

    private int pagesLoaded;

    private RealmList<Post> posts;

    public String getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getPagesLoaded() {
        return pagesLoaded;
    }

    public void setPagesLoaded(int pagesLoaded) {
        this.pagesLoaded = pagesLoaded;
    }

    public RealmList<Post> getPosts() {
        return posts;
    }

}
