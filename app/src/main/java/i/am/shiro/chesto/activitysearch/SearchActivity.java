package i.am.shiro.chesto.activitysearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import i.am.shiro.chesto.R;
import i.am.shiro.chesto.activitymain.MainActivity;
import i.am.shiro.chesto.engine.SearchHistory;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.editText) EditText editText;
    private String currentQuery;
    private TagStore tagStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

        EditorSearchListener editorSearchListener = new EditorSearchListener();
        editorSearchListener.setAction(this::invokeSearch);
        editText.setOnEditorActionListener(editorSearchListener);

        AfterTextChangedListener afterTextChangedListener = new AfterTextChangedListener();
        afterTextChangedListener.setAction(this::onTextChanged);
        editText.addTextChangedListener(afterTextChangedListener);

        SearchAdapter adapter = new SearchAdapter();
        adapter.setOnItemClickListener(this::onAdapterItemClicked);

        tagStore = new TagStore(adapter);

        RecyclerView recyclerView = ButterKnife.findById(this, R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        MenuItem clearButton = menu.findItem(R.id.clear);

        AfterTextChangedListener listener = new AfterTextChangedListener();
        listener.setAction(s -> clearButton.setVisible(!s.isEmpty()));
        editText.addTextChangedListener(listener);

        String searchString = SearchHistory.current().getSearchString();
        editText.setText(searchString);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.clear:
                editText.setText("");
                return true;
            case R.id.go:
                invokeSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onTextChanged(String s) {
        int spaceIndex = s.lastIndexOf(" ");
        currentQuery = s.substring(spaceIndex + 1);
        tagStore.searchTags(currentQuery);
    }

    private void onAdapterItemClicked(String itemName) {
        String text = editText.getText()
                .toString()
                .replaceFirst(currentQuery, itemName);
        editText.setText(text);
    }

    private void invokeSearch() {
        String searchString = editText.getText().toString();
        Uri uri = Uri.parse(searchString);
        Intent intent = new Intent(
                Intent.ACTION_SEARCH,
                uri,
                this,
                MainActivity.class
        );
        startActivity(intent);
        finish();
    }
}
