package i.am.shiro.chesto.loader;

import java.util.HashSet;
import java.util.List;

import i.am.shiro.chesto.ChestoApplication;
import i.am.shiro.chesto.listener.Listener0;
import i.am.shiro.chesto.listener.Listener1;
import i.am.shiro.chesto.model.Post;
import i.am.shiro.chesto.model.PostJson;
import i.am.shiro.chesto.model.SearchResult;
import i.am.shiro.chesto.notifier.Notifier0;
import i.am.shiro.chesto.notifier.Notifier1;
import i.am.shiro.chesto.subscription.SubscriptionGroup;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmList;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by Shiro on 11/5/2017.
 */

public class DanbooruSearchLoader {

    private SearchResult searchResult;

    private boolean isLoading;

    private Disposable disposable;

    private Notifier1<Boolean> onLoadingNotifier = new Notifier1<>();

    private Notifier0 onErrorNotifier = new Notifier0();

    private Notifier1<Integer> onPostAddedNotifier = new Notifier1<>();

    private Notifier1<Integer> onPostUpdatedNotifier = new Notifier1<>();

    private Notifier0 onResultsClearedNotifier = new Notifier0();

    public DanbooruSearchLoader(SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    public String getSearchQuery() {
        return searchResult.getQuery();
    }

    public int getResultSize() {
        return searchResult.getPosts().size();
    }

    public Post getResult(int i) {
        return searchResult.getPosts().get(i);
    }

    public String getResultId() {
        return searchResult.getId();
    }

    public boolean isLoading() {
        return isLoading;
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
        onLoadingNotifier.notifyListeners(loading);
    }

    public void addOnLoadingListener(SubscriptionGroup group, Listener1<Boolean> listener) {
        onLoadingNotifier.addListener(listener);
        group.add(onLoadingNotifier, listener);
    }

    public void addOnErrorListener(SubscriptionGroup group, Listener0 listener) {
        onErrorNotifier.addListener(listener);
        group.add(onErrorNotifier, listener);
    }

    public void addOnPostAddedListener(SubscriptionGroup group, Listener1<Integer> listener) {
        onPostAddedNotifier.addListener(listener);
        group.add(onPostUpdatedNotifier, listener);
    }

    public void addOnPostUpdatedListener(SubscriptionGroup group, Listener1<Integer> listener) {
        onPostUpdatedNotifier.addListener(listener);
        group.add(onPostUpdatedNotifier, listener);
    }

    public void addOnResultsClearedListener(SubscriptionGroup group, Listener0 listener) {
        onResultsClearedNotifier.addListener(listener);
        group.add(onResultsClearedNotifier, listener);
    }

    public void refresh() {
        disposable.dispose();

        Realm realm = searchResult.getRealm();
        realm.beginTransaction();
        searchResult.setPagesLoaded(0);
        searchResult.getPosts().clear();
        realm.commitTransaction();

        onResultsClearedNotifier.notifyListeners();
        load();
    }

    public void load() {
        if (isLoading) {
            return;
        }
        setLoading(true);

        String query = searchResult.getQuery();
        int page = searchResult.getPagesLoaded() + 1;

        disposable = ChestoApplication.danbooru()
                .getPosts(query, page)
                .flatMap(Observable::fromIterable)
                .filter(PostJson::hasImageUrls)
                .map(Post::new)
                .toList()
                .observeOn(mainThread())
                .doFinally(() -> setLoading(false))
                .subscribe(this::onLoadSuccess, this::onLoadError);
    }

    private void onLoadSuccess(List<Post> newResults) {
        Realm realm = searchResult.getRealm();
        realm.beginTransaction();

        int pagesLoaded = searchResult.getPagesLoaded();
        searchResult.setPagesLoaded(pagesLoaded + 1);

        RealmList<Post> posts = searchResult.getPosts();
        HashSet<Post> postSet = new HashSet<>(posts);

        for (Post newResult : newResults) {
            if (postSet.contains(newResult)) {
                realm.insertOrUpdate(newResult);
                int index = posts.lastIndexOf(newResult);
                onPostUpdatedNotifier.notifyListeners(index);
            } else {
                posts.add(newResult);
                int index = posts.size();
                onPostAddedNotifier.notifyListeners(index);
            }
        }

        realm.commitTransaction();
    }

    private void onLoadError(Throwable throwable) {
        Timber.e(throwable, "Error fetching posts");
        onErrorNotifier.notifyListeners();
    }
}
