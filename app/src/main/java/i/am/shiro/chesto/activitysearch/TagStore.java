package i.am.shiro.chesto.activitysearch;

import java.util.List;

import i.am.shiro.chesto.ChestoApplication;
import i.am.shiro.chesto.models.Tag;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Subaru Tashiro on 7/3/2017.
 */

final class TagStore {

    private final SearchAdapter adapter;
    private RealmResults<Tag> cachedResults;

    TagStore(SearchAdapter adapter) {
        this.adapter = adapter;
    }

    void searchTags(String tagSearchString) {
        if (cachedResults != null) {
            cachedResults.removeAllChangeListeners();
        }

        RealmResults<Tag> newResults = Realm.getDefaultInstance()
                .where(Tag.class)
                .contains("name", tagSearchString, Case.INSENSITIVE)
                .findAllSorted("postCount", Sort.DESCENDING);

        applyToAdapter(newResults);
        
        cachedResults = newResults;

        newResults.addChangeListener(this::applyToAdapter);

        ChestoApplication.danbooru()
                .searchTags('*' + tagSearchString + '*')
                .subscribe(tags -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(tags);
                    realm.commitTransaction();
                });
    }

    synchronized private void applyToAdapter(List<Tag> newResults) {
        adapter.setData(newResults);
        adapter.notifyDataSetChanged();
    }
}
