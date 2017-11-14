package i.am.shiro.chesto.activitymain.fragmentdetail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.loader.DanbooruSearchLoader;
import i.am.shiro.chesto.models.Post;
import jp.wasabeef.glide.transformations.BlurTransformation;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 7/11/2017.
 */

final class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ViewHolder> {

    private DanbooruSearchLoader searchLoader;

    void setData(DanbooruSearchLoader searchLoader) {
        this.searchLoader = searchLoader;
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
        if (position >= searchLoader.getResultSize() - 5) {
            searchLoader.load();
        }

        Timber.d("Position %s binded", position);

        Post post = searchLoader.getResult(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return searchLoader.getResultSize();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }

        private void bind(Post post) {
            AppCompatActivity parentActivity = (AppCompatActivity) itemView.getContext();

            RequestBuilder<Drawable> thumb = Glide.with(parentActivity)
                    .load(post.getThumbFileUrl())
                    .apply(RequestOptions
                            .bitmapTransform(new BlurTransformation(1))
                            .diskCacheStrategy(DiskCacheStrategy.DATA));

            Glide.with(parentActivity)
                    .load(post.getPreviewFileUrl())
                    .apply(RequestOptions.errorOf(R.drawable.image_broken))
                    .thumbnail(thumb)
                    .into((ImageView) itemView);
        }
    }

}
