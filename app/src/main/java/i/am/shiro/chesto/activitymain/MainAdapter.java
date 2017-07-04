package i.am.shiro.chesto.activitymain;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.listeners.Listener1;
import i.am.shiro.chesto.models.Post;

/**
 * Created by Shiro on 5/2/2017.
 */

final class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private PostSearch searchResults;
    private Listener1<Integer> onItemClickedListener;

    void setData(PostSearch searchResults) {
        this.searchResults = searchResults;
    }

    void setOnItemClickedListener(Listener1<Integer> listener) {
        onItemClickedListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        ImageView view = new ImageView(context);
        ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(v -> onItemClickedListener.onEvent(vh.getAdapterPosition()));
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= searchResults.size() - 15) {
            searchResults.load();
        }

        Post post = searchResults.getPost(position);
        ImageView imageView = holder.imageView;
        AppCompatActivity parentActivity = (AppCompatActivity) imageView.getContext();

        Glide.with(parentActivity)
                .load(post.getSmallFileUrl())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_broken)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        private ViewHolder(ImageView view) {
            super(view);
            imageView = view;
        }
    }
}
