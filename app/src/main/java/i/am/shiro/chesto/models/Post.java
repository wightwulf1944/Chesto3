package i.am.shiro.chesto.models;

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

    private String fileName;

    private String tagStringArtist;
    private String tagStringCharacter;
    private String tagStringCopyright;
    private String tagStringGeneral;

    private String webUrl;
    private String thumbFileUrl;
    private String previewFileUrl;
    private String originalFileUrl;

    private boolean isPreviewDownsized;

    public Post() {
        // no arg constructor required by Realm
    }

    public Post(PostJson postJson, String baseUrl) {
        id = postJson.id;
        width = postJson.width;
        height = postJson.height;
        fileName = postJson.id + '.' + postJson.fileExt;
        tagStringArtist = postJson.tagStringArtist;
        tagStringCharacter = postJson.tagStringCharacter;
        tagStringCopyright = postJson.tagStringCopyright;
        tagStringGeneral = postJson.tagStringGeneral;
        webUrl = baseUrl + "/posts/" + postJson.id;
        thumbFileUrl = baseUrl + postJson.previewFileUrl;
        previewFileUrl = baseUrl + postJson.largeFileUrl;
        originalFileUrl = baseUrl + postJson.fileUrl;
        isPreviewDownsized = postJson.hasLarge;

        final int maxThumbWidth = 220;
        final int maxThumbHeight = 220;
        final int minThumbWidth = 100;
        final int minThumbHeight = 100;
        if (isLandscape()) {
            thumbWidth = maxThumbWidth;
            thumbHeight = Math.max(minThumbHeight, (thumbWidth * height) / width);
        } else {
            thumbHeight = maxThumbHeight;
            thumbWidth = Math.max(minThumbWidth, (thumbHeight * width) / height);
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

    public boolean isLandscape() {
        return width > height;
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
