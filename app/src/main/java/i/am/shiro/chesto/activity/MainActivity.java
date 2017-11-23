package i.am.shiro.chesto.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.fragment.DetailFragment;
import i.am.shiro.chesto.fragment.MasterFragment;
import i.am.shiro.chesto.service.DownloadService;
import i.am.shiro.chesto.subscription.Subscription;
import i.am.shiro.chesto.viewmodel.MainViewModel;
import io.realm.Realm;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static i.am.shiro.chesto.viewmodel.MainViewModel.DETAIL;
import static i.am.shiro.chesto.viewmodel.MainViewModel.MASTER;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 */

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 0;

    private final Realm realm = Realm.getDefaultInstance();

    private MainViewModel viewModel;

    private Subscription subscription;

    private long lastTimeBackPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_main);

        if (savedState == null) {
            viewModel = new MainViewModel(getSearchString());

            getFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new MasterFragment())
                    .commit();
        } else {
            String modelId = savedState.getString("modelId");
            viewModel = new MainViewModel(realm, modelId);

            // fragment state automatically restored by fragment manager
        }

        View view = findViewById(android.R.id.content);
        Snackbar errorSnackbar = Snackbar.make(view, "Check your connection", LENGTH_INDEFINITE);
        errorSnackbar.setAction("Retry", v -> viewModel.loadPosts());

        subscription = Subscription.from(
                viewModel.addOnErrorListener(errorSnackbar::show),
                viewModel.addOnViewStateChangedListener(this::onViewStateChanged)
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String modelId = viewModel.saveState(realm);
        outState.putString("modelId", modelId);
        subscription.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        if (viewModel.getViewState() == DETAIL) {
            viewModel.goToMaster();
        } else if (!isTaskRoot() || System.currentTimeMillis() < lastTimeBackPressed + 1500) {
            super.onBackPressed();
        } else {
            View contentView = findViewById(android.R.id.content);
            Snackbar.make(contentView, R.string.main_snackbar_exit, LENGTH_SHORT).show();
            lastTimeBackPressed = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                invokeSearch();
                return true;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CODE || grantResults.length == 0) {
            return;
        }

        if (grantResults[0] == PERMISSION_GRANTED) {
            invokeDownload();
        } else {
            View contentView = findViewById(android.R.id.content);
            Snackbar.make(contentView, "Please allow access to save image", LENGTH_SHORT).show();
        }
    }

    public MainViewModel getViewModel() {
        return viewModel;
    }

    private void invokeSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void invokeDownload() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PERMISSION_GRANTED) {
            DownloadService.queue(this, viewModel.getCurrentPost());
        } else {
            String[] permissionStrings = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
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

    private String getSearchString() {
        Intent intent = getIntent();
        String action = intent.getAction();

        if (action == null) {
            throw new RuntimeException("No action found for intent: " + intent.toString());
        }

        switch (action) {
            case Intent.ACTION_MAIN:
                return "";
            case Intent.ACTION_SEARCH:
                return intent.getStringExtra("default");
            default:
                throw new RuntimeException("Unhandled intent action: " + action);
        }
    }

    private void onViewStateChanged(int viewState) {
        if (viewState == MASTER) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new MasterFragment())
                    .commit();
        } else {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new DetailFragment())
                    .commit();
        }
    }
}
