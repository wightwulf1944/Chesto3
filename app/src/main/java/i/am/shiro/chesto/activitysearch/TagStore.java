package i.am.shiro.chesto.activitysearch;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

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

    private final RecyclerView.Adapter adapter;
    private RealmResults<Tag> cachedResults;

    TagStore(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        cachedResults = Realm.getDefaultInstance()
                .where(Tag.class)
                .findAllSorted("postCount", Sort.DESCENDING);
        //TODO
//        adapter.setData(cachedResults);
    }

    void searchTags(String tagSearchString) {
        cachedResults.removeAllChangeListeners();

        RealmResults<Tag> newResults = Realm.getDefaultInstance()
                .where(Tag.class)
                .contains("name", tagSearchString, Case.INSENSITIVE)
                .findAllSorted("postCount", Sort.DESCENDING);

        applyToAdapter(newResults);

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

    synchronized private void applyToAdapter(RealmResults<Tag> newResults) {
        TagListDiffer tagListDiffer = new TagListDiffer(cachedResults, newResults);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(tagListDiffer, true);

        cachedResults = newResults;
        //TODO
//        adapter.setData(cachedResults);
        diffResult.dispatchUpdatesTo(adapter);
    }
}
