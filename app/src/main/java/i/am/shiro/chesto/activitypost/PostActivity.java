package i.am.shiro.chesto.activitypost;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import i.am.shiro.chesto.R;
import i.am.shiro.chesto.activitymain.MainActivity;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.engine.SearchHistory;
import i.am.shiro.chesto.engine.SearchSubscriber;
import i.am.shiro.chesto.models.Post;
import i.am.shiro.chesto.servicedownload.DownloadService;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

/**
 * Created by Subaru Tashiro on 7/7/2017.
 * TODO: clearly outline view setup and data binding to view in onCreate()
 * TODO: share url or image option
 */

public class PostActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 0;

    @BindView(R.id.imageRecyclerView) RecyclerView imageRecycler;
    @BindView(R.id.tagRecyclerView) RecyclerView tagRecycler;
    @BindView(R.id.infoButton) ImageButton infoButton;
    @BindView(R.id.hideButton) ImageButton hideButton;
    @BindView(R.id.bottomSheet) View bottomSheet;
    private int currentPage;
    private PostSearch currentSearch;
    private SearchSubscriber searchSubscriber;
    private PostTagAdapter postTagAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        Snackbar errorSnackbar = Snackbar.make(imageRecycler, "Could not load more posts", Snackbar.LENGTH_INDEFINITE);

        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(STATE_HIDDEN);
        infoButton.setOnClickListener(v -> behavior.setState(STATE_COLLAPSED));
        hideButton.setOnClickListener(v -> behavior.setState(STATE_HIDDEN));

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        postTagAdapter = new PostTagAdapter();
        postTagAdapter.setOnItemClickListener(this::onTagClicked);
        tagRecycler.setLayoutManager(layoutManager);
        tagRecycler.setAdapter(postTagAdapter);

        PostImageAdapter postImageAdapter = new PostImageAdapter();
        ScrollToPageListener scrollToPageListener = new ScrollToPageListener();
        scrollToPageListener.setOnScrollToPageListener(this::onScrollToImagePage);
        imageRecycler.setAdapter(postImageAdapter);
        imageRecycler.addOnScrollListener(scrollToPageListener);
        imageRecycler.setHasFixedSize(true);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(imageRecycler);

        // bind search to activity
        currentSearch = SearchHistory.current();
        postImageAdapter.setData(currentSearch);
        errorSnackbar.setAction("Retry", v -> currentSearch.load());

        int postIndex = getIntent().getIntExtra("default", -1);
        imageRecycler.scrollToPosition(postIndex);

        searchSubscriber = currentSearch.makeSubscriber();
        searchSubscriber.setOnPostAddedListener(postImageAdapter::notifyItemInserted);
        searchSubscriber.setOnPostUpdatedListener(postImageAdapter::notifyItemChanged);
        searchSubscriber.setOnResultsClearedListener(postImageAdapter::notifyDataSetChanged);
        searchSubscriber.setOnErrorListener(errorSnackbar::show);
    }

    @Override
    protected void onDestroy() {
        searchSubscriber.unsubscribe();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAndReturnResult();
                return true;
            case R.id.action_download:
                downloadPost();
                return true;
            case R.id.action_open_browser:
                openPostInBrowser();
                return true;
            case R.id.action_share:
                sharePost();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finishAndReturnResult();
    }

    private void finishAndReturnResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("default", currentPage);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void onScrollToImagePage(int position) {
        currentPage = position;
        Post post = currentSearch.getPost(position);
        postTagAdapter.setData(post);
        postTagAdapter.notifyDataSetChanged();
    }

    private void onTagClicked(String tagString) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra("default", tagString);
        startActivity(intent);
    }

    private void downloadPost() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PERMISSION_GRANTED) {
            Post post = currentSearch.getPost(currentPage);
            DownloadService.queue(this, post);
        } else {
            String[] permissionStrings = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissionStrings, PERMISSION_REQUEST_CODE);
        }
    }

    private void openPostInBrowser() {
        Post post = currentSearch.getPost(currentPage);
        Uri webUri = Uri.parse(post.getWebUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, webUri);
        startActivity(intent);
    }

    private void sharePost() {
        Post post = currentSearch.getPost(currentPage);
        String webUrl = post.getWebUrl();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, webUrl);
        intent.setType("text/plain");

        Intent chooserIntent = Intent.createChooser(intent, "Share link - " + webUrl);

        startActivity(chooserIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CODE || grantResults.length == 0) {
            return;
        }

        if (grantResults[0] == PERMISSION_GRANTED) {
            downloadPost();
        } else {
            Snackbar.make(imageRecycler, "Please allow access to save image", Snackbar.LENGTH_SHORT).show();
        }
    }
}
