package i.am.shiro.chesto.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.annimon.stream.function.IntConsumer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayoutManager;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.model.Post;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.ALL;

public class MasterAdapter extends ListAdapter<Post, MasterAdapter.ViewHolder> {

    private final FragmentActivity parentActivity;

    private final RequestOptions defaultRequestOptions;

    private IntConsumer onItemClickListener;

    private IntConsumer onItemBindListener;

    public MasterAdapter(FragmentActivity parentActivity) {
        super(new DiffCallback());
        this.parentActivity = parentActivity;
        defaultRequestOptions = new RequestOptions()
                .transform(new RoundedCornersTransformation(5, 0))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_broken)
                .diskCacheStrategy(ALL);
    }

    public void setOnItemClickListener(IntConsumer listener) {
        onItemClickListener = listener;
    }

    public void setOnItemBindListener(IntConsumer listener) {
        onItemBindListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_master_thumbs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        onItemBindListener.accept(position);

        Post post = getItem(position);
        ImageView imageView = (ImageView) holder.itemView;

        FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) imageView.getLayoutParams();
        flexboxLp.width = post.getThumbWidth();
        flexboxLp.height = post.getThumbHeight();
        flexboxLp.setMaxWidth(post.getThumbMaxWidth());
        flexboxLp.setFlexGrow(post.getThumbFlexGrow());

        Glide.with(parentActivity)
                .load(post.getThumbFileUrl())
                .apply(defaultRequestOptions)
                .into(imageView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(v -> onItemClick());
        }

        private void onItemClick() {
            onItemClickListener.accept(getAdapterPosition());
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
