package i.am.shiro.chesto.activitymain;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import butterknife.BindView;
import butterknife.ButterKnife;
import i.am.shiro.chesto.R;
import i.am.shiro.chesto.activitypost.PostActivity;
import i.am.shiro.chesto.activitysearch.SearchActivity;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.engine.SearchHistory;
import i.am.shiro.chesto.engine.SearchSubscriber;

public class MainActivity extends AppCompatActivity {

    private static final int POST_ACTIVITY_REQUEST = 1;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;
    private SearchSubscriber searchSubscriber;
    private MainAdapter adapter;
    private Snackbar errorSnackbar;
    private long lastTimeBackPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);

        adapter = new MainAdapter();
        adapter.setOnItemClickedListener(this::onAdapterItemClicked);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        refreshLayout.setColorSchemeResources(R.color.primaryDark);

        errorSnackbar = Snackbar.make(recyclerView, "Check your connection", Snackbar.LENGTH_INDEFINITE);

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
        PostSearch postSearch = new PostSearch("");
        bindSearchToView(postSearch);
        postSearch.load();
        SearchHistory.goForward(postSearch);
    }

    private void onSearchLaunch() {
        PostSearch postSearch = new PostSearch(getIntent().getDataString());
        bindSearchToView(postSearch);
        postSearch.load();
        SearchHistory.goForward(postSearch);
    }

    private void onRotate() {
        PostSearch postSearch = SearchHistory.current();
        bindSearchToView(postSearch);
    }

    private void bindSearchToView(PostSearch postSearch) {
        refreshLayout.setOnRefreshListener(postSearch::refresh);
        toolbar.setSubtitle(postSearch.getSearchString());
        adapter.setData(postSearch);
        errorSnackbar.setAction("Retry", v -> postSearch.load());

        searchSubscriber = postSearch.makeSubscriber();
        searchSubscriber.setOnLoadingListener(refreshLayout::setRefreshing);
        searchSubscriber.setOnErrorListener(errorSnackbar::show);
        searchSubscriber.setOnPostAddedListener(adapter::notifyItemInserted);
        searchSubscriber.setOnPostUpdatedListener(adapter::notifyItemChanged);
        searchSubscriber.setOnResultsClearedListener(adapter::notifyDataSetChanged);

    }

    @Override
    protected void onDestroy() {
        searchSubscriber.unsubscribe();
        super.onDestroy();
    }

    private void onAdapterItemClicked(int index) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("default", index);
        startActivityForResult(intent, POST_ACTIVITY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != POST_ACTIVITY_REQUEST) {
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        int currentIndex = data.getIntExtra("default", -1);
        recyclerView.scrollToPosition(currentIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (SearchHistory.canGoBack()) {
            SearchHistory.goBack();
            super.onBackPressed();
        } else if (System.currentTimeMillis() < lastTimeBackPressed + 1500) {
            super.onBackPressed();
        } else {
            Snackbar.make(recyclerView, R.string.main_snackbar_exit, Snackbar.LENGTH_SHORT).show();
            lastTimeBackPressed = System.currentTimeMillis();
        }
    }
}
