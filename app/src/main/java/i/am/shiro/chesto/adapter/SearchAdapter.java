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
import i.am.shiro.chesto.model.Tag;

/**
 * Created by Shiro on 3/20/2017.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<Tag> data;

    private Consumer<String> onItemClickListener;

    private Consumer<String> onAppendClickListener;

    public void setData(List<Tag> data) {
        this.data = data;
        notifyDataSetChanged();
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
        Tag tag = data.get(position);
        viewHolder.bind(tag);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void onItemClicked(int i) {
        String name = data.get(i).getName();
        onItemClickListener.accept(name);
    }

    private void onAppendClicked(int i) {
        String name = data.get(i).getName();
        onAppendClickListener.accept(name);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView postCount;
        private final TextView name;
        private final ImageButton appendButton;

        ViewHolder(View view) {
            super(view);
            postCount = view.findViewById(R.id.postCount);
            name = view.findViewById(R.id.name);
            appendButton = view.findViewById(R.id.appendButton);

            itemView.setOnClickListener(v -> onItemClicked(getAdapterPosition()));
            appendButton.setOnClickListener(v -> onAppendClicked(getAdapterPosition()));
        }

        private void bind(Tag tag) {
            postCount.setText(tag.getPostCountStr());
            name.setText(tag.getName());
        }
    }
}