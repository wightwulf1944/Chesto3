package i.am.shiro.chesto.activitypost;

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
import i.am.shiro.chesto.models.Post;

/**
 * Created by Subaru Tashiro on 7/19/2017.
 * TODO: identify why LayoutInflater views ignores some xml attributes such as layout_wrapBefore
 */

final class PostTagAdapter extends RecyclerView.Adapter<PostTagAdapter.ViewHolder> {

    private static final int TYPE_LABEL = 0;
    private static final int TYPE_TAG = 1;

    private List<String> data = new ArrayList<>();
    private int copyrightIndex;
    private int characterIndex;
    private int artistIndex;
    private int tagIndex;

    void setData(Post post) {
        copyrightIndex = 0;
        data.add("Copyrights:");
        Collections.addAll(data, post.getTagStringCopyright().split(" "));

        characterIndex = data.size();
        data.add("Characters:");
        Collections.addAll(data, post.getTagStringCharacter().split(" "));

        artistIndex = data.size();
        data.add("Artist:");
        Collections.addAll(data, post.getTagStringArtist().split(" "));

        tagIndex = data.size();
        data.add("Tags:");
        Collections.addAll(data, post.getTagStringGeneral().split(" "));

    }

    @Override
    public int getItemViewType(int position) {
        return position == copyrightIndex ||
                position == characterIndex ||
                position == artistIndex ||
                position == tagIndex ? TYPE_LABEL : TYPE_TAG;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        if (viewType == TYPE_LABEL) {
            view = inflater.inflate(R.layout.item_post_label, parent, false);
            FlexboxLayoutManager.LayoutParams layoutParams = (FlexboxLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setWrapBefore(true);
        } else if (viewType == TYPE_TAG) {
            view = inflater.inflate(R.layout.item_post_tag, parent, false);
        } else {
            throw new RuntimeException("invalid view type");
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String itemString = data.get(position);
        TextView textView = (TextView) holder.itemView;
        textView.setText(itemString);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
