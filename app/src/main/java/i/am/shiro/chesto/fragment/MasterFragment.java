package i.am.shiro.chesto.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import i.am.shiro.chesto.activity.MainActivity;
import i.am.shiro.chesto.adapter.MasterAdapter;
import i.am.shiro.chesto.subscription.Subscription;
import i.am.shiro.chesto.viewmodel.MainViewModel;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 * TODO: may benefit from listening to viewModel.currentindexchanged
 */

public class MasterFragment extends Fragment {

    private Subscription subscription;

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
        MainViewModel viewModel = parentActivity.getViewModel();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setSubtitle(viewModel.getQuery());
        parentActivity.setSupportActionBar(toolbar);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(parentActivity);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);

        MasterAdapter adapter = new MasterAdapter(this);
        adapter.setData(viewModel.getPosts());
        adapter.setOnScrollToThresholdListener(15, viewModel::loadPosts);
        adapter.addOnItemClickedListener(viewModel::setCurrentIndex);
        adapter.addOnItemClickedListener(i -> parentActivity.goToDetail());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.post(() -> recyclerView.scrollToPosition(viewModel.getCurrentIndex()));

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.primaryDark);
        refreshLayout.setRefreshing(viewModel.isLoading());
        refreshLayout.setOnRefreshListener(viewModel::refreshPosts);

        subscription = Subscription.from(
                viewModel.addOnLoadingListener(refreshLayout::setRefreshing),
                viewModel.addOnPostAddedListener(adapter::notifyItemInserted),
                viewModel.addOnPostUpdatedListener(adapter::notifyItemChanged),
                viewModel.addOnResultsClearedListener(adapter::notifyDataSetChanged)
        );

        Timber.d("MASTER FRAGMENT CREATED");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscription.unsubscribe();

        Timber.d("MASTER FRAGMENT DESTROYED");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_main, menu);
    }
}
