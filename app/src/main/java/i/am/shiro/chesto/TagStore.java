package i.am.shiro.chesto;

import java.util.List;

import i.am.shiro.chesto.listener.Listener1;
import i.am.shiro.chesto.model.Tag;
import io.reactivex.Observable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

import static io.realm.Sort.DESCENDING;

/**
 * Created by Subaru Tashiro on 7/3/2017.
 */

public final class TagStore {

    private final Realm realm;

    private RealmResults<Tag> results;

    private Listener1<List<Tag>> onDatasetChangedListener;

    public TagStore(Realm realm) {
        this.realm = realm;
        this.results = realm.where(Tag.class)
                .findAllSorted("postCount", DESCENDING);
    }

    public List<Tag> getResults() {
        return results;
    }

    public void searchTags(String tagSearchString) {
        results.removeAllChangeListeners();
        results = realm.where(Tag.class)
                .contains("name", tagSearchString, Case.INSENSITIVE)
                .findAllSorted("postCount", DESCENDING);
        onDatasetChangedListener.onEvent(results);
        results.addChangeListener(onDatasetChangedListener::onEvent);

        ChestoApplication.danbooru()
                .searchTags('*' + tagSearchString + '*')
                .flatMap(Observable::fromIterable)
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

    public void setDatasetChangedListener(Listener1<List<Tag>> listener) {
        onDatasetChangedListener = listener;
    }
}
