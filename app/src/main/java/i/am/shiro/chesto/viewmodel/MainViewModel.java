package i.am.shiro.chesto.viewmodel;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import i.am.shiro.chesto.ChestoApplication;
import i.am.shiro.chesto.listener.Listener0;
import i.am.shiro.chesto.listener.Listener1;
import i.am.shiro.chesto.model.MainModel;
import i.am.shiro.chesto.model.Post;
import i.am.shiro.chesto.model.PostJson;
import i.am.shiro.chesto.notifier.Notifier0;
import i.am.shiro.chesto.notifier.Notifier1;
import i.am.shiro.chesto.subscription.Subscription;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmList;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by Shiro on 11/21/2017.
 */

public final class MainViewModel {

    private final Notifier1<Post> onCurrentPostChangedNotifier = new Notifier1<>();

    private final Notifier1<Boolean> onLoadingNotifier = new Notifier1<>();

    private final Notifier0 onErrorNotifier = new Notifier0();

    private final Notifier1<Integer> onPostAddedNotifier = new Notifier1<>();

    private final Notifier1<Integer> onPostUpdatedNotifier = new Notifier1<>();

    private final Notifier0 onResultsClearedNotifier = new Notifier0();

    private final String modelId;

    private final String query;

    private final RealmList<Post> posts;

    private int pagesLoaded;

    private int currentIndex;

    private boolean isLoading;

    private int viewState;

    private Disposable disposable;

    public MainViewModel(String query) {
        this.modelId = UUID.randomUUID().toString();
        this.query = query;
        this.posts = new RealmList<>();

        loadPosts();
    }

    public MainViewModel(Realm realm, String modelId) {
        MainModel mainModel = realm.where(MainModel.class)
                .equalTo("id", modelId)
                .findFirst();

        if (mainModel == null) {
            throw new RuntimeException("MainModel with id " + modelId + " does note exist");
        }

        this.modelId = mainModel.getId();
        this.query = mainModel.getQuery();
        this.posts = mainModel.getResults();
        this.pagesLoaded = mainModel.getPagesLoaded();
        this.currentIndex = mainModel.getCurrentIndex();
        this.isLoading = mainModel.isLoading();

        if (isLoading) loadPosts();
    }

    public String saveState(Realm realm) {
        disposable.dispose();

        MainModel mainModel = new MainModel();
        mainModel.setId(modelId);
        mainModel.setQuery(query);
        mainModel.setResults(posts);
        mainModel.setPagesLoaded(pagesLoaded);
        mainModel.setCurrentIndex(currentIndex);

        realm.beginTransaction();
        realm.insertOrUpdate(mainModel);
        realm.commitTransaction();

        return modelId;
    }

    public void loadPosts() {
        if (isLoading) {
            return;
        }
        setLoading(true);

        disposable = ChestoApplication.danbooru()
                .getPosts(query, pagesLoaded + 1)
                .flatMap(Observable::fromIterable)
                .filter(PostJson::hasImageUrls)
                .map(Post::new)
                .toList()
                .observeOn(mainThread())
                .doFinally(() -> setLoading(false))
                .subscribe(this::onLoadSuccess, this::onLoadError);
    }

    private void onLoadSuccess(List<Post> newPosts) {
        pagesLoaded++;

        HashSet<Post> postSet = new HashSet<>(posts);

        for (Post newPost : newPosts) {
            if (postSet.contains(newPost)) {
                int index = posts.lastIndexOf(newPost);
                posts.set(index, newPost);
                onPostUpdatedNotifier.fireEvent(index);
            } else {
                posts.add(newPost);
                onPostAddedNotifier.fireEvent(posts.size());
            }
        }
    }

    private void onLoadError(Throwable throwable) {
        Timber.e(throwable, "Error fetching posts");
        onErrorNotifier.fireEvent();
    }

    public void refreshPosts() {
        disposable.dispose();
        pagesLoaded = 0;
        posts.clear();
        onResultsClearedNotifier.fireEvent();
        loadPosts();
    }

    public String getQuery() {
        return query;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        onCurrentPostChangedNotifier.fireEvent(getCurrentPost());
    }

    public Post getCurrentPost() {
        return posts.get(currentIndex);
    }

    public List<Post> getPosts() {
        return posts;
    }

    public boolean isLoading() {
        return isLoading;
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
        onLoadingNotifier.fireEvent(loading);
    }

    public Subscription addOnCurrentPostChangedListener(Listener1<Post> listener) {
        return onCurrentPostChangedNotifier.addListener(listener);
    }

    public Subscription addOnLoadingListener(Listener1<Boolean> listener) {
        return onLoadingNotifier.addListener(listener);
    }

    public Subscription addOnErrorListener(Listener0 listener) {
        return onErrorNotifier.addListener(listener);
    }

    public Subscription addOnPostAddedListener(Listener1<Integer> listener) {
        return onPostAddedNotifier.addListener(listener);
    }

    public Subscription addOnPostUpdatedListener(Listener1<Integer> listener) {
        return onPostUpdatedNotifier.addListener(listener);
    }

    public Subscription addOnResultsClearedListener(Listener0 listener) {
        return onResultsClearedNotifier.addListener(listener);
    }
}
