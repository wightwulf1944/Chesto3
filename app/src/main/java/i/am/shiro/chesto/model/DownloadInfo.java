package i.am.shiro.chesto.model;

import android.content.Intent;

/**
 * Created by Subaru Tashiro on 8/1/2017.
 */

public final class DownloadInfo {

    public final int id;
    public final String url;
    public final String filename;

    public DownloadInfo(Intent intent) {
        id = intent.getIntExtra("id", -1);
        url = intent.getStringExtra("url");
        filename = intent.getStringExtra("filename");
    }
}
