package i.am.shiro.chesto.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import i.am.shiro.chesto.activity.MainActivity;
import i.am.shiro.chesto.activity.SearchActivity;
import i.am.shiro.chesto.adapter.MasterAdapter;
import i.am.shiro.chesto.subscription.Subscription;
import i.am.shiro.chesto.viewmodel.MainViewModel;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 * TODO: may benefit from listening to viewModel.currentindexchanged
 */

public class MasterFragment extends Fragment {

    private MainViewModel viewModel;

    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        viewModel = parentActivity.getViewModel();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(viewModel.getQuery());
        toolbar.inflateMenu(R.menu.activity_main);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);

        MasterAdapter adapter = new MasterAdapter(this);
        adapter.setData(viewModel.getPosts());
        adapter.setOnScrollToThresholdListener(15, viewModel::loadPosts);
        adapter.addOnItemClickedListener(viewModel::setCurrentIndex);
        adapter.addOnItemClickedListener(i -> viewModel.goToDetail());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(viewModel.getCurrentIndex());

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

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscription.unsubscribe();
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
        Intent intent = new Intent(getContext(), SearchActivity.class);
        startActivity(intent);
    }
}
