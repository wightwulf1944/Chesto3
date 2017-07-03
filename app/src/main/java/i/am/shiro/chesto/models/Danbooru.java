package i.am.shiro.chesto.models;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Shiro on 5/4/2017.
 */

public interface Danbooru {

    @GET("posts.json?limit=100")
    Observable<List<Post>> getPosts(@Query("tags") String tags, @Query("page") int page);

    @GET("tags.json?search[order]=count&search[hide_empty]=yes")
    Observable<List<Tag>> searchTags(@Query("search[name_matches]") String tags);
}
