package i.am.shiro.chesto.activitypost;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import i.am.shiro.chesto.models.Post;

/**
 * Created by Subaru Tashiro on 7/19/2017.
 * TODO: step 1, have setData() populate data list with labels and tags
 * TODO: step 2, differentiate between different item types
 */

final class PostTagAdapter extends RecyclerView.Adapter<PostTagAdapter.ViewHolder> {

    List<String> data = new ArrayList<>();

    void setData(Post post) {
        data.add("Copyrights:");
        Collections.addAll(data, post.getTagStringCopyright().split(" "));
        data.add("Characters:");
        Collections.addAll(data, post.getTagStringCharacter().split(" "));
        data.add("Artist:");
        Collections.addAll(data, post.getTagStringArtist().split(" "));
        data.add("Tags:");
        Collections.addAll(data, post.getTagStringGeneral().split(" "));

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setTextColor(Color.RED);
        return new ViewHolder(textView);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
