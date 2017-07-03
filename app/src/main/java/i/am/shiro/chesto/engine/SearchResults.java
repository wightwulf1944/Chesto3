package i.am.shiro.chesto.engine;

import java.util.ArrayList;
import java.util.List;

import i.am.shiro.chesto.models.Post;

/**
 * Created by Subaru Tashiro on 6/13/2017.
 */

public final class SearchResults {

    private final PostSearch parent;
    private final ArrayList<Post> list = new ArrayList<>();

    SearchResults(PostSearch parent) {
        this.parent = parent;
    }

    public Post get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    void clear() {
        list.clear();
        parent.onResultsClearedListener.onEvent();
    }

    void merge(List<Post> newResults) {
        list.ensureCapacity(size() + newResults.size());

        for (Post newPost : newResults) {
            if (!newPost.hasFileUrl()) {
                continue;
            }

            int index = list.indexOf(newPost);
            if (index == -1) {
                list.add(newPost);
                parent.onPostAddedListener.onEvent(size());
            } else {
                list.set(index, newPost);
                parent.onPostUpdatedListener.onEvent(index);
            }
        }
    }
}
