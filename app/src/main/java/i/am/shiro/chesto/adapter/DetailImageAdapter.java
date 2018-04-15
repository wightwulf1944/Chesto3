package i.am.shiro.chesto.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import java.util.List;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.listener.Listener0;
import i.am.shiro.chesto.listener.Listener1;
import i.am.shiro.chesto.model.Post;
import jp.wasabeef.glide.transformations.BlurTransformation;
import timber.log.Timber;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.errorOf;

/**
 * Created by Subaru Tashiro on 7/11/2017.
 */

public class DetailImageAdapter extends ListAdapter<Post, DetailImageAdapter.ViewHolder> {

    private final FragmentActivity parent;

    private Listener1<Integer> onItemBindListener;

    public DetailImageAdapter(FragmentActivity parent) {
        super(new DiffCallback());
        this.parent = parent;
    }

    public void setOnItemBindListener(Listener1<Integer> listener) {
        onItemBindListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_detail_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        onItemBindListener.onEvent(position);

        Post post = getItem(position);
        holder.bind(post);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }

        private void bind(Post post) {
            RequestBuilder<Drawable> thumb = Glide.with(parent)
                    .load(post.getThumbFileUrl())
                    .apply(bitmapTransform(new BlurTransformation(1)))
                    .apply(diskCacheStrategyOf(DATA));

            Glide.with(parent)
                    .load(post.getPreviewFileUrl())
                    .apply(errorOf(R.drawable.image_broken))
                    .thumbnail(thumb)
                    .into((ImageView) itemView);
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<Post> {

        @Override
        public boolean areItemsTheSame(Post oldItem, Post newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(Post oldItem, Post newItem) {
            return true;
        }
    }
}