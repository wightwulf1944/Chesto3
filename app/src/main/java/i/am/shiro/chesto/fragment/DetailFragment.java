package i.am.shiro.chesto.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.activity.MainActivity;
import i.am.shiro.chesto.adapter.DetailImageAdapter;
import i.am.shiro.chesto.adapter.DetailTagAdapter;
import i.am.shiro.chesto.listener.ScrollToPageListener;
import i.am.shiro.chesto.subscription.Subscription;
import i.am.shiro.chesto.viewmodel.MainViewModel;
import timber.log.Timber;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;
import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.support.design.widget.Snackbar.make;

/**
 * Created by Subaru Tashiro on 8/24/2017.
 */

public class DetailFragment extends Fragment {

    private Subscription subscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        MainViewModel viewModel = parentActivity.getViewModel();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        parentActivity.setSupportActionBar(toolbar);
        parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View bottomSheet = view.findViewById(R.id.bottomSheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(STATE_HIDDEN);

        ImageButton infoButton = view.findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> behavior.setState(STATE_COLLAPSED));

        ImageButton hideButton = view.findViewById(R.id.hideButton);
        hideButton.setOnClickListener(v -> behavior.setState(STATE_HIDDEN));

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(parentActivity);
        layoutManager.setFlexWrap(FlexWrap.WRAP);

        DetailTagAdapter detailTagAdapter = new DetailTagAdapter();
        detailTagAdapter.setCurrentPost(viewModel.getCurrentPost());
        detailTagAdapter.setOnItemClickListener(this::onTagClicked);

        RecyclerView tagRecycler = view.findViewById(R.id.tagRecyclerView);
        tagRecycler.setLayoutManager(layoutManager);
        tagRecycler.setAdapter(detailTagAdapter);

        DetailImageAdapter detailImageAdapter = new DetailImageAdapter(this);
        detailImageAdapter.setData(viewModel.getPosts());
        detailImageAdapter.setOnScrollToThresholdListener(5, viewModel::loadPosts);

        ScrollToPageListener scrollToPageListener = new ScrollToPageListener();
        scrollToPageListener.setOnScrollToPageListener(viewModel::setCurrentIndex);

        RecyclerView imageRecycler = view.findViewById(R.id.imageRecyclerView);
        imageRecycler.setHasFixedSize(true);
        imageRecycler.setAdapter(detailImageAdapter);
        imageRecycler.addOnScrollListener(scrollToPageListener);
        imageRecycler.scrollToPosition(viewModel.getCurrentIndex());

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(imageRecycler);

        Snackbar errorSnackbar = make(imageRecycler, "Could not load more posts", LENGTH_INDEFINITE)
                .setAction("Retry", v -> viewModel.loadPosts());

        subscription = Subscription.from(
                viewModel.addOnCurrentPostChangedListener(detailTagAdapter::setCurrentPost),
                viewModel.addOnPostAddedListener(detailImageAdapter::notifyItemInserted),
                viewModel.addOnPostUpdatedListener(detailImageAdapter::notifyItemChanged),
                viewModel.addOnResultsClearedListener(detailImageAdapter::notifyDataSetChanged),
                viewModel.addOnErrorListener(errorSnackbar::show)
        );

        Timber.d("DETAIL FRAGMENT CREATED");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscription.unsubscribe();

        Timber.d("DETAIL FRAGMENT DESTROYED");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_post, menu);
    }

    private void onTagClicked(String tagString) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra("default", tagString);
        startActivity(intent);
    }
}
