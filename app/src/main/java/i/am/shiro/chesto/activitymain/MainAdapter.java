package i.am.shiro.chesto.activitymain;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.flexbox.FlexboxLayoutManager;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.listeners.Listener1;
import i.am.shiro.chesto.models.Post;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_recycler_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= searchResults.size() - 15) {
            searchResults.load();
        }

        Post post = searchResults.getPost(position);
        ImageView imageView = holder.imageView;
        AppCompatActivity parentActivity = (AppCompatActivity) imageView.getContext();

        int maxHeight = 200;
        int maxWidth = 200;
        int original_height = post.getHeight();
        int original_width = post.getWidth();
        int thumb_height = original_height;
        int thumb_width = original_width;
        float flexGrow = 0.0f;

        if (thumb_height > maxHeight) {
            thumb_height = maxHeight;
            thumb_width = (thumb_height * original_width) / original_height;
        }

        if (thumb_width > maxWidth) {
            thumb_width = maxWidth;
            thumb_height = (thumb_width * original_height) / original_width;
        }

        if (thumb_width > thumb_height) {
            flexGrow = 1.0f;
        }

        FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) imageView.getLayoutParams();
        flexboxLp.setMinWidth(100);
        flexboxLp.setMinHeight(100);
        flexboxLp.setMaxWidth(300);
        flexboxLp.width = thumb_width;
        flexboxLp.height = thumb_height;
        flexboxLp.setFlexGrow(flexGrow);

        RoundedCornersTransformation roundedCornersTransformation = new RoundedCornersTransformation(parentActivity, 4, 0);

        Glide.with(parentActivity)
                .load(post.getSmallFileUrl())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_broken)
                .bitmapTransform(roundedCornersTransformation)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        private ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view;
            imageView.setOnClickListener(v -> onItemClickedListener.onEvent(getAdapterPosition()));
        }
    }
}
