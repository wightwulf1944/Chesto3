package i.am.shiro.chesto.engine;

import java.util.ArrayList;
import java.util.List;

import i.am.shiro.chesto.ChestoApplication;
import i.am.shiro.chesto.models.Post;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 6/13/2017.
 */

public final class PostSearch {

    private final String searchString;
    private final ArrayList<Post> list;
    private final Observable observable;

    private int currentPage = 1;
    private boolean isLoading;
    private Disposable disposable;


    public PostSearch(String searchString) {
        this.searchString = searchString;
        list = new ArrayList<>(100);
        observable = new Observable();
    }

    public Observable getObservable() {
        return observable;
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

        disposable = ChestoApplication.danbooru()
                .getPosts(searchString, currentPage)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> onLoading(true))
                .doFinally(() -> onLoading(false))
                .subscribe(
                        this::merge,
                        this::onLoadError,
                        () -> currentPage++
                );
    }

    private void onLoading(boolean isLoading) {
        observable.notifyLoading(isLoading);
        this.isLoading = isLoading;
    }

    private void clear() {
        list.clear();
        observable.notifyCleared();
    }

    private void merge(List<Post> newResults) {
        int newCapacity = list.size() + newResults.size();
        list.ensureCapacity(newCapacity);

        for (Post newPost : newResults) {
            if (!newPost.hasFileUrl()) {
                continue;
            }

            int index = list.indexOf(newPost);
            if (index == -1) {
                list.add(newPost);
                observable.notifyPostAdded(list.size());
            } else {
                list.set(index, newPost);
                observable.notifyPostUpdated(index);
            }
        }
    }

    private void onLoadError(Throwable throwable) {
        Timber.e(throwable, "Error fetching posts");
        observable.notifyError();
    }
}
