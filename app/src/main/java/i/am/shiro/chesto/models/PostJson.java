package i.am.shiro.chesto.models;

import com.squareup.moshi.Json;

/**
 * Created by Subaru Tashiro on 7/18/2017.
 * Java representation of json response for Moshi consumption
 */

public class PostJson {

    @Json(name = "id") int id;
    @Json(name = "image_width") int width;
    @Json(name = "image_height") int height;
    @Json(name = "file_ext") String fileExt;
    @Json(name = "tag_string_artist") String tagStringArtist;
    @Json(name = "tag_string_character") String tagStringCharacter;
    @Json(name = "tag_string_copyright") String tagStringCopyright;
    @Json(name = "tag_string_general") String tagStringGeneral;
    @Json(name = "has_large") boolean hasLarge;
    @Json(name = "preview_file_url") String previewFileUrl;
    @Json(name = "large_file_url") String largeFileUrl;
    @Json(name = "file_url") String fileUrl;

    public static boolean hasImageUrls(PostJson postJson) {
        return postJson.previewFileUrl != null;
    }
}
