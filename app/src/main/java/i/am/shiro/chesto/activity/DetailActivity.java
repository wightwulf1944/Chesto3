package i.am.shiro.chesto.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.adapter.DetailImageAdapter;
import i.am.shiro.chesto.adapter.DetailTagAdapter;
import i.am.shiro.chesto.util.ScrollToPageListener;
import i.am.shiro.chesto.service.DownloadService;
import i.am.shiro.chesto.viewmodel.DetailViewModel;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;

/**
 * Created by Shiro on 12/27/2017.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String FLOW_ID_EXTRA = "flowId";

    private static final int PERMISSION_REQUEST_CODE = 0;

    private DetailViewModel viewModel;

    public static Intent makeIntent(Context context, String flowId) {
        Intent starter = new Intent(context, DetailActivity.class);
        starter.putExtra(FLOW_ID_EXTRA, flowId);
        return starter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String flowId = getIntent().getStringExtra(FLOW_ID_EXTRA);

        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        viewModel.loadFlow(flowId);

        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_nav_back);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.inflateMenu(R.menu.activity_detail);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        View bottomSheet = findViewById(R.id.bottomSheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(STATE_HIDDEN);

        ImageButton infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> behavior.setState(STATE_COLLAPSED));

        ImageButton hideButton = findViewById(R.id.hideButton);
        hideButton.setOnClickListener(v -> behavior.setState(STATE_HIDDEN));

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);

        DetailTagAdapter detailTagAdapter = new DetailTagAdapter();
        detailTagAdapter.setOnItemClickListener(this::invokeMaster);

        RecyclerView tagRecycler = findViewById(R.id.tagRecyclerView);
        tagRecycler.setLayoutManager(layoutManager);
        tagRecycler.setAdapter(detailTagAdapter);

        DetailImageAdapter detailImageAdapter = new DetailImageAdapter(this);
        detailImageAdapter.setOnItemBindListener(viewModel::onItemBind);

        ScrollToPageListener scrollToPageListener = new ScrollToPageListener();
        scrollToPageListener.setOnScrollToPageListener(viewModel::onScrollToPage);

        RecyclerView imageRecycler = findViewById(R.id.imageRecyclerView);
        imageRecycler.setHasFixedSize(true);
        imageRecycler.setAdapter(detailImageAdapter);
        imageRecycler.addOnScrollListener(scrollToPageListener);
        imageRecycler.scrollToPosition(viewModel.getCurrentIndex());

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(imageRecycler);

        viewModel.observeCurrentPost(this, detailTagAdapter::setCurrentPost);
        viewModel.observePosts(this, detailImageAdapter::submitList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != PERMISSION_REQUEST_CODE || grantResults.length == 0) return;

        if (grantResults[0] == PERMISSION_GRANTED) {
            invokeDownload();
        } else {
            View view = findViewById(android.R.id.content);
            Snackbar.make(view, "Please allow access to save image", LENGTH_SHORT).show();
        }
    }

    private boolean onMenuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
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
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            DownloadService.queue(this, viewModel.getCurrentPost());
            View view = findViewById(android.R.id.content);
            Snackbar.make(view, "Download queued", LENGTH_SHORT).show();
        } else {
            String[] permissionStrings = {WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissionStrings, PERMISSION_REQUEST_CODE);
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

    private void invokeMaster(String tagString) {
        Intent intent = MasterActivity.makeIntent(this, tagString);
        startActivity(intent);
    }
}
