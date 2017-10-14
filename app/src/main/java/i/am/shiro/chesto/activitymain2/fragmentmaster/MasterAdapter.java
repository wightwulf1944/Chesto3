package i.am.shiro.chesto.activitymain2.fragmentmaster;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayoutManager;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.listeners.Listener1;
import i.am.shiro.chesto.models.Post;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Subaru Tashiro on 8/11/2017.
 */

class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.ViewHolder> {

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
                .inflate(R.layout.item_main_thumbs, parent, false);

        FlexboxLayoutManager.LayoutParams layoutParams = (FlexboxLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.setMinWidth(100);
        layoutParams.setMinHeight(150);
        layoutParams.setFlexGrow(1.0f);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= searchResults.size() - 15) {
            searchResults.load();
        }

        Post post = searchResults.getPost(position);
        ImageView imageView = (ImageView) holder.itemView;
        AppCompatActivity parentActivity = (AppCompatActivity) imageView.getContext();

        FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) imageView.getLayoutParams();
        flexboxLp.width = post.getThumbWidth();
        flexboxLp.height = post.getThumbHeight();
        flexboxLp.setMaxWidth(post.getThumbMaxWidth());

        RequestOptions requestOptions = RequestOptions
                .bitmapTransform(new RoundedCornersTransformation(4, 0))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_broken)
                .diskCacheStrategy(DiskCacheStrategy.DATA);

        Glide.with(parentActivity)
                .load(post.getThumbFileUrl())
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(v -> onItemClickedListener.onEvent(getAdapterPosition()));
        }
    }
}
