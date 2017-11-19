package i.am.shiro.chesto.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.listener.Listener1;
import i.am.shiro.chesto.model.Tag;

/**
 * Created by Shiro on 3/20/2017.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Listener1<String> onItemClickListener;

    private List<Tag> items;

    public void setData(List<Tag> data) {
        items = data;
    }

    public void setOnItemClickListener(Listener1<String> listener) {
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Tag tag = items.get(position);
        viewHolder.postCount.setText(tag.getPostCountStr());
        viewHolder.name.setText(tag.getName());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView postCount;
        private final TextView name;

        ViewHolder(View view) {
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
