package i.am.shiro.chesto.activitymain2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.activitymain2.fragmentdetail.DetailFragment;
import i.am.shiro.chesto.activitymain2.fragmentmaster.MasterFragment;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.engine.SearchHistory;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 */

public class MainActivity2 extends AppCompatActivity {

    private PostSearch postSearch;
    private long lastTimeBackPressed;
    private int currentIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if (savedInstanceState == null) {
            postSearch = new PostSearch(getSearchString());
            postSearch.load();
            SearchHistory.goForward(postSearch);
            attachMasterFragment();
        } else {
            postSearch = SearchHistory.current();
            currentIndex = savedInstanceState.getInt("currentIndex");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentIndex", currentIndex);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            goToMaster(null);
        } else if (System.currentTimeMillis() < lastTimeBackPressed + 1500) {
            super.onBackPressed();
        } else {
            View contentView = findViewById(android.R.id.content);
            Snackbar.make(contentView, R.string.main_snackbar_exit, Snackbar.LENGTH_SHORT).show();
            lastTimeBackPressed = System.currentTimeMillis();
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
