package i.am.shiro.chesto.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.function.Consumer;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.model.Post;

/**
 * Created by Subaru Tashiro on 7/19/2017.
 * TODO: identify why LayoutInflater views ignores some xml attributes such as layout_wrapBefore
 */

public class DetailTagAdapter extends RecyclerView.Adapter<DetailTagAdapter.ViewHolder> {

    private final ArrayList<Pair<Integer, String>> items = new ArrayList<>();

    private Consumer<String> onItemClickListener;

    public void setOnItemClickListener(Consumer<String> listener) {
        onItemClickListener = listener;
    }

    public void setCurrentPost(Post post) {
        items.clear();
        items.ensureCapacity(post.getTagCount());
        addCategoryItems("Copyrights:", R.layout.item_detail_tag_copyright, post.getTagStringCopyright());
        addCategoryItems("Characters:", R.layout.item_detail_tag_character, post.getTagStringCharacter());
        addCategoryItems("Artist:", R.layout.item_detail_tag_artist, post.getTagStringArtist());
        addCategoryItems("Tags:", R.layout.item_detail_tag_general, post.getTagStringGeneral());
        addCategoryItems("Meta:", R.layout.item_detail_tag_meta, post.getTagStringMeta());
        notifyDataSetChanged();
    }

    private void addCategoryItems(String categoryLabel, int layout, String tags) {
        if (tags.isEmpty()) return;

        items.add(new Pair<>(R.layout.item_detail_label, categoryLabel));
        for (String tag : tags.split(" ")) {
            items.add(new Pair<>(layout, tag));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).first;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(viewType, parent, false);
        ViewHolder vh = new ViewHolder(view);

        if (viewType == R.layout.item_detail_label) {
            FlexboxLayoutManager.LayoutParams layoutParams = (FlexboxLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setWrapBefore(true);
        } else {
            view.setOnClickListener(v -> {
                int adapterPosition = vh.getAdapterPosition();
                String tagString = items.get(adapterPosition).second;
                onItemClickListener.accept(tagString);
            });
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String itemText = items.get(position).second;
        holder.bind(itemText);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }

        private void bind(String itemText) {
            textView.setText(itemText);
        }
    }
}
