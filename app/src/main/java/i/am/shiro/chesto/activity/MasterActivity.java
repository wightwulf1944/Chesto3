package i.am.shiro.chesto.activity;

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

import java.util.UUID;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.adapter.MasterAdapter;

import static android.content.Intent.ACTION_SEARCH;
import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;

/**
 * Created by Shiro on 12/27/2017.
 * set current index on return
 */

public class MasterActivity extends AppCompatActivity {

    private static int REQUEST_CODE = 0;

    private static String FLOW_ID_EXTRA = "flowId";

    private static String QUERY_EXTRA = "query";

    private long lastTimeBackPressed;

    public static Intent makeIntent(Context context, String query) {
        String flowId = UUID.randomUUID().toString();
        Intent starter = new Intent(context, MasterActivity.class);
        starter.setAction(ACTION_SEARCH);
        starter.putExtra(FLOW_ID_EXTRA, flowId);
        starter.putExtra(QUERY_EXTRA, query);
        return starter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: get search string

        setContentView(R.layout.activity_master);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(viewModel.getQuery());
        toolbar.inflateMenu(R.menu.activity_master);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);

        MasterAdapter adapter = new MasterAdapter(this);
        adapter.setData(viewModel.getPosts());
        adapter.setOnScrollToThresholdListener(15, viewModel::loadPosts);
        adapter.addOnItemClickedListener(this::invokeDetail);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.primaryDark);
        refreshLayout.setRefreshing(viewModel.isLoading());
        refreshLayout.setOnRefreshListener(viewModel::refreshPosts);

        View view = findViewById(android.R.id.content);
        Snackbar errorSnackbar = Snackbar.make(view, "Check your connection", LENGTH_INDEFINITE);
        errorSnackbar.setAction("Retry", v -> viewModel.loadPosts());

        // TODO: subscribe to data changes
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            int i = data.getIntExtra("default", 0);
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.scrollToPosition(i);
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

    private void invokeSearch() {
        Intent intent = SearchActivity.makeIntent(this);
        startActivity(intent);
    }

    private void invokeDetail(int i) {
        Intent intent = DetailActivity.makeIntent(this, i);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private String getSearchString() {
        Intent intent = getIntent();
        String action = getIntent().getAction();

        if (action == null) {
            throw new RuntimeException("No action found for intent: " + intent.toString());
        }

        switch (action) {
            case Intent.ACTION_MAIN:
                return "";
            case ACTION_SEARCH:
                return intent.getStringExtra(QUERY_EXTRA);
            default:
                throw new RuntimeException("Unhandled intent action: " + action);
        }
    }
}
