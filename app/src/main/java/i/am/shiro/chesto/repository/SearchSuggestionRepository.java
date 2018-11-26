package i.am.shiro.chesto.repository;

import android.arch.lifecycle.MutableLiveData;

import java.io.Closeable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import i.am.shiro.chesto.extension.EmphasizedSpannableString;
import i.am.shiro.chesto.model.SearchSuggestion;
import i.am.shiro.chesto.model.Tag;
import i.am.shiro.chesto.retrofit.Danbooru;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.realm.Case;
import io.realm.Realm;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.realm.Sort.DESCENDING;

public class SearchSuggestionRepository implements Closeable {

    private final Realm realm = Realm.getDefaultInstance();

    private final MutableLiveData<List<SearchSuggestion>> liveData = new MutableLiveData<>();

    private final Set<Integer> selectedItemIds = new HashSet<>();

    private Disposable disposable = Disposables.disposed();

    private String filter;

    @Override
    public void close() {
        disposable.dispose();
        realm.close();
    }

    public void onSuggestionSelected(SearchSuggestion suggestion) {
        selectedItemIds.remove(suggestion.getId());
    }

    public void onSuggestionUnselected(SearchSuggestion suggestion) {
        selectedItemIds.add(suggestion.getId());
    }

    public void setFilter(String filter) {
        this.filter = filter;

        Disposable cacheDisposable = realm.where(Tag.class)
                .contains("name", filter, Case.INSENSITIVE)
                .sort("postCount", DESCENDING)
                .findAll()
                .asFlowable()
                .take(2)
                .flatMapIterable(tags -> tags)
                .map(this::makeSuggestion)
                .toList()
                .subscribe(liveData::setValue, Timber::e);

        Disposable networkDisposable = Danbooru.API.searchTags('*' + filter + '*')
                .flattenAsObservable(tagJsons -> tagJsons)
                .map(Tag::new)
                .toList()
                .observeOn(mainThread())
                .subscribe(this::saveTags, Timber::e);

        disposable = new CompositeDisposable(cacheDisposable, networkDisposable);
    }

    private void saveTags(List<Tag> tags) {
        realm.beginTransaction();
        realm.insertOrUpdate(tags);
        realm.commitTransaction();
    }

    private SearchSuggestion makeSuggestion(Tag tag) {
        return new SearchSuggestion.Builder()
                .setName(nameFrom(tag.getName()))
                .setPostCount(postCountFrom(tag.getPostCount()))
                .setSelected(selectedItemIds.contains(tag.getId()))
                .build();
    }

    private CharSequence nameFrom(String text) {
        if (filter.isEmpty()) return text;
        return new EmphasizedSpannableString(text, filter);
    }

    private String postCountFrom(int postCount) {
        String postCountStr = String.valueOf(postCount);
        if (postCount < 1_000) {
            return postCountStr;
        } else if (postCount < 10_000) {
            return new StringBuilder(postCountStr)
                    .reverse()
                    .delete(0, 2)
                    .reverse()
                    .insert(1, '.')
                    .append('k')
                    .toString();
        } else if (postCount < 1_000_000) {
            return new StringBuilder(postCountStr)
                    .reverse()
                    .delete(0, 3)
                    .reverse()
                    .append('k')
                    .toString();
        } else if (postCount < 10_000_000) {
            return new StringBuilder(postCountStr)
                    .reverse()
                    .delete(0, 5)
                    .reverse()
                    .insert(1, '.')
                    .append('m')
                    .toString();
        } else {
            return new StringBuilder(postCountStr)
                    .reverse()
                    .delete(0, 6)
                    .reverse()
                    .append('m')
                    .toString();
        }
    }
}
