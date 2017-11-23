package i.am.shiro.chesto.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import i.am.shiro.chesto.service.DownloadService;
import i.am.shiro.chesto.subscription.Subscription;
import i.am.shiro.chesto.viewmodel.MainViewModel;
import timber.log.Timber;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by Subaru Tashiro on 8/24/2017.
 */

public class DetailFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 0;

    private MainViewModel viewModel;

    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        viewModel = parentActivity.getViewModel();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_nav_back);
        toolbar.setNavigationOnClickListener(v -> viewModel.goToMaster());
        toolbar.inflateMenu(R.menu.activity_post);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        View bottomSheet = view.findViewById(R.id.bottomSheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(STATE_HIDDEN);

        ImageButton infoButton = view.findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> behavior.setState(STATE_COLLAPSED));

        ImageButton hideButton = view.findViewById(R.id.hideButton);
        hideButton.setOnClickListener(v -> behavior.setState(STATE_HIDDEN));

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
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

        subscription = Subscription.from(
                viewModel.addOnCurrentPostChangedListener(detailTagAdapter::setCurrentPost),
                viewModel.addOnPostAddedListener(detailImageAdapter::notifyItemInserted),
                viewModel.addOnPostUpdatedListener(detailImageAdapter::notifyItemChanged),
                viewModel.addOnResultsClearedListener(detailImageAdapter::notifyDataSetChanged)
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CODE || grantResults.length == 0) return;

        if (grantResults[0] == PERMISSION_GRANTED) {
            invokeDownload();
        } else {
            Snackbar.make(getView(), "Please allow access to save image", LENGTH_SHORT).show();
        }
    }

    private boolean onMenuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                viewModel.goToMaster();
                return true;
            case R.id.action_download:
                invokeDownload();
                return true;
            case R.id.action_open_browser:
                invokeOpenInBrowser();
                return true;
            case R.id.action_share:
                invokeShare();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void invokeDownload() {
        Context context = getContext();
        if (context == null) return;

        if (checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            DownloadService.queue(context, viewModel.getCurrentPost());
        } else {
            String[] permissionStrings = {WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissionStrings, PERMISSION_REQUEST_CODE);
        }
    }

    private void invokeOpenInBrowser() {
        String webUrl = viewModel.getCurrentPost().getWebUrl();
        Uri webUri = Uri.parse(webUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, webUri);
        startActivity(intent);
    }

    private void invokeShare() {
        String webUrl = viewModel.getCurrentPost().getWebUrl();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, webUrl);
        intent.setType("text/plain");
        Intent chooserIntent = Intent.createChooser(intent, "Share link - " + webUrl);
        startActivity(chooserIntent);
    }

    private void onTagClicked(String tagString) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra("default", tagString);
        startActivity(intent);
    }
}
