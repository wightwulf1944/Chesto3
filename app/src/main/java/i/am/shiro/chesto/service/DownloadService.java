package i.am.shiro.chesto.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import i.am.shiro.chesto.model.DownloadInfo;
import i.am.shiro.chesto.model.Post;
import i.am.shiro.chesto.util.NotificationUtil;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 7/28/2017.
 */

public final class DownloadService extends IntentService {

    private NotificationUtil notificationUtil;

    public DownloadService() {
        super("DownloadService");
    }

    public static void queue(Context context, Post post) {
        Intent starter = new Intent(context, DownloadService.class);
        starter.putExtra("id", post.getId());
        starter.putExtra("url", post.getOriginalFileUrl());
        starter.putExtra("filename", post.getFileName());
        starter.putExtra("width", post.getWidth());
        starter.putExtra("height", post.getHeight());
        context.startService(starter);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationUtil = new NotificationUtil(this);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        notificationUtil.notifyDownloadQueued();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        DownloadInfo downloadInfo = new DownloadInfo(intent);

        try {
            File sourceFile = getSourceFile(downloadInfo);
            File targetFile = getTargetFile(downloadInfo);
            copy(sourceFile, targetFile);

            Uri fileUri = Uri.fromFile(targetFile);
            Intent newImageIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri);
            sendBroadcast(newImageIntent);

            notificationUtil.notifyDownloadSuccess(downloadInfo, targetFile);
        } catch (Exception e) {
            notificationUtil.notifyDownloadFailed(downloadInfo);
            Timber.e(e, "Download error: %s", downloadInfo.url);
        }

        notificationUtil.notifyDownloadDone();
    }

    private File getSourceFile(DownloadInfo dlInfo) throws Exception {
        return Glide.with(this)
                .download(dlInfo.url)
                .submit()
                .get();
    }

    private File getTargetFile(DownloadInfo dlInfo) {
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File saveDir = new File(picturesDir, "Chesto");
        if (!saveDir.mkdirs()) {
            Timber.d("getTargetFile: saveDir not created");
        }
        return new File(saveDir, dlInfo.filename);
    }

    private void copy(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inChannel.close();
        outChannel.close();
    }
}
