package i.am.shiro.chesto.retrofit;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BASIC;

import java.util.List;

import i.am.shiro.chesto.model.PostJson;
import i.am.shiro.chesto.model.TagJson;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Shiro on 5/4/2017.
 */

public class Danbooru {

    public static final String BASE_URL = "https://danbooru.donmai.us";

    public static final Api API = buildApi();

    private static Api buildApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
                .setLevel(BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        return new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(Api.class);
    }

    public interface Api {

        @GET("posts.json?limit=100")
        Single<List<PostJson>> getPosts(@Query("tags") String tags, @Query("page") int page);

        @GET("tags.json?search[order]=count&search[hide_empty]=yes")
        Single<List<TagJson>> searchTags(@Query("search[name_matches]") String tags);
    }
}
