package i.am.shiro.chesto.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 11/21/2017.
 */

public class MainModel extends RealmObject {

    @PrimaryKey
    private String id;

    private String query;

    private RealmList<Post> results;

    private int pagesLoaded;

    private int currentIndex;

    private boolean isLoading;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public RealmList<Post> getResults() {
        return results;
    }

    public void setResults(RealmList<Post> results) {
        this.results = results;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
