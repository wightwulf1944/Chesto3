package i.am.shiro.chesto.engine;

import i.am.shiro.chesto.ChestoApplication;
import i.am.shiro.chesto.listeners.Listener1;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 6/13/2017.
 */

public final class PostSearch {

    private final String searchString;
    private final SearchResults searchResults;

    private Listener1<Boolean> onLoadingListener;

    private int currentPage = 1;
    private Disposable disposable;

    public PostSearch(String searchString) {
        this.searchString = searchString;
        searchResults = new SearchResults();
    }

    public String getSearchString() {
        return searchString;
    }

    public SearchResults getSearchResults() {
        return searchResults;
    }

    public void refresh() {
        disposable.dispose();
        currentPage = 1;
        searchResults.clear();
        goLoad();
    }

    public void goLoad() {
        disposable = ChestoApplication.danbooru()
                .getPosts(searchString, currentPage)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> onLoadingListener.onEvent(true))
                .doOnTerminate(() -> onLoadingListener.onEvent(false))
                .subscribe(
                        searchResults::merge,
                        this::onLoadError,
                        () -> currentPage++
                );
    }

    private void onLoadError(Throwable throwable) {
        Timber.e(throwable, "Error fetching posts");
        // TODO: notify load error
    }

    public void setOnLoadingListener(Listener1<Boolean> listener) {
        onLoadingListener = listener;
    }
}
