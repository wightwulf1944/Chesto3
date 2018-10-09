package i.am.shiro.chesto.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.adapter.SearchInputAdapter;
import i.am.shiro.chesto.adapter.InputSuggestionsAdapter;
import i.am.shiro.chesto.util.SimpleTextWatcher;
import i.am.shiro.chesto.viewmodel.SearchViewModel2;

public class SearchActivity2 extends AppCompatActivity {

    public static Intent makeIntent(Context context) {
        return new Intent(context, SearchActivity2.class);
    }

    private SearchViewModel2 viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(SearchViewModel2.class);

        setContentView(R.layout.activity_search2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        View goButton = findViewById(R.id.button_go);
        goButton.setOnClickListener(v -> invokeMaster());

        EditText suggestionFilterEdit = findViewById(R.id.edit_suggestion_filter);
        suggestionFilterEdit.addTextChangedListener(new SimpleTextWatcher(viewModel::onSuggestionFilterChanged));

        SearchInputAdapter searchInputAdapter = new SearchInputAdapter();
        searchInputAdapter.setOnItemRemoveClickListener(viewModel::onSearchInputRemoveClick);

        RecyclerView searchInputRecycler = findViewById(R.id.recycler_search_input);
        searchInputRecycler.setHasFixedSize(true);
        searchInputRecycler.setAdapter(searchInputAdapter);

        InputSuggestionsAdapter inputSuggestionsAdapter = new InputSuggestionsAdapter();
        inputSuggestionsAdapter.setOnItemClickListener(viewModel::onInputSuggestionClick);

        RecyclerView inputSuggestionsRecycler = findViewById(R.id.recycler_input_suggestions);
        inputSuggestionsRecycler.setHasFixedSize(true);
        inputSuggestionsRecycler.setAdapter(inputSuggestionsAdapter);

        viewModel.getSearchInputData().observe(this, searchInputAdapter::setData);
        viewModel.getInputSuggestionsData().observe(this, inputSuggestionsAdapter::setData);
    }

    private void invokeMaster() {
        String queryString = viewModel.getQueryString();

        Intent intent = MasterActivity.makeIntent(this, queryString);
        startActivity(intent);
    }
}
