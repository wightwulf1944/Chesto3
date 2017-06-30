package i.am.shiro.chesto.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import i.am.shiro.chesto.listeners.Listener0;
import i.am.shiro.chesto.listeners.Listener1;
import i.am.shiro.chesto.models.Post;

/**
 * Created by Subaru Tashiro on 6/13/2017.
 */

public final class SearchResults {

    private final ArrayList<Post> list = new ArrayList<>();
    private final HashMap<Post, Integer> map = new HashMap<>();

    private Listener1<Integer> onPostAddedListener;
    private Listener1<Integer> onPostUpdatedListener;
    private Listener0 onResultsClearedListener;

    public Post get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    void merge(List<Post> newResults) {
        for (Iterator<Post> iterator = newResults.iterator(); iterator.hasNext(); ) {
            if (!iterator.next().hasFileUrl()) {
                iterator.remove();
            }
        }

        list.ensureCapacity(size() + newResults.size());

        for (Post newResult : newResults) {
            addOrReplace(newResult);
        }
    }

    void clear() {
        list.clear();
        map.clear();
        onResultsClearedListener.onEvent();
    }

    private void addOrReplace(Post post) {
        Integer index = map.get(post);
        if (index == null) {
            add(post);
        } else {
            set(index, post);
        }
    }

    private void add(Post post) {
        map.put(post, list.size());
        list.add(post);
        onPostAddedListener.onEvent(size());
    }

    private void set(int index, Post post) {
        map.remove(post);
        map.put(post, index);
        list.set(index, post);
        onPostUpdatedListener.onEvent(index);
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
