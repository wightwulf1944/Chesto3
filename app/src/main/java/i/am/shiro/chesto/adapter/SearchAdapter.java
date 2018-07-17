package i.am.shiro.chesto.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.annimon.stream.function.Consumer;

import java.util.List;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.model.TagSuggestion;

/**
 * Created by Shiro on 3/20/2017.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<TagSuggestion> data;

    private Consumer<String> onItemClickListener;

    private Consumer<String> onAppendClickListener;

    public void setData(List<TagSuggestion> data) {
        this.data = data;
        notifyDataSetChanged();
        // todo use submitList if data is smaller than before
    }

    public void setOnItemClickListener(Consumer<String> listener) {
        onItemClickListener = listener;
    }

    public void setOnAppendClickListener(Consumer<String> listener) {
        onAppendClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_search_tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        TagSuggestion suggestion = data.get(position);
        viewHolder.bind(suggestion);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView postCount;
        private final TextView name;
        private final ImageButton appendButton;

        private String nameStr;

        ViewHolder(View view) {
            super(view);
            postCount = view.findViewById(R.id.postCount);
            name = view.findViewById(R.id.name);
            appendButton = view.findViewById(R.id.appendButton);

            itemView.setOnClickListener(v -> onItemClickListener.accept(nameStr));
            appendButton.setOnClickListener(v -> onAppendClickListener.accept(nameStr));
        }

        private void bind(TagSuggestion suggestion) {
            nameStr = suggestion.getName().toString();
            postCount.setText(suggestion.getPostCount());
            name.setText(suggestion.getName());
        }
    }
}