package i.am.shiro.chesto.models;

import android.net.Uri;

import com.squareup.moshi.Json;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 5/2/2017.
 */

public class Post extends RealmObject {

    @PrimaryKey
    @Json(name = "id")
    private int id;

    @Json(name = "image_width")
    private int width;
    @Json(name = "image_height")
    private int height;
    @Json(name = "file_ext")
    private String fileExt;

    @Json(name = "tag_string_artist")
    private String tagStringArtist;
    @Json(name = "tag_string_character")
    private String tagStringCharacter;
    @Json(name = "tag_string_copyright")
    private String tagStringCopyright;
    @Json(name = "tag_string_general")
    private String tagStringGeneral;

    @Json(name = "has_large")
    private boolean hasLarge;
    @Json(name = "preview_file_url")
    private String smallFileUrl;
    @Json(name = "large_file_url")
    private String largeFileUrl;
    @Json(name = "file_url")
    private String originalFileUrl;

    private static final String BASE_URL = "http://danbooru.donmai.us";

    public boolean hasFileUrl() {
        return smallFileUrl != null && largeFileUrl != null;
    }

    public String getWebUrl() {
        return BASE_URL + "/posts/" + id;
    }

    public Uri getWebUri() {
        return Uri.parse(getWebUrl());
    }

    public String getFileName() {
        return String.format("%s.%s", id, fileExt);
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTagStringArtist() {
        return tagStringArtist;
    }

    public String getTagStringCharacter() {
        return tagStringCharacter;
    }

    public String getTagStringCopyright() {
        return tagStringCopyright;
    }

    public String getTagStringGeneral() {
        return tagStringGeneral;
    }

    public boolean hasLargeFileUrl() {
        return hasLarge;
    }

    public String getSmallFileUrl() {
        return BASE_URL + smallFileUrl;
    }

    public String getLargeFileUrl() {
        return BASE_URL + largeFileUrl;
    }

    public String getOriginalFileUrl() {
        return BASE_URL + originalFileUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Post)) {
            return false;
        } else {
            Post post = (Post) o;
            return id == post.getId();
        }
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
