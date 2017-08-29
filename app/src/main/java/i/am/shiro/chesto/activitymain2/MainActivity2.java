package i.am.shiro.chesto.activitymain2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.activitymain.MainActivity;
import i.am.shiro.chesto.activitymain2.fragmentdetail.DetailFragment;
import i.am.shiro.chesto.activitymain2.fragmentmaster.MasterFragment;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.engine.SearchHistory;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 */

public class MainActivity2 extends AppCompatActivity {

    private PostSearch postSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if (savedInstanceState == null) {
            String searchString = getSearchString();
            postSearch = new PostSearch(searchString);
            postSearch.load();
            SearchHistory.goForward(postSearch);
            attachMasterFragment();
        } else {
            postSearch = SearchHistory.current();
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private String getSearchString() {
        String action = getIntent().getAction();
        switch (action) {
            case Intent.ACTION_MAIN:
                return "";
            case Intent.ACTION_SEARCH:
                return getIntent().getStringExtra("default");
            default:
                throw new RuntimeException("Unhandled intent action: " + action);
        }
    }

    private void attachMasterFragment() {
        getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, new MasterFragment())
                .commit();
    }

    public PostSearch getPostSearch() {
        return postSearch;
    }

    public void onMasterItemClicked(int index) {
        Bundle args = new Bundle();
        args.putInt("index", index);

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(args);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onDetailTagClicked(String tagString) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra("default", tagString);
        startActivity(intent);
    }
}