package i.am.shiro.chesto.activitymain2.fragmentmaster;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.activitymain2.MainActivity2;
import i.am.shiro.chesto.activitysearch.SearchActivity;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.engine.SearchSubscriber;
import timber.log.Timber;

import static butterknife.ButterKnife.findById;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 */

public class MasterFragment extends Fragment {

    private SearchSubscriber searchSubscriber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        MainActivity2 parentActivity = (MainActivity2) getActivity();
        PostSearch postSearch = parentActivity.getPostSearch();

        Toolbar toolbar = findById(view, R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(postSearch.getSearchString());
        toolbar.inflateMenu(R.menu.activity_main);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(parentActivity);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);

        MasterAdapter adapter = new MasterAdapter();
        adapter.setData(postSearch);
        adapter.setOnItemClickedListener(parentActivity::onMasterItemClicked);

        RecyclerView recyclerView = findById(view, R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout refreshLayout = findById(view, R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.primaryDark);
        refreshLayout.setRefreshing(postSearch.isLoading());
        refreshLayout.setOnRefreshListener(postSearch::refresh);

        Snackbar errorSnackbar = Snackbar.make(view, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", v -> postSearch.load());

        searchSubscriber = postSearch.makeSubscriber();
        searchSubscriber.setOnLoadingListener(refreshLayout::setRefreshing);
        searchSubscriber.setOnErrorListener(errorSnackbar::show);
        searchSubscriber.setOnPostAddedListener(adapter::notifyItemInserted);
        searchSubscriber.setOnPostUpdatedListener(adapter::notifyItemChanged);
        searchSubscriber.setOnResultsClearedListener(adapter::notifyDataSetChanged);

        Timber.d("MASTER FRAGMENT CREATED");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchSubscriber.unsubscribe();

        Timber.d("MASTER FRAGMENT DESTROYED");
    }

    private boolean onMenuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}
