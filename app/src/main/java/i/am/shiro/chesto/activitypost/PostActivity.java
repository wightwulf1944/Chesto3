package i.am.shiro.chesto.activitypost;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import i.am.shiro.chesto.R;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.engine.SearchHistory;
import i.am.shiro.chesto.models.Post;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static butterknife.ButterKnife.findById;

/**
 * Created by Subaru Tashiro on 7/7/2017.
 * TODO: share url or image option
 * TODO: notify adapter of dataset changes
 */

public class PostActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 0;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private PostSearch currentSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        setSupportActionBar(findById(this, R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        currentSearch = SearchHistory.current();

        PostAdapter postAdapter = new PostAdapter();
        postAdapter.setData(currentSearch);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(postAdapter);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);

        int postIndex = getIntent().getIntExtra("default", -1);
        recyclerView.scrollToPosition(postIndex);
    }

    @Override
    protected void onDestroy() {

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
        resultIntent.putExtra("default", getCurrentItemPosition()); //TODO
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void downloadPost() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PERMISSION_GRANTED) {
            // TODO; launch download
        } else {
            String[] permissionStrings = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissionStrings, PERMISSION_REQUEST_CODE);
        }
    }

    private void openPostInBrowser() {
        Post post = currentSearch.getPost(getCurrentItemPosition());
        Uri webUri = post.getWebUri();
        Intent intent = new Intent(Intent.ACTION_VIEW, webUri);
        startActivity(intent);
    }

    private void sharePost() {
        Post post = currentSearch.getPost(getCurrentItemPosition());
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
            Snackbar.make(recyclerView, "Please allow access to save image", Snackbar.LENGTH_SHORT).show();
        }
    }

    private int getCurrentItemPosition() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        return layoutManager.findFirstVisibleItemPosition();
    }
}