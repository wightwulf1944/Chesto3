package i.am.shiro.chesto.moshi;

import com.squareup.moshi.FromJson;

import i.am.shiro.chesto.models.Tag;
import i.am.shiro.chesto.models.TagJson;

/**
 * Created by Subaru Tashiro on 7/19/2017.
 */

public class TagMoshiAdapter {

    @FromJson
    Tag fromJson(TagJson tagJson) {
        return new Tag(tagJson);
    }
}
