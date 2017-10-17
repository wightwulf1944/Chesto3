package i.am.shiro.chesto.activitymain.fragmentdetail;

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
import i.am.shiro.chesto.activitymain.MainActivity;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.engine.SearchSubscriber;
import timber.log.Timber;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

/**
 * Created by Subaru Tashiro on 8/24/2017.
 */

public class DetailFragment extends Fragment {

    private SearchSubscriber searchSubscriber;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_post, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        PostSearch postSearch = parentActivity.getPostSearch();
        int currentIndex = parentActivity.getCurrentIndex();

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

        PostTagAdapter postTagAdapter = new PostTagAdapter();
        postTagAdapter.setData(postSearch);
        postTagAdapter.setOnItemClickListener(this::onTagClicked);

        RecyclerView tagRecycler = view.findViewById(R.id.tagRecyclerView);
        tagRecycler.setLayoutManager(layoutManager);
        tagRecycler.setAdapter(postTagAdapter);

        PostImageAdapter postImageAdapter = new PostImageAdapter();
        postImageAdapter.setData(postSearch);

        ScrollToPageListener listener1 = new ScrollToPageListener();
        listener1.setOnScrollToPageListener(postTagAdapter::setCurrentIndex);

        ScrollToPageListener listener2 = new ScrollToPageListener();
        listener2.setOnScrollToPageListener(parentActivity::setCurrentIndex);

        RecyclerView imageRecycler = view.findViewById(R.id.imageRecyclerView);
        imageRecycler.setHasFixedSize(true);
        imageRecycler.setAdapter(postImageAdapter);
        imageRecycler.addOnScrollListener(listener1);
        imageRecycler.addOnScrollListener(listener2);
        imageRecycler.scrollToPosition(currentIndex);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(imageRecycler);

        Snackbar errorSnackbar = Snackbar.make(imageRecycler, "Could not load more posts", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", v -> postSearch.load());

        searchSubscriber = postSearch.makeSubscriber();
        searchSubscriber.setOnPostAddedListener(postImageAdapter::notifyItemInserted);
        searchSubscriber.setOnPostUpdatedListener(postImageAdapter::notifyItemChanged);
        searchSubscriber.setOnResultsClearedListener(postImageAdapter::notifyDataSetChanged);
        searchSubscriber.setOnErrorListener(errorSnackbar::show);

        Timber.d("DETAIL FRAGMENT CREATED");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchSubscriber.unsubscribe();

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