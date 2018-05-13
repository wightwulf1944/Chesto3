package i.am.shiro.chesto.model;

import i.am.shiro.chesto.retrofit.Danbooru;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Subaru Tashiro on 7/18/2017.
 */

public class Post extends RealmObject {

    @PrimaryKey
    private int id;

    private int width;
    private int height;
    private int thumbWidth;
    private int thumbHeight;
    private int thumbMaxWidth;
    private float thumbFlexGrow;

    private String fileName;

    private int tagCount;
    private String tagStringArtist;
    private String tagStringCharacter;
    private String tagStringCopyright;
    private String tagStringGeneral;
    private String tagStringMeta;

    private String webUrl;
    private String thumbFileUrl;
    private String previewFileUrl;
    private String originalFileUrl;

    public Post() {
        // no arg constructor required by Realm
    }

    public Post(PostJson postJson) {
        id = postJson.id;
        width = postJson.width;
        height = postJson.height;
        fileName = postJson.id + "." + postJson.fileExt;
        tagCount = postJson.tagCount;
        tagStringArtist = postJson.tagStringArtist;
        tagStringCharacter = postJson.tagStringCharacter;
        tagStringCopyright = postJson.tagStringCopyright;
        tagStringGeneral = postJson.tagStringGeneral;
        tagStringMeta = postJson.tagStringMeta;
        webUrl = Danbooru.BASE_URL + "/posts/" + postJson.id;
        thumbFileUrl = postJson.previewFileUrl;
        previewFileUrl = postJson.largeFileUrl;
        originalFileUrl = postJson.fileUrl;

        final int maxLength = 220;
        if (width > height) {
            thumbWidth = maxLength;
            thumbHeight = (thumbWidth * height) / width;
            thumbMaxWidth = (maxLength * width) / height;
            thumbFlexGrow = (float) width / (float) height;
        } else {
            thumbHeight = maxLength;
            thumbWidth = (thumbHeight * width) / height;
            thumbMaxWidth = thumbWidth;
            thumbFlexGrow = 0.0f;
        }
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

    public float getThumbFlexGrow() {
        return thumbFlexGrow;
    }

    public String getFileName() {
        return fileName;
    }

    public int getTagCount() {
        return tagCount;
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
