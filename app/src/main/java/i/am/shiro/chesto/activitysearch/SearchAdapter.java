package i.am.shiro.chesto.activitysearch;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.listeners.Listener1;
import i.am.shiro.chesto.models.Tag;


/**
 * Created by Shiro on 3/20/2017.
 */
final class SearchAdapter extends Adapter<ViewHolder> {

    private Listener1<String> onItemClickListener;
    private List<Tag> items;

    void setData(List<Tag> data) {
        items = data;
    }

    void setOnItemClickListener(Listener1<String> listener) {
        onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_search_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tag tag = items.get(position);
        TagViewHolder tagViewHolder = (TagViewHolder) holder;
        tagViewHolder.postCount.setText(tag.getPostCountStr());
        tagViewHolder.name.setText(tag.getName());
    }

    private final class TagViewHolder extends ViewHolder {

        private final TextView postCount;
        private final TextView name;

        TagViewHolder(View view) {
            super(view);
            postCount = view.findViewById(R.id.postCount);
            name = view.findViewById(R.id.name);
            view.setOnClickListener(v -> {
                String itemName = name.getText().toString();
                onItemClickListener.onEvent(itemName);
            });
        }
    }
}
