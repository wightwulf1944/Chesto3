package i.am.shiro.chesto.viewmodel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import i.am.shiro.chesto.constant.LoadState;
import i.am.shiro.chesto.dao.FlowDao;
import i.am.shiro.chesto.model.Post;
import i.am.shiro.chesto.model.PostJson;
import i.am.shiro.chesto.retrofit.Danbooru;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class MasterViewModel extends ViewModel {

    private final MutableLiveData<List<Post>> postsData = new MutableLiveData<>();

    private final MutableLiveData<Integer> loadStatusData = new MutableLiveData<>();

    private final FlowDao flowDao = new FlowDao(this::copyDataFromFlow);

    private Disposable disposable;

    public void newFlow(String flowId, String query) {
        flowDao.newFlow(flowId, query);
        loadPosts();
    }

    public void loadFlow(String flowId) {
        flowDao.loadFlow(flowId);
    }

    public String getFlowId() {
        return flowDao.getFlowId();
    }

    private void copyDataFromFlow() {
        postsData.setValue(flowDao.getPosts());
        loadStatusData.setValue(flowDao.getLoadStatus());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
        flowDao.close();
    }

    public void observeLoadStatus(LifecycleOwner owner, Observer<Integer> observer) {
        loadStatusData.observe(owner, observer);
    }

    public void observePosts(LifecycleOwner owner, Observer<List<Post>> observer) {
        postsData.observe(owner, observer);
    }

    public void onItemBind(int position) {
        if (flowDao.getLoadStatus() == LoadState.LOADING) return;

        if (position + 15 >= flowDao.getPostCount()) {
            loadPosts();
        }
    }

    public void onItemClick(int position) {
        flowDao.setCurrentIndex(position);
    }

    public void onRefresh() {
        flowDao.resetFlow();
        loadPosts();
    }

    public void onRetry() {
        loadPosts();
    }

    private void loadPosts() {
        flowDao.setLoadStatus(LoadState.LOADING);

        String query = flowDao.getQuery();
        int nextPage = flowDao.getCurrentPage() + 1;

        disposable = Danbooru.API.getPosts(query, nextPage)
                .flattenAsObservable(postJsons -> postJsons)
                .filter(PostJson::hasImageUrls)
                .map(Post::new)
                .toList()
                .observeOn(mainThread())
                .subscribe(this::onLoadSuccess, this::onLoadError);
    }

    private void onLoadSuccess(List<Post> newPosts) {
        flowDao.notifyPageLoaded(newPosts);
    }

    private void onLoadError(Throwable t) {
        flowDao.notifyLoadFailed();
        Timber.e(t, "Error fetching posts");
    }
}
