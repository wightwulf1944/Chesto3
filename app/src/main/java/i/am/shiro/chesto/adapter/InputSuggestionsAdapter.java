package i.am.shiro.chesto.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.function.Consumer;

import java.util.Collections;
import java.util.List;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.model.TagSuggestion;

public class InputSuggestionsAdapter extends RecyclerView.Adapter<InputSuggestionsAdapter.ViewHolder> {

    private List<TagSuggestion> inputSuggestions;

    private Consumer<TagSuggestion> onItemClickListener;

    public InputSuggestionsAdapter() {
        inputSuggestions = Collections.emptyList();
    }

    public void setData(List<TagSuggestion> inputSuggestions) {
        this.inputSuggestions = inputSuggestions;
        notifyDataSetChanged();
        // todo use submitList if inputSuggestions is smaller than before
    }

    public void setOnItemClickListener(Consumer<TagSuggestion> listener) {
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_search_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TagSuggestion suggestion = inputSuggestions.get(position);
        holder.bindData(suggestion);
    }

    @Override
    public int getItemCount() {
        return inputSuggestions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView postCountText;

        private final TextView nameText;

        private TagSuggestion inputSuggestion;

        private ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(view -> onItemClick());
            postCountText = itemView.findViewById(R.id.text_postcount);
            nameText = itemView.findViewById(R.id.text_name);
        }

        private void bindData(TagSuggestion data) {
            this.inputSuggestion = data;
            postCountText.setText(data.getPostCount());
            nameText.setText(data.getName());
        }

        private void onItemClick() {
            onItemClickListener.accept(inputSuggestion);
        }
    }
}
