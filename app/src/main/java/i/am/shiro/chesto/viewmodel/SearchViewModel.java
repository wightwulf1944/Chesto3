package i.am.shiro.chesto.viewmodel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;

import com.annimon.stream.Stream;

import java.util.List;

import i.am.shiro.chesto.TagSuggestionBuilder;
import i.am.shiro.chesto.model.Tag;
import i.am.shiro.chesto.model.TagSuggestion;
import i.am.shiro.chesto.retrofit.Danbooru;
import io.reactivex.disposables.Disposable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

import static com.annimon.stream.Collectors.toList;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.realm.Sort.DESCENDING;

/**
 * Created by Shiro on 12/8/2017.
 */

public class SearchViewModel extends ViewModel {

    private final Realm realm = Realm.getDefaultInstance();

    private final MutableLiveData<List<TagSuggestion>> tagSuggestionsData = new MutableLiveData<>();

    private RealmResults<Tag> managedResults;

    private Disposable disposable;

    private String searchFocus = "";

    public SearchViewModel() {
        managedResults = realm.where(Tag.class)
                .sort("postCount", DESCENDING)
                .findAll();

        updateResults();
    }

    @Override
    protected void onCleared() {
        if (disposable != null) disposable.dispose();
        realm.close();
    }

    public void observeTagSuggestions(LifecycleOwner owner, Observer<List<TagSuggestion>> observer) {
        tagSuggestionsData.observe(owner, observer);
    }

    private void updateResults() {
        TagSuggestionBuilder tagSuggestionBuilder = new TagSuggestionBuilder(searchFocus);

        List<TagSuggestion> tagSuggestions = Stream.of(managedResults)
                .map(realm::copyFromRealm)
                .map(tagSuggestionBuilder::makeFrom)
                .collect(toList());

        tagSuggestionsData.setValue(tagSuggestions);
    }

    public void fetchSuggestions(String focus) {
        searchFocus = focus;

        managedResults = realm.where(Tag.class)
                .contains("name", focus, Case.INSENSITIVE)
                .sort("postCount", DESCENDING)
                .findAll();
        updateResults();

        disposable = Danbooru.API.searchTags('*' + focus + '*')
                .flattenAsObservable(tagJsons -> tagJsons)
                .map(Tag::new)
                .toList()
                .observeOn(mainThread())
                .subscribe(this::onSuccess, this::onError);
    }

    private void onSuccess(List<Tag> results) {
        realm.beginTransaction();
        realm.insertOrUpdate(results);
        realm.commitTransaction();

        updateResults();
    }

    private void onError(Throwable t) {
        Timber.e(t, "failed to fetch tags");
    }
}
