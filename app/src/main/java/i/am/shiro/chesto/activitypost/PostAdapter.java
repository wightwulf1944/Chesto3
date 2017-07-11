package i.am.shiro.chesto.activitypost;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.engine.PostSearch;
import i.am.shiro.chesto.models.Post;
import jp.wasabeef.glide.transformations.BlurTransformation;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 7/11/2017.
 */

final class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private PostSearch searchResults;

    void setData(PostSearch searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= searchResults.size() - 5) {
            searchResults.load();
        }

        Timber.d("Position %s binded", position);

        Post post = searchResults.getPost(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }

        private void bind(Post post) {
            AppCompatActivity parentActivity = (AppCompatActivity) itemView.getContext();

            BlurTransformation blurTransformation = new BlurTransformation(parentActivity, 1);

            DrawableRequestBuilder thumb = Glide.with(parentActivity)
                    .load(post.getSmallFileUrl())
                    .bitmapTransform(blurTransformation)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE);

            Glide.with(parentActivity)
                    .load(post.getLargeFileUrl())
                    .error(R.drawable.image_broken)
                    .thumbnail(thumb)
                    .into((ImageView) itemView);
        }
    }

}
