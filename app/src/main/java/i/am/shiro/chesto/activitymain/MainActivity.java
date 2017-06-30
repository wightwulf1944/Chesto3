package i.am.shiro.chesto.activitymain;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import i.am.shiro.chesto.R;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.engine.SearchHistory;
import i.am.shiro.chesto.engine.SearchResults;

//TODO: implement load more
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;
    private PostSearch postSearch;
    private RatioDelegate delegate;
    private MainAdapter adapter;
    private Snackbar errorSnackbar;
    private long lastTimeBackPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        int spacingPx = (int) (8 * getResources().getDisplayMetrics().density);
        GreedoSpacingItemDecoration spacer = new GreedoSpacingItemDecoration(spacingPx);

        delegate = new RatioDelegate();
        GreedoLayoutManager layoutManager = new GreedoLayoutManager(delegate);
        layoutManager.setMaxRowHeight(300);

        adapter = new MainAdapter();
        adapter.setOnItemClickedListener(this::onAdapterItemClicked);

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(spacer);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        refreshLayout.setColorSchemeResources(R.color.primaryDark);

        errorSnackbar = Snackbar.make(recyclerView, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                .setAction()

        if (savedInstanceState == null) {
            String action = getIntent().getAction();
            switch (action) {
                case Intent.ACTION_MAIN:
                    onEmptyLaunch();
                    break;
                case Intent.ACTION_SEARCH:
                    onSearchLaunch();
                    break;
                default:
                    throw new RuntimeException("Unhandled intent action: " + action);
            }
        } else {
            onRotate();
        }
    }

    private void onEmptyLaunch() {
        postSearch = new PostSearch("");

        bindSearchToView();
        postSearch.goLoad();
        SearchHistory.goForward(postSearch);
    }

    private void onSearchLaunch() {
        postSearch = new PostSearch(getIntent().getDataString());

        bindSearchToView();
        postSearch.goLoad();
        SearchHistory.goForward(postSearch);
    }

    private void onRotate() {
        postSearch = SearchHistory.current();

        bindSearchToView();
    }

    private void bindSearchToView() {
        refreshLayout.setOnRefreshListener(postSearch::refresh);
        postSearch.setOnLoadingListener(refreshLayout::setRefreshing);

        toolbar.setSubtitle(postSearch.getSearchString());

        SearchResults searchResults = postSearch.getSearchResults();
        delegate.setData(searchResults);
        adapter.setData(searchResults);

        searchResults.setOnPostAddedListener(adapter::notifyItemInserted);
        searchResults.setOnPostUpdatedListener(adapter::notifyItemChanged);
        searchResults.setOnResultsClearedListener(adapter::notifyDataSetChanged);
    }

    @Override
    protected void onDestroy() {
        postSearch.setOnLoadingListener(b -> { /* do nothing */ });

        SearchResults searchResults = postSearch.getSearchResults();
        searchResults.setOnPostAddedListener(i -> { /* do nothing */ });
        searchResults.setOnPostUpdatedListener(i -> { /* do nothing */ });
        searchResults.setOnResultsClearedListener(() -> { /* do nothing */ });

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        GreedoLayoutManager layoutManager = (GreedoLayoutManager) recyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        outState.putInt("firstVisibleItemPosition", firstVisibleItemPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int firstVisibleItemPosition = savedInstanceState.getInt("firstVisibleItemPosition");
        GreedoLayoutManager layoutManager = (GreedoLayoutManager) recyclerView.getLayoutManager();
        layoutManager.scrollToPosition(firstVisibleItemPosition);
    }

    private void onAdapterItemClicked(int index) {
        // launch post activity
        Uri dataUri = Uri.parse("contrapposto");
        Intent intent = new Intent(Intent.ACTION_SEARCH, dataUri, this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (SearchHistory.canGoBack()) {
            SearchHistory.goBack();
            super.onBackPressed();
        } else if (System.currentTimeMillis() < lastTimeBackPressed + 1500) {
            super.onBackPressed();
        } else {
            Snackbar.make(recyclerView, R.string.snackbar_confirmexit, Snackbar.LENGTH_SHORT).show();
            lastTimeBackPressed = System.currentTimeMillis();
        }
    }
}
