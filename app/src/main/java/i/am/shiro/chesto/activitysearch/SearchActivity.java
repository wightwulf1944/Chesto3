package i.am.shiro.chesto.activitysearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import i.am.shiro.chesto.R;
import i.am.shiro.chesto.activitymain2.MainActivity2;
import i.am.shiro.chesto.engine.SearchHistory;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.editText) EditText editText;
    @BindView(R.id.clearButton) ImageButton clearButton;
    private TagStore tagStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

        clearButton.setOnClickListener(view -> editText.setText(""));

        EditorSearchListener editorSearchListener = new EditorSearchListener();
        editorSearchListener.setAction(this::invokeSearch);
        editText.setOnEditorActionListener(editorSearchListener);

        AfterTextChangedListener afterTextChangedListener = new AfterTextChangedListener();
        afterTextChangedListener.setAction(this::onTextChanged);
        editText.addTextChangedListener(afterTextChangedListener);

        SearchAdapter adapter = new SearchAdapter();
        adapter.setOnItemClickListener(this::invokeSearch);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        tagStore = new TagStore(adapter);

        String searchString = SearchHistory.current().getSearchString();
        editText.setText(searchString);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void onTextChanged(String s) {
        if (s.isEmpty()) {
            clearButton.setVisibility(View.GONE);
        } else {
            clearButton.setVisibility(View.VISIBLE);
        }

        int spaceIndex = s.lastIndexOf(" ");
        String currentQuery = s.substring(spaceIndex + 1);
        tagStore.searchTags(currentQuery);
    }

    private void invokeSearch(String searchString) {
        Intent intent = new Intent(this, MainActivity2.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra("default", searchString);
        startActivity(intent);
        finish();
    }
}
