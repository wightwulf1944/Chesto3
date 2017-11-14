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
import i.am.shiro.chesto.loader.DanbooruSearchLoader;
import i.am.shiro.chesto.subscription.SubscriptionGroup;
import timber.log.Timber;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.support.design.widget.Snackbar.make;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 */

public class MasterFragment extends Fragment {

    SubscriptionGroup subscriptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        DanbooruSearchLoader searchLoader = parentActivity.getSearchLoader();
        int currentIndex = parentActivity.getCurrentIndex();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setSubtitle(searchLoader.getSearchQuery());
        parentActivity.setSupportActionBar(toolbar);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(parentActivity);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);

        MasterAdapter adapter = new MasterAdapter();
        adapter.setData(searchLoader);
        adapter.setOnItemClickedListener(parentActivity::goToDetail);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.post(() -> recyclerView.scrollToPosition(currentIndex));

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.primaryDark);
        refreshLayout.setRefreshing(searchLoader.isLoading());
        refreshLayout.setOnRefreshListener(searchLoader::refresh);

        Snackbar errorSnackbar = make(view, "Check your connection", LENGTH_INDEFINITE)
                .setAction("Retry", v -> searchLoader.load());

        subscriptions = new SubscriptionGroup();
        searchLoader.addOnLoadingListener(subscriptions, refreshLayout::setRefreshing);
        searchLoader.addOnErrorListener(subscriptions, errorSnackbar::show);
        searchLoader.addOnPostAddedListener(subscriptions, adapter::notifyItemInserted);
        searchLoader.addOnPostUpdatedListener(subscriptions, adapter::notifyItemChanged);
        searchLoader.addOnResultsClearedListener(subscriptions, adapter::notifyDataSetChanged);

        Timber.d("MASTER FRAGMENT CREATED");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscriptions.unsubscribe();

        Timber.d("MASTER FRAGMENT DESTROYED");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_main, menu);
    }
}
