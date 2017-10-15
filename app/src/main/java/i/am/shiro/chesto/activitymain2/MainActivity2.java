package i.am.shiro.chesto.activitymain2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.activitymain2.fragmentdetail.DetailFragment;
import i.am.shiro.chesto.activitymain2.fragmentmaster.MasterFragment;
import i.am.shiro.chesto.activitysearch.SearchActivity;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.engine.SearchHistory;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 */

public class MainActivity2 extends AppCompatActivity {

    private static int activityCount;

    private PostSearch postSearch;
    private long lastTimeBackPressed;
    private int currentIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_main2);

        if (savedState == null) {
            postSearch = new PostSearch(getSearchString());
            postSearch.load();
            SearchHistory.goForward(postSearch);
            attachMasterFragment();
            activityCount++;
        } else {
            postSearch = SearchHistory.current();
            currentIndex = savedState.getInt("currentIndex");
            activityCount = savedState.getInt("activityCount");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentIndex", currentIndex);
        outState.putInt("activityCount", activityCount);
    }

    @Override
    protected void onDestroy() {
        activityCount--;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            goToMaster(null);
        } else if (activityCount > 1) {
            super.onBackPressed();
        } else if (System.currentTimeMillis() < lastTimeBackPressed + 1500) {
            super.onBackPressed();
        } else {
            View contentView = findViewById(android.R.id.content);
            Snackbar.make(contentView, R.string.main_snackbar_exit, Snackbar.LENGTH_SHORT).show();
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
                goToMaster(null);
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

    public PostSearch getPostSearch() {
        return postSearch;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int i) {
        currentIndex = i;
    }

    public void goToMaster(View view) {
        getFragmentManager().popBackStack();
    }

    public void goToDetail(int index) {
        currentIndex = index;

        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new DetailFragment())
                .addToBackStack(null)
                .commit();
    }

    private void attachMasterFragment() {
        getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, new MasterFragment())
                .commit();
    }

    private void invokeSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void invokeOpenInBrowser() {
        String webUrl = postSearch.getPost(currentIndex).getWebUrl();
        Uri webUri = Uri.parse(webUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, webUri);
        startActivity(intent);
    }

    private void invokeShare() {
        String webUrl = postSearch.getPost(currentIndex).getWebUrl();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, webUrl);
        intent.setType("text/plain");
        Intent chooserIntent = Intent.createChooser(intent, "Share link - " + webUrl);
        startActivity(chooserIntent);
    }

    private String getSearchString() {
        Intent intent = getIntent();
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_MAIN:
                return "";
            case Intent.ACTION_SEARCH:
                return intent.getStringExtra("default");
            default:
                throw new RuntimeException("Unhandled intent action: " + action);
        }
    }
}
