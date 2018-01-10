package i.am.shiro.chesto.model;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Shiro on 5/4/2017.
 */

public interface Danbooru {

    @GET("posts.json?limit=100")
    Single<List<PostJson>> getPosts(@Query("tags") String tags, @Query("page") int page);

    @GET("tags.json?search[order]=count&search[hide_empty]=yes")
    Single<List<TagJson>> searchTags(@Query("search[name_matches]") String tags);
}
