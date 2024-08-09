package i.am.shiro.chesto.viewmodel;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.List;

import i.am.shiro.chesto.constant.LoadState;
import i.am.shiro.chesto.dao.FlowDao;
import i.am.shiro.chesto.model.Post;
import i.am.shiro.chesto.model.PostJson;
import i.am.shiro.chesto.retrofit.Danbooru;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class DetailViewModel extends ViewModel {

    private final MutableLiveData<List<Post>> postsData = new MutableLiveData<>();

    private final MutableLiveData<Post> currentPostData = new MutableLiveData<>();

    private final FlowDao flowDao = new FlowDao(this::copyDataFromFlow);

    private Disposable disposable;

    public void loadFlow(String flowId) {
        flowDao.loadFlow(flowId);
    }

    public Post getCurrentPost() {
        return flowDao.getCurrentPost();
    }

    public int getCurrentIndex() {
        return flowDao.getCurrentIndex();
    }

    private void copyDataFromFlow() {
        postsData.setValue(flowDao.getPosts());
        currentPostData.setValue(flowDao.getCurrentPost());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) disposable.dispose();
        flowDao.close();
    }

    public void observePosts(LifecycleOwner owner, Observer<List<Post>> observer) {
        postsData.observe(owner, observer);
    }

    public void observeCurrentPost(LifecycleOwner owner, Observer<Post> observer) {
        currentPostData.observe(owner, observer);
    }

    public void onItemBind(int position) {
        if (flowDao.getLoadStatus() == LoadState.LOADING) return;

        if (position + 15 >= flowDao.getPostCount()) {
            loadPosts();
        }
    }

    public void onScrollToPage(int position) {
        flowDao.setCurrentIndex(position);
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
