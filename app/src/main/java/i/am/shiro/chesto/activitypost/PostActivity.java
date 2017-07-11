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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import i.am.shiro.chesto.R;
import i.am.shiro.chesto.engine.SearchHistory;
import i.am.shiro.chesto.models.Post;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static butterknife.ButterKnife.findById;

/**
 * Created by Subaru Tashiro on 7/7/2017.
 * TODO: share url or image option
 */

public class PostActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 0;

    @BindView(R.id.imageview) ImageView imageView;
    int postIndex;
    Post post;

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

        postIndex = getIntent().getIntExtra("default", -1);
        post = SearchHistory.current().getPost(postIndex);

        BlurTransformation blurTransformation = new BlurTransformation(this, 1);

        DrawableRequestBuilder thumb = Glide.with(this)
                .load(post.getSmallFileUrl())
                .bitmapTransform(blurTransformation)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        Glide.with(this)
                .load(post.getLargeFileUrl())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_broken)
                .thumbnail(thumb)
                .into(imageView);
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

    private void finishAndReturnResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("default", postIndex); //TODO
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
        Uri webUri = post.getWebUri();
        Intent intent = new Intent(Intent.ACTION_VIEW, webUri);
        startActivity(intent);
    }

    private void sharePost() {
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
            Snackbar.make(imageView, "Please allow access to save image", Snackbar.LENGTH_SHORT).show();
        }
    }
}
