package i.am.shiro.chesto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.TagStore;
import i.am.shiro.chesto.adapter.SearchAdapter;
import io.realm.Realm;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private final Realm realm = Realm.getDefaultInstance();

    private final TagStore tagStore = new TagStore(realm);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_nav_back);
        toolbar.setNavigationOnClickListener(view -> finish());

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        SearchAdapter adapter = new SearchAdapter();
        adapter.setData(tagStore.getResults());
        adapter.setOnItemClickListener(this::onQueryTextSubmit);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        tagStore.setDatasetChangedListener(adapter::setData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra("default", query);
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        int spaceIndex = newText.lastIndexOf(" ");
        String currentQuery = newText.substring(spaceIndex + 1);
        tagStore.searchTags(currentQuery);
        return true;
    }
}
