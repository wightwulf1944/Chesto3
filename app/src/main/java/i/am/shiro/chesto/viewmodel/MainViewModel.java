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
import i.am.shiro.chesto.subscription.SubscriptionGroup;
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

    private final Notifier1<Integer> onCurrentIndexChangedNotifier = new Notifier1<>();

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
    }

    public String saveState(Realm realm) {
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
                onPostUpdatedNotifier.notifyListeners(index);
            } else {
                posts.add(newPost);
                onPostAddedNotifier.notifyListeners(posts.size());
            }
        }
    }

    private void onLoadError(Throwable throwable) {
        Timber.e(throwable, "Error fetching posts");
        onErrorNotifier.notifyListeners();
    }

    public void refreshPosts() {
        disposable.dispose();
        pagesLoaded = 0;
        posts.clear();
        onResultsClearedNotifier.notifyListeners();
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
        onCurrentIndexChangedNotifier.notifyListeners(currentIndex);
        onCurrentPostChangedNotifier.notifyListeners(getCurrentPost());
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
        onLoadingNotifier.notifyListeners(loading);
    }

    public void addOnCurrentIndexChangedListener(SubscriptionGroup subscriptionGroup, Listener1<Integer> listener) {
        onCurrentIndexChangedNotifier.addListener(listener);
        subscriptionGroup.add(onCurrentIndexChangedNotifier, listener);
    }

    public void addOnCurrentPostChangedListener(SubscriptionGroup subscriptionGroup, Listener1<Post> listener) {
        onCurrentPostChangedNotifier.addListener(listener);
        subscriptionGroup.add(onCurrentPostChangedNotifier, listener);
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

}
