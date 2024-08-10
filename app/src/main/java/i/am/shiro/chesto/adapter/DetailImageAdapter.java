package i.am.shiro.chesto.adapter;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;

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
import com.bumptech.glide.RequestManager;

import java.util.function.IntConsumer;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.model.Post;

/**
 * Created by Subaru Tashiro on 7/11/2017.
 */

public class DetailImageAdapter extends ListAdapter<Post, DetailImageAdapter.ViewHolder> {

    private final RequestManager requestManager;

    private final LayoutInflater inflater;

    private IntConsumer onItemBindListener;

    public DetailImageAdapter(FragmentActivity parent) {
        super(new DiffCallback());
        requestManager = Glide.with(parent);
        inflater = parent.getLayoutInflater();
    }

    public void setOnItemBindListener(IntConsumer listener) {
        onItemBindListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
            RequestBuilder<Drawable> thumb = requestManager
                    .load(post.getThumbFileUrl())
                    .diskCacheStrategy(DATA);

            requestManager
                    .load(post.getPreviewFileUrl())
                    .error(R.drawable.image_broken)
                    .thumbnail(thumb)
                    .into((ImageView) itemView);
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<Post> {

        @Override
        public boolean areItemsTheSame(Post oldItem, @NonNull Post newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return true;
        }
    }
}