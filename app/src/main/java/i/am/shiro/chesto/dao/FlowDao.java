package i.am.shiro.chesto.dao;

import java.io.Closeable;
import java.util.HashSet;
import java.util.List;

import i.am.shiro.chesto.constant.LoadState;
import i.am.shiro.chesto.model.MasterDetailFlow;
import i.am.shiro.chesto.model.Post;
import io.realm.Realm;
import io.realm.RealmList;

public class FlowDao implements Closeable {

    private final Realm realm = Realm.getDefaultInstance();

    private final Runnable onFlowChangeListener;

    private MasterDetailFlow flow;

    public FlowDao(Runnable onFlowChangeListener) {
        this.onFlowChangeListener = onFlowChangeListener;
    }

    public void newFlow(String flowId, String query) {
        realm.beginTransaction();
        flow = realm.createObject(MasterDetailFlow.class, flowId);
        flow.setQuery(query);
        realm.commitTransaction();

        flow.addChangeListener(realmModel -> onFlowChangeListener.run());
        onFlowChangeListener.run();
    }

    public void loadFlow(String flowId) {
        if (flow != null) return;

        flow = realm.where(MasterDetailFlow.class)
                .equalTo("id", flowId)
                .findFirst();

        if (flow == null) throw new NullPointerException("Could not find flow with id" + flowId);

        flow.addChangeListener(realmModel -> onFlowChangeListener.run());
        onFlowChangeListener.run();
    }

    @Override
    public void close() {
        realm.close();
    }

    public String getFlowId() {
        return flow.getId();
    }

    public String getQuery() {
        return flow.getQuery();
    }

    public List<Post> getPosts() {
        return realm.copyFromRealm(flow.getPosts());
    }

    public int getPostCount() {
        return flow.getPosts().size();
    }

    public int getLoadStatus() {
        return flow.getLoadStatus();
    }

    public int getCurrentPage() {
        return flow.getPagesLoaded();
    }

    public void setCurrentIndex(int position) {
        realm.beginTransaction();
        flow.setCurrentIndex(position);
        realm.commitTransaction();
    }

    public void setLoadStatus(@LoadState int loadStatus) {
        realm.beginTransaction();
        flow.setLoadState(loadStatus);
        realm.commitTransaction();
    }

    public void resetFlow() {
        realm.beginTransaction();
        flow.setPagesLoaded(0);
        flow.getPosts().clear();
        flow.setCurrentIndex(0);
        flow.setLoadState(LoadState.SUCCESS);
        realm.commitTransaction();
    }

    public void notifyPageLoaded(List<Post> newPosts) {
        realm.beginTransaction();

        flow.setPagesLoaded(flow.getPagesLoaded() + 1);
        flow.setLoadState(LoadState.SUCCESS);

        RealmList<Post> posts = flow.getPosts();
        HashSet<Post> postSet = new HashSet<>(posts);
        for (Post newPost : newPosts) {
            if (postSet.contains(newPost)) {
                int index = posts.lastIndexOf(newPost);
                posts.set(index, newPost);
            } else {
                posts.add(newPost);
            }
        }

        realm.commitTransaction();
    }

    public void notifyLoadFailed() {
        realm.beginTransaction();
        flow.setLoadState(LoadState.ERROR);
        realm.commitTransaction();
    }
}
