package i.am.shiro.chesto.adapter;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.errorOf;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import java.util.function.IntConsumer;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.model.Post;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by Subaru Tashiro on 7/11/2017.
 */

public class DetailImageAdapter extends ListAdapter<Post, DetailImageAdapter.ViewHolder> {

    private final FragmentActivity parent;

    private IntConsumer onItemBindListener;

    public DetailImageAdapter(FragmentActivity parent) {
        super(new DiffCallback());
        this.parent = parent;
    }

    public void setOnItemBindListener(IntConsumer listener) {
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
        onItemBindListener.accept(position);

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