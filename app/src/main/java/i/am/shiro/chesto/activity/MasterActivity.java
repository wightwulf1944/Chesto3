package i.am.shiro.chesto.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.adapter.MasterAdapter;
import i.am.shiro.chesto.viewmodel.MasterViewModel;

import static android.content.Intent.ACTION_SEARCH;
import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static i.am.shiro.chesto.constant.LoadState.ERROR;
import static i.am.shiro.chesto.constant.LoadState.LOADING;
import static i.am.shiro.chesto.constant.LoadState.SUCCESS;

/**
 * Created by Shiro on 12/27/2017.
 */

public class MasterActivity extends AppCompatActivity {

    private static final String FLOW_ID_EXTRA = "flowId";

    private static final String QUERY_EXTRA = "query";

    private MasterViewModel viewModel;

    private Snackbar errorSnackbar;

    private long lastTimeBackPressed;

    public static Intent makeIntent(Context context, String query) {
        Intent starter = new Intent(context, MasterActivity.class);
        starter.setAction(ACTION_SEARCH);
        starter.putExtra(QUERY_EXTRA, query);
        return starter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String query = getIntent().getStringExtra(QUERY_EXTRA);

        viewModel = ViewModelProviders.of(this).get(MasterViewModel.class);
        if (savedInstanceState == null) {
            viewModel.newFlow(query);
        } else {
            String flowId = savedInstanceState.getString(FLOW_ID_EXTRA);
            viewModel.loadFlow(flowId);
        }

        setContentView(R.layout.activity_master);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(query);
        toolbar.inflateMenu(R.menu.activity_master);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);

        MasterAdapter adapter = new MasterAdapter(this);
        adapter.setOnItemBindListener(viewModel::onItemBind);
        adapter.setOnItemClickListener(this::onItemClick);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.primaryDark);
        refreshLayout.setOnRefreshListener(viewModel::onRefresh);

        viewModel.observePosts(this, adapter::submitList);
        viewModel.observeLoadStatus(this, loadState -> {
            if (loadState == null) return;
            switch (loadState) {
                case LOADING:
                    refreshLayout.setRefreshing(true);
                    hideErrorSnackbar();
                    break;
                case SUCCESS:
                    refreshLayout.setRefreshing(false);
                    break;
                case ERROR:
                    refreshLayout.setRefreshing(false);
                    showErrorSnackbar();
                    break;
            }
        });
    }

    private void showErrorSnackbar() {
        View view = findViewById(android.R.id.content);
        errorSnackbar = Snackbar.make(view, "Check your connection", LENGTH_INDEFINITE);
        errorSnackbar.setAction("Retry", v -> viewModel.onRetry());
        errorSnackbar.show();
    }

    private void hideErrorSnackbar() {
        if (errorSnackbar != null) errorSnackbar.dismiss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FLOW_ID_EXTRA, viewModel.getFlowId());
    }

    @Override
    public void onBackPressed() {
        if (!isTaskRoot() || System.currentTimeMillis() < lastTimeBackPressed + 1500) {
            super.onBackPressed();
        } else {
            View contentView = findViewById(android.R.id.content);
            Snackbar.make(contentView, R.string.main_snackbar_exit, LENGTH_SHORT).show();
            lastTimeBackPressed = System.currentTimeMillis();
        }
    }

    private boolean onMenuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                invokeSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onItemClick(int position) {
        viewModel.onItemClick(position);
        invokeDetail();
    }

    private void invokeSearch() {
        Intent intent = SearchActivity.makeIntent(this);
        startActivity(intent);
    }

    private void invokeDetail() {
        Intent intent = DetailActivity.makeIntent(this, viewModel.getFlowId());
        startActivity(intent);
    }
}
