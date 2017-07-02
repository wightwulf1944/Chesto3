package i.am.shiro.chesto.engine;

import java.util.ArrayList;
import java.util.List;

import i.am.shiro.chesto.listeners.Listener0;
import i.am.shiro.chesto.listeners.Listener1;
import i.am.shiro.chesto.models.Post;

/**
 * Created by Subaru Tashiro on 6/13/2017.
 */

public final class SearchResults {

    private final ArrayList<Post> list = new ArrayList<>();

    private Listener1<Integer> onPostAddedListener;
    private Listener1<Integer> onPostUpdatedListener;
    private Listener0 onResultsClearedListener;

    public Post get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    void clear() {
        list.clear();
        onResultsClearedListener.onEvent();
    }

    void merge(List<Post> newResults) {
        list.ensureCapacity(size() + newResults.size());

        newResults.stream()
                .filter(Post::hasFileUrl)
                .forEach(this::addOrReplace);
    }

    private void addOrReplace(Post post) {
        int index = list.indexOf(post);
        if (index == -1) {
            list.add(post);
            onPostAddedListener.onEvent(size());
        } else {
            list.set(index, post);
            onPostUpdatedListener.onEvent(index);
        }
    }

    public void setOnPostAddedListener(Listener1<Integer> listener) {
        onPostAddedListener = listener;
    }

    public void setOnPostUpdatedListener(Listener1<Integer> listener) {
        onPostUpdatedListener = listener;
    }

    public void setOnResultsClearedListener(Listener0 listener) {
        onResultsClearedListener = listener;
    }
}
