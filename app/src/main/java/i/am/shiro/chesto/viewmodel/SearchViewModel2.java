package i.am.shiro.chesto.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import i.am.shiro.chesto.TagSuggestionBuilder;
import i.am.shiro.chesto.model.Tag;
import i.am.shiro.chesto.model.TagSuggestion;
import i.am.shiro.chesto.retrofit.Danbooru;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

import static com.annimon.stream.Collectors.toList;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.realm.Sort.DESCENDING;

public class SearchViewModel2 extends ViewModel {

    private final SearchInputRepository searchInputRepository = new SearchInputRepository();

    private final InputSuggestionsRepository inputSuggestionsRepository = new InputSuggestionsRepository();

    @Override
    protected void onCleared() {
        inputSuggestionsRepository.close();
    }

    public LiveData<List<TagSuggestion>> getInputSuggestionsData() {
        return inputSuggestionsRepository.liveData;
    }

    public LiveData<List<String>> getSearchInputData() {
        return searchInputRepository.liveData;
    }

    public void onSuggestionFilterChanged(String s) {
        inputSuggestionsRepository.setFilter(s);
    }

    public void onInputSuggestionClick(TagSuggestion tagSuggestion) {
        searchInputRepository.addSearchInput(tagSuggestion.getName().toString());
    }

    public void onSearchInputRemoveClick(int position) {
        searchInputRepository.removeSearchInput(position);
    }

    public String getQueryString() {
        return Stream.of(searchInputRepository.searchInputs)
                .map(s -> s.replace(' ', '_'))
                .collect(Collectors.joining(" "));
    }

    private class SearchInputRepository {

        private final List<String> searchInputs = new ArrayList<>();

        private final MutableLiveData<List<String>> liveData = new MutableLiveData<>();

        private void addSearchInput(String input) {
            searchInputs.add(0, input);
            liveData.setValue(searchInputs);
        }

        private void removeSearchInput(int position) {
            searchInputs.remove(position);
            liveData.setValue(searchInputs);
        }
    }

    private class InputSuggestionsRepository implements Closeable {

        private final Realm realm = Realm.getDefaultInstance();

        private final MutableLiveData<List<TagSuggestion>> liveData = new MutableLiveData<>();

        private Disposable disposable = Disposables.disposed();

        private RealmResults<Tag> managedResults;

        private String filter;

        InputSuggestionsRepository() {
            filter = "";

            managedResults = realm.where(Tag.class)
                    .sort("postCount", DESCENDING)
                    .findAll();

            updateSuggestions();
        }

        @Override
        public void close() {
            disposable.dispose();
            realm.close();
        }

        private void setFilter(String filter) {
            this.filter = filter;

            managedResults = realm.where(Tag.class)
                    .contains("name", filter, Case.INSENSITIVE)
                    .sort("postCount", DESCENDING)
                    .findAll();

            updateSuggestions();

            disposable = Danbooru.API.searchTags('*' + filter + '*')
                    .flattenAsObservable(tagJsons -> tagJsons)
                    .map(Tag::new)
                    .toList()
                    .observeOn(mainThread())
                    .subscribe(this::onSuccess, this::onError);
        }

        private void updateSuggestions() {
            TagSuggestionBuilder tagSuggestionBuilder = new TagSuggestionBuilder(filter);

            List<TagSuggestion> tagSuggestions = Stream.of(managedResults)
                    .map(realm::copyFromRealm)
                    .map(tagSuggestionBuilder::makeFrom)
                    .collect(toList());

            liveData.setValue(tagSuggestions);
        }

        private void onSuccess(List<Tag> results) {
            realm.beginTransaction();
            realm.insertOrUpdate(results);
            realm.commitTransaction();

            updateSuggestions();
        }

        private void onError(Throwable t) {
            Timber.e(t, "failed to fetch tags");
        }
    }
}
