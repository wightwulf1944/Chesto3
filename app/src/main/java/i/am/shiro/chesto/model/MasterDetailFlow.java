package i.am.shiro.chesto.model;

import i.am.shiro.chesto.constant.LoadState;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 2/11/2018.
 */

public class MasterDetailFlow extends RealmObject {

    @PrimaryKey
    private String id;

    private String query;

    private RealmList<Post> posts;

    private int pagesLoaded;

    private int currentIndex;

    private int loadState;

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

    public RealmList<Post> getPosts() {
        return posts;
    }

    public void setPosts(RealmList<Post> posts) {
        this.posts = posts;
    }

    public int getPagesLoaded() {
        return pagesLoaded;
    }

    public void setPagesLoaded(int pagesLoaded) {
        this.pagesLoaded = pagesLoaded;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    @LoadState
    public int getLoadStatus() {
        return loadState;
    }

    public void setLoadState(@LoadState int loadState) {
        this.loadState = loadState;
    }
}
