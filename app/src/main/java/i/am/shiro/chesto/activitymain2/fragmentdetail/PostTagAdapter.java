package i.am.shiro.chesto.activitymain2.fragmentdetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.listeners.Listener1;
import i.am.shiro.chesto.models.Post;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 7/19/2017.
 * TODO: identify why LayoutInflater views ignores some xml attributes such as layout_wrapBefore
 */

final class PostTagAdapter extends RecyclerView.Adapter<PostTagAdapter.ViewHolder> {

    private PostSearch postSearch;
    private List<String> tagList = new ArrayList<>();
    private Listener1<String> onItemClickListener;
    private int copyrightIndex;
    private int characterIndex;
    private int artistIndex;
    private int generalIndex;

    void setData(PostSearch postSearch) {
        this.postSearch = postSearch;
    }

    void setOnItemClickListener(Listener1<String> listener) {
        onItemClickListener = listener;
    }

    void setCurrentIndex(int index) {
        Post post = postSearch.getPost(index);
        tagList.clear();
        copyrightIndex = setCategoryTags("Copyrights:", post.getTagStringCopyright());
        characterIndex = setCategoryTags("Characters:", post.getTagStringCharacter());
        artistIndex = setCategoryTags("Artist:", post.getTagStringArtist());
        generalIndex = setCategoryTags("Tags:", post.getTagStringGeneral());
        notifyDataSetChanged();
    }

    private int setCategoryTags(String categoryLabel, String tags) {
        int index;
        Timber.d(tags);
        if (tags.trim().isEmpty()) {
            index = -1;
        } else {
            index = tagList.size();
            tagList.add(categoryLabel);
            Collections.addAll(tagList, tags.split(" "));
        }
        return index;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == copyrightIndex) {
            return R.layout.item_post_label;
        } else if (position < characterIndex) {
            return R.layout.item_post_tag_copyright;
        } else if (position == characterIndex) {
            return R.layout.item_post_label;
        } else if (position < artistIndex) {
            return R.layout.item_post_tag_character;
        } else if (position == artistIndex) {
            return R.layout.item_post_label;
        } else if (position < generalIndex) {
            return R.layout.item_post_tag_artist;
        } else if (position == generalIndex) {
            return R.layout.item_post_label;
        } else {
            return R.layout.item_post_tag_general;
        }
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
                String tagString = tagList.get(adapterPosition);
                onItemClickListener.onEvent(tagString);
            });
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String itemString = tagList.get(position);
        TextView textView = (TextView) holder.itemView;
        textView.setText(itemString);
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
