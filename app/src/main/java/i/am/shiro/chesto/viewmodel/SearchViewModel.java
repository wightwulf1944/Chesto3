package i.am.shiro.chesto.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import i.am.shiro.chesto.model.Tag;
import i.am.shiro.chesto.retrofit.Danbooru;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

import static io.realm.Sort.DESCENDING;

/**
 * Created by Shiro on 12/8/2017.
 */

public class SearchViewModel extends ViewModel {

    private final Realm realm = Realm.getDefaultInstance();

    private final MutableLiveData<RealmResults<Tag>> results = new MutableLiveData<>();

    public SearchViewModel() {
        RealmResults<Tag> tags = realm.where(Tag.class)
                .sort("postCount", DESCENDING)
                .findAll();

        results.setValue(tags);
    }

    @Override
    protected void onCleared() {
        realm.close();
    }

    public LiveData<RealmResults<Tag>> getResults() {
        return results;
    }

    public void searchTags(String focus) {
        results.getValue().removeAllChangeListeners();
        RealmResults<Tag> cachedTags = realm.where(Tag.class)
                .contains("name", focus, Case.INSENSITIVE)
                .sort("postCount", DESCENDING)
                .findAll();

        cachedTags.addChangeListener(results::setValue);

        results.setValue(cachedTags);

        Danbooru.api.searchTags('*' + focus + '*')
                .flattenAsObservable(tagJsons -> tagJsons)
                .map(Tag::new)
                .toList()
                .subscribe(
                        tags -> {
                            Realm realm = Realm.getDefaultInstance();
                            realm.executeTransaction(r -> r.insertOrUpdate(tags));
                            realm.close();
                        },
                        Throwable::printStackTrace
                );
    }
}
