package i.am.shiro.chesto.adapter;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.listener.Listener0;
import i.am.shiro.chesto.model.Post;
import jp.wasabeef.glide.transformations.BlurTransformation;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 7/11/2017.
 */

public final class DetailImageAdapter extends RecyclerView.Adapter<DetailImageAdapter.ViewHolder> {

    private final Fragment parentFragment;

    private Listener0 onScrollToThresholdListener;

    private List<Post> data;

    private int scrollThreshold;

    public DetailImageAdapter(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void setData(List<Post> data) {
        this.data = data;
    }

    public void setOnScrollToThresholdListener(int threshold, Listener0 listener) {
        scrollThreshold = threshold;
        onScrollToThresholdListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_post_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= data.size() - scrollThreshold) {
            onScrollToThresholdListener.onEvent();
        }

        Timber.d("Position %s binded", position);

        Post post = data.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }

        private void bind(Post post) {
            RequestBuilder<Drawable> thumb = Glide.with(parentFragment)
                    .load(post.getThumbFileUrl())
                    .apply(RequestOptions
                            .bitmapTransform(new BlurTransformation(1))
                            .diskCacheStrategy(DiskCacheStrategy.DATA));

            Glide.with(parentFragment)
                    .load(post.getPreviewFileUrl())
                    .apply(RequestOptions.errorOf(R.drawable.image_broken))
                    .thumbnail(thumb)
                    .into((ImageView) itemView);
        }
    }

}
