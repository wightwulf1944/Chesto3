package i.am.shiro.chesto.servicedownload;

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

import i.am.shiro.chesto.models.Post;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 7/28/2017.
 */

public final class DownloadService extends IntentService {

    private NotificationHelper notificationHelper;

    public static void queue(Context context, Post post) {
        Intent starter = new Intent(context, DownloadService.class);
        starter.putExtra("id", post.getId());
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
        super.onCreate();
        notificationHelper = new NotificationHelper(this);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        notificationHelper.notifyDownloadQueued();
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

            notificationHelper.notifyDownloadSuccess(downloadInfo, targetFile);
        } catch (Exception e) {
            notificationHelper.notifyDownloadFailed(downloadInfo);
            Timber.e(e, "Download error: %s", downloadInfo.url);
        }

        notificationHelper.notifyDownloadDone();
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
