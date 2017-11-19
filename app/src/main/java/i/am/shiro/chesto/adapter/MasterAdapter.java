package i.am.shiro.chesto.adapter;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayoutManager;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.listener.Listener1;
import i.am.shiro.chesto.loader.DanbooruSearchLoader;
import i.am.shiro.chesto.model.Post;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 */

public class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.ViewHolder> {

    private Fragment parentFragment;

    private DanbooruSearchLoader searchLoader;

    private Listener1<Integer> onItemClickedListener;

    public MasterAdapter(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void setData(DanbooruSearchLoader searchLoader) {
        this.searchLoader = searchLoader;
    }

    public void setOnItemClickedListener(Listener1<Integer> listener) {
        onItemClickedListener = listener;
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
        if (position >= searchLoader.getResultSize() - 15) {
            searchLoader.load();
        }

        Post post = searchLoader.getResult(position);
        ImageView imageView = (ImageView) holder.itemView;

        FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) imageView.getLayoutParams();
        flexboxLp.width = post.getThumbWidth();
        flexboxLp.height = post.getThumbHeight();
        flexboxLp.setMaxWidth(post.getThumbMaxWidth());

        RequestOptions requestOptions = RequestOptions
                .bitmapTransform(new RoundedCornersTransformation(4, 0))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_broken);

        Glide.with(parentFragment)
                .load(post.getThumbFileUrl())
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return searchLoader.getResultSize();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(v -> onItemClickedListener.onEvent(getAdapterPosition()));
        }
    }
}
