package i.am.shiro.chesto.activitysearch;

import android.support.v7.util.DiffUtil;

import java.util.List;

import i.am.shiro.chesto.models.Tag;

/**
 * Created by Subaru Tashiro on 7/3/2017.
 */

final class TagListDiffer extends DiffUtil.Callback {

    private List<Tag> list1;
    private List<Tag> list2;

    TagListDiffer(List<Tag> list1, List<Tag> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    @Override
    public int getOldListSize() {
        return list1.size();
    }

    @Override
    public int getNewListSize() {
        return list2.size();
    }

    @Override
    public boolean areItemsTheSame(int i1, int i2) {
        Tag oldTag = list1.get(i1);
        Tag newTag = list2.get(i2);
        return oldTag.getId() == newTag.getId();
    }

    @Override
    public boolean areContentsTheSame(int i1, int i2) {
        Tag oldTag = list1.get(i1);
        Tag newTag = list2.get(i2);
        boolean isNameTheSame = oldTag.getName().equals(newTag.getName());
        boolean isPostCountTheSame = oldTag.getPostCount() == newTag.getPostCount();
        return isNameTheSame && isPostCountTheSame;
    }
}
