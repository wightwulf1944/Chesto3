package i.am.shiro.chesto.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Subaru Tashiro on 7/18/2017.
 */

public class Post extends RealmObject {

    private static final String baseUrl = "http://danbooru.donmai.us";

    @PrimaryKey
    private int id;

    private int width;
    private int height;
    private int thumbWidth;
    private int thumbHeight;
    private int thumbMaxWidth;

    private String fileName;

    private String tagStringArtist;
    private String tagStringCharacter;
    private String tagStringCopyright;
    private String tagStringGeneral;
    private String tagStringMeta;

    private String webUrl;
    private String thumbFileUrl;
    private String previewFileUrl;
    private String originalFileUrl;

    private boolean isPreviewDownsized;

    public Post() {
        // no arg constructor required by Realm
    }

    public Post(PostJson postJson) {
        id = postJson.id;
        width = postJson.width;
        height = postJson.height;
        fileName = postJson.id + "." + postJson.fileExt;
        tagStringArtist = postJson.tagStringArtist;
        tagStringCharacter = postJson.tagStringCharacter;
        tagStringCopyright = postJson.tagStringCopyright;
        tagStringGeneral = postJson.tagStringGeneral;
        tagStringMeta = postJson.tagStringMeta;
        webUrl = baseUrl + "/posts/" + postJson.id;
        thumbFileUrl = baseUrl + postJson.previewFileUrl;
        previewFileUrl = baseUrl + postJson.largeFileUrl;
        originalFileUrl = baseUrl + postJson.fileUrl;
        isPreviewDownsized = postJson.hasLarge;

        final int maxMeasure = 200;
        if (width > height) {
            thumbWidth = maxMeasure;
            thumbHeight = (thumbWidth * height) / width;
        } else {
            thumbHeight = maxMeasure;
            thumbWidth = (thumbHeight * width) / height;
        }
        thumbMaxWidth = (maxMeasure * width) / height;
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

    public int getThumbWidth() {
        return thumbWidth;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public int getThumbMaxWidth() {
        return thumbMaxWidth;
    }

    public String getFileName() {
        return fileName;
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

    public String getTagStringMeta() {
        return tagStringMeta;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getThumbFileUrl() {
        return thumbFileUrl;
    }

    public String getPreviewFileUrl() {
        return previewFileUrl;
    }

    public String getOriginalFileUrl() {
        return originalFileUrl;
    }

    public boolean isPreviewDownsized() {
        return isPreviewDownsized;
    }

    public boolean hasFileUrl() {
        return thumbFileUrl != null && previewFileUrl != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Post)) {
            return false;
        } else {
            Post post = (Post) o;
            return id == post.id;
        }
    }

    @Override
    public int hashCode() {
        return id;
    }
}
