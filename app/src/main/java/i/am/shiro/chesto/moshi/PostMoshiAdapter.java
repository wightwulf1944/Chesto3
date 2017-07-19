package i.am.shiro.chesto.moshi;

import com.squareup.moshi.FromJson;

import i.am.shiro.chesto.models.Post;
import i.am.shiro.chesto.models.PostJson;

/**
 * Created by Subaru Tashiro on 7/19/2017.
 */

public class PostMoshiAdapter {

    private String baseUrl;

    public PostMoshiAdapter(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @FromJson
    Post fromJson(PostJson postJson) {
        return new Post(postJson, baseUrl);
    }
}
