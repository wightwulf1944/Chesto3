package i.am.shiro.chesto.model;

import com.squareup.moshi.Json;

/**
 * Created by Subaru Tashiro on 7/18/2017.
 * Java representation of json response for Moshi consumption
 */

public class TagJson {

    @Json(name = "id") int id;
    @Json(name = "name") String name;
    @Json(name = "post_count") int postCount;
    @Json(name = "category") int category;
}
