package i.am.shiro.chesto.activitypost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import i.am.shiro.chesto.R;
import i.am.shiro.chesto.engine.SearchHistory;
import i.am.shiro.chesto.models.Post;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static butterknife.ButterKnife.findById;

/**
 * Created by Subaru Tashiro on 7/7/2017.
 */

public class PostActivity extends AppCompatActivity {

    @BindView(R.id.imageview) ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        setSupportActionBar(findById(this, R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        int postIndex = getIntent().getIntExtra("default", -1);
        Post post = SearchHistory.current().getPost(postIndex);

        BlurTransformation blurTransformation = new BlurTransformation(this, 1);

        DrawableRequestBuilder thumb = Glide.with(this)
                .load(post.getSmallFileUrl())
                .bitmapTransform(blurTransformation)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        Glide.with(this)
                .load(post.getLargeFileUrl())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_broken)
                .thumbnail(thumb)
                .into(imageView);
    }
}
