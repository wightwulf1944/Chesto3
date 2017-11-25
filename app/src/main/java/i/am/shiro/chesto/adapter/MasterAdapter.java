package i.am.shiro.chesto.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.listener.Listener0;
import i.am.shiro.chesto.listener.Listener1;
import i.am.shiro.chesto.model.Post;
import i.am.shiro.chesto.notifier.Notifier1;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.ALL;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.errorOf;
import static com.bumptech.glide.request.RequestOptions.formatOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 */

public class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.ViewHolder> {

    private final Notifier1<Integer> onItemClickedNotifier = new Notifier1<>();

    private final Fragment parentFragment;

    private Listener0 onScrollToThresholdListener;

    private List<Post> data;

    private int scrollThreshold;

    public MasterAdapter(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void setData(List<Post> data) {
        this.data = data;
    }

    public void addOnItemClickedListener(Listener1<Integer> listener) {
        onItemClickedNotifier.addListener(listener);
    }

    public void setOnScrollToThresholdListener(int threshold, Listener0 listener) {
        scrollThreshold = threshold;
        onScrollToThresholdListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_main_thumbs, parent, false);

        FlexboxLayoutManager.LayoutParams layoutParams = (FlexboxLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.setMinWidth(100);
        layoutParams.setMinHeight(150);
        layoutParams.setFlexGrow(1.0f);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= data.size() - scrollThreshold) {
            onScrollToThresholdListener.onEvent();
        }

        Post post = data.get(position);
        ImageView imageView = (ImageView) holder.itemView;

        FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) imageView.getLayoutParams();
        flexboxLp.width = post.getThumbWidth();
        flexboxLp.height = post.getThumbHeight();
        flexboxLp.setMaxWidth(post.getThumbMaxWidth());

        Glide.with(parentFragment)
                .load(post.getThumbFileUrl())
                .apply(bitmapTransform(new RoundedCornersTransformation(5, 0)))
                .apply(placeholderOf(R.drawable.image_placeholder))
                .apply(errorOf(R.drawable.image_broken))
                .apply(diskCacheStrategyOf(ALL))
                .apply(formatOf(PREFER_RGB_565))
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(v ->
                    onItemClickedNotifier.fireEvent(getAdapterPosition())
            );
        }
    }
}
