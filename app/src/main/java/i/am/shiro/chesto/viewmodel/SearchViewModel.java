package i.am.shiro.chesto.viewmodel;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import i.am.shiro.chesto.ChestoApplication;
import i.am.shiro.chesto.listener.Listener1;
import i.am.shiro.chesto.model.Tag;
import i.am.shiro.chesto.notifier.Notifier1;
import i.am.shiro.chesto.subscription.Subscription;
import io.reactivex.Observable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

import static io.realm.Sort.DESCENDING;

/**
 * Created by Shiro on 12/8/2017.
 */

public class SearchViewModel extends ViewModel {

    private final Notifier1<List<Tag>> onResultsChangedNotifier = new Notifier1<>();

    private final Realm realm = Realm.getDefaultInstance();

    private RealmResults<Tag> results;

    public SearchViewModel() {
        results = realm.where(Tag.class)
                .findAllSorted("postCount", DESCENDING);
    }

    @Override
    protected void onCleared() {
        realm.close();
    }

    public List<Tag> getResults() {
        return results;
    }

    public void searchTags(String focus) {
        results.removeAllChangeListeners();
        results = realm.where(Tag.class)
                .contains("name", focus, Case.INSENSITIVE)
                .findAllSorted("postCount", DESCENDING);
        results.addChangeListener(onResultsChangedNotifier::fireEvent);

        onResultsChangedNotifier.fireEvent(results);

        ChestoApplication.danbooru()
                .searchTags('*' + focus + '*')
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

    public Subscription addOnResultsChangedListener(Listener1<List<Tag>> listener) {
        return onResultsChangedNotifier.addListener(listener);
    }
}
