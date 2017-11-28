package i.am.shiro.chesto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.fragment.DetailFragment;
import i.am.shiro.chesto.fragment.MasterFragment;
import i.am.shiro.chesto.subscription.Subscription;
import i.am.shiro.chesto.viewmodel.MainViewModel;
import io.realm.Realm;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static i.am.shiro.chesto.viewmodel.MainViewModel.DETAIL;
import static i.am.shiro.chesto.viewmodel.MainViewModel.MASTER;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 * TODO: dismiss error when loading starts
 */

public class MainActivity extends AppCompatActivity {

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

            getSupportFragmentManager().beginTransaction()
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

    public MainViewModel getViewModel() {
        return viewModel;
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
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new MasterFragment())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new DetailFragment())
                    .commit();
        }
    }
}
