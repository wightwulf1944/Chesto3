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
import i.am.shiro.chesto.engine.Observable;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.engine.SearchHistory;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;
    private PostSearch postSearch;
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

        errorSnackbar = Snackbar.make(recyclerView, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", v -> postSearch.refresh());

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
        postSearch.load();
        SearchHistory.goForward(postSearch);
    }

    private void onSearchLaunch() {
        postSearch = new PostSearch(getIntent().getDataString());
        bindSearchToView();
        postSearch.load();
        SearchHistory.goForward(postSearch);
    }

    private void onRotate() {
        postSearch = SearchHistory.current();
        bindSearchToView();
    }

    private void bindSearchToView() {
        refreshLayout.setOnRefreshListener(postSearch::refresh);
        toolbar.setSubtitle(postSearch.getSearchString());
        adapter.setData(postSearch);

        Observable observable = postSearch.getObservable();
        observable.setOnLoadingListener(refreshLayout::setRefreshing);
        observable.setOnErrorListener(errorSnackbar::show);
        observable.setOnPostAddedListener(adapter::notifyItemInserted);
        observable.setOnPostUpdatedListener(adapter::notifyItemChanged);
        observable.setOnResultsClearedListener(adapter::notifyDataSetChanged);
    }

    @Override
    protected void onDestroy() {
        postSearch.getObservable().unsubscribeAllListeners();
        super.onDestroy();
    }

    private void onAdapterItemClicked(int index) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("default", index);
        startActivity(intent);
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
