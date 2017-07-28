package i.am.shiro.chesto.servicedownload;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import i.am.shiro.chesto.models.Post;

/**
 * Created by Subaru Tashiro on 7/28/2017.
 */

public final class DownloadService extends IntentService {

    public static void queue(Context context, Post post) {
        Intent starter = new Intent(context, DownloadService.class);
        starter.putExtra("url", post.getOriginalFileUrl());
        starter.putExtra("filename", post.getFileName());
        starter.putExtra("width", post.getWidth());
        starter.putExtra("height", post.getHeight());
        context.startService(starter);
    }

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    public void onCreate() {
        // TODO: bind a notification here
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        // TODO notify notification here
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // TODO download here
    }
}
