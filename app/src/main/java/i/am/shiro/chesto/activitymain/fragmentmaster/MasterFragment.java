package i.am.shiro.chesto.activitymain.fragmentmaster;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.activitymain.MainActivity;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.engine.SearchSubscriber;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 */

public class MasterFragment extends Fragment {

    private SearchSubscriber searchSubscriber;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        PostSearch postSearch = parentActivity.getPostSearch();
        int currentIndex = parentActivity.getCurrentIndex();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setSubtitle(postSearch.getSearchString());
        parentActivity.setSupportActionBar(toolbar);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(parentActivity);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);

        MasterAdapter adapter = new MasterAdapter();
        adapter.setData(postSearch);
        adapter.setOnItemClickedListener(parentActivity::goToDetail);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.post(() -> recyclerView.scrollToPosition(currentIndex));

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_main, menu);
    }
}