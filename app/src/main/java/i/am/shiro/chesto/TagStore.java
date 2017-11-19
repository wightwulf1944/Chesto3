package i.am.shiro.chesto;

import i.am.shiro.chesto.adapter.SearchAdapter;
import i.am.shiro.chesto.model.Tag;
import io.reactivex.Observable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Subaru Tashiro on 7/3/2017.
 */

public final class TagStore {

    private final SearchAdapter adapter;
    private RealmResults<Tag> results;

    public TagStore(SearchAdapter adapter) {
        this.adapter = adapter;
        results = Realm.getDefaultInstance()
                .where(Tag.class)
                .findAllSorted("postCount", Sort.DESCENDING);
        adapter.setData(results);
    }

    public void searchTags(String tagSearchString) {

        results.removeAllChangeListeners();
        results = Realm.getDefaultInstance()
                .where(Tag.class)
                .contains("name", tagSearchString, Case.INSENSITIVE)
                .findAllSorted("postCount", Sort.DESCENDING);
        results.addChangeListener(tags -> adapter.notifyDataSetChanged());

        adapter.setData(results);
        adapter.notifyDataSetChanged();

        ChestoApplication.danbooru()
                .searchTags('*' + tagSearchString + '*')
                .flatMap(Observable::fromIterable)
                .map(Tag::new)
                .toList()
                .subscribe(
                        tags -> Realm.getDefaultInstance().executeTransaction(
                                realm -> realm.insertOrUpdate(tags)
                        ),
                        Throwable::printStackTrace
                );
    }
}
