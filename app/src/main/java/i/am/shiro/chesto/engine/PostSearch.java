package i.am.shiro.chesto.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import i.am.shiro.chesto.ChestoApplication;
import i.am.shiro.chesto.models.Post;
import i.am.shiro.chesto.models.PostJson;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 6/13/2017.
 */

public final class PostSearch {

    private final String searchString;
    private final ArrayList<Post> list = new ArrayList<>(100);
    private final SubscriberList subscriberList = new SubscriberList();

    private int currentPage = 1;
    private boolean isLoading;
    private Disposable disposable;


    public PostSearch(String searchString) {
        this.searchString = searchString;
    }

    public SearchSubscriber makeSubscriber() {
        return new SearchSubscriber(subscriberList);
    }

    public String getSearchString() {
        return searchString;
    }

    public Post getPost(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void refresh() {
        disposable.dispose();
        currentPage = 1;
        clear();
        load();
    }

    public void load() {
        if (isLoading) {
            return;
        }

        String baseUrl = "http://safebooru.donmai.us";

        disposable = ChestoApplication.danbooru()
                .getPosts(searchString, currentPage)
                .flatMap(Observable::fromIterable)
                .filter(PostJson::hasImageUrls)
                .map(postJson -> new Post(postJson, baseUrl))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> onLoading(true))
                .doFinally(() -> onLoading(false))
                .subscribe(
                        this::onLoadSuccess,
                        this::onLoadError
                );
    }

    private void onLoading(boolean isLoading) {
        subscriberList.notifyLoading(isLoading);
        this.isLoading = isLoading;
    }

    private void clear() {
        list.clear();
        subscriberList.notifyCleared();
    }

    private void onLoadSuccess(List<Post> newResults) {
        currentPage++;

        int newCapacity = list.size() + newResults.size();
        list.ensureCapacity(newCapacity);

        HashSet<Post> postsSet = new HashSet<>(list);

        for (Post newPost : newResults) {
            if (postsSet.contains(newPost)) {
                int index = list.lastIndexOf(newPost);
                list.set(index, newPost);
                subscriberList.notifyPostUpdated(index);
            } else {
                list.add(newPost);
                subscriberList.notifyPostAdded(list.size());
            }
        }
    }

    private void onLoadError(Throwable throwable) {
        Timber.e(throwable, "Error fetching posts");
        subscriberList.notifyError();
    }
}
