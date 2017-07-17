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

    private static int MAX_THUMB_HEIGHT = 220;
    private static int MAX_THUMB_WIDTH = 220;
    private static int MIN_THUMB_HEIGHT = 100;
    private static int MIN_THUMB_WIDTH = 100;

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

        int originalHeight = post.getHeight();
        int originalWidth = post.getWidth();

        FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) imageView.getLayoutParams();
        if (originalWidth > originalHeight) {
            flexboxLp.width = MAX_THUMB_WIDTH;
            flexboxLp.height = Math.max(MIN_THUMB_HEIGHT, (flexboxLp.width * originalHeight) / originalWidth);
            flexboxLp.setFlexGrow(1.0f);
            flexboxLp.setMaxWidth(300);
        } else {
            flexboxLp.height = MAX_THUMB_HEIGHT;
            flexboxLp.width = Math.max(MIN_THUMB_WIDTH, (flexboxLp.height * originalWidth) / originalHeight);
        }

        RoundedCornersTransformation roundedCornersTransformation = new RoundedCornersTransformation(parentActivity, 4, 0);

        Glide.with(parentActivity)
                .load(post.getSmallFileUrl())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_broken)
                .dontAnimate()
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
