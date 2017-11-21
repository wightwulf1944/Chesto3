package i.am.shiro.chesto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.listener.Listener1;
import i.am.shiro.chesto.model.Post;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 7/19/2017.
 * TODO: identify why LayoutInflater views ignores some xml attributes such as layout_wrapBefore
 * TODO: may get free performance gain by using arraylist.ensurecapacity()
 */

public final class DetailTagAdapter extends RecyclerView.Adapter<DetailTagAdapter.ViewHolder> {

    private final ArrayList<Item> items = new ArrayList<>();

    private Listener1<String> onItemClickListener;

    public void setOnItemClickListener(Listener1<String> listener) {
        onItemClickListener = listener;
    }

    public void setCurrentPost(Post post) {
        items.clear();
        setCategoryTags("Copyrights:", R.layout.item_post_tag_copyright, post.getTagStringCopyright());
        setCategoryTags("Characters:", R.layout.item_post_tag_character, post.getTagStringCharacter());
        setCategoryTags("Artist:", R.layout.item_post_tag_artist, post.getTagStringArtist());
        setCategoryTags("Tags:", R.layout.item_post_tag_general, post.getTagStringGeneral());
        setCategoryTags("Meta:", R.layout.item_post_tag_meta, post.getTagStringMeta());
        notifyDataSetChanged();
    }

    private void setCategoryTags(String categoryLabel, int layout, String tags) {
        Timber.d("%s %s", categoryLabel, tags);
        if (tags.isEmpty()) return;

        items.add(new Item(R.layout.item_post_label, categoryLabel));
        for (String tag : tags.split(" ")) {
            items.add(new Item(layout, tag));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(viewType, parent, false);
        ViewHolder vh = new ViewHolder(view);

        if (viewType == R.layout.item_post_label) {
            FlexboxLayoutManager.LayoutParams layoutParams = (FlexboxLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setWrapBefore(true);
        } else {
            view.setOnClickListener(v -> {
                int adapterPosition = vh.getAdapterPosition();
                String tagString = items.get(adapterPosition).text;
                onItemClickListener.onEvent(tagString);
            });
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String itemString = items.get(position).text;
        TextView textView = (TextView) holder.itemView;
        textView.setText(itemString);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class Item {
        private final int viewType;
        private final String text;

        private Item(int viewType, String text) {
            this.viewType = viewType;
            this.text = text;
        }
    }
}
