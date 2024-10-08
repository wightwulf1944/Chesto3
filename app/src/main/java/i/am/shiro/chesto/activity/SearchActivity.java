package i.am.shiro.chesto.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.adapter.SearchAdapter;
import i.am.shiro.chesto.viewmodel.SearchViewModel;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static Intent makeIntent(Context context) {
        return new Intent(context, SearchActivity.class);
    }

    private SearchViewModel viewModel;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        searchView = findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        SearchAdapter adapter = new SearchAdapter();
        adapter.setOnItemClickListener(this::invokeMaster);
        adapter.setOnAppendClickListener(this::onAppendClicked);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        viewModel.observeTagSuggestions(this, adapter::setData);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        invokeMaster(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        int spaceIndex = newText.lastIndexOf(" ");
        String lastWord = newText.substring(spaceIndex + 1);
        viewModel.fetchSuggestions(lastWord);
        return true;
    }

    private void onAppendClicked(String tagName) {
        String query = searchView.getQuery().toString();
        int spaceIndex = query.lastIndexOf(" ");
        String newQuery = query.substring(0, spaceIndex + 1) + tagName;
        searchView.setQuery(newQuery, false);
    }

    private void invokeMaster(String query) {
        Intent intent = MasterActivity.makeIntent(this, query);
        startActivity(intent);
    }
}
