package i.am.shiro.chesto.service;

import static android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE;
import static android.os.Environment.DIRECTORY_PICTURES;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import i.am.shiro.chesto.framework.NotificationManager;
import i.am.shiro.chesto.framework.ServiceNotificationManager;
import i.am.shiro.chesto.model.Post;
import i.am.shiro.chesto.notification.ErrorNotification;
import i.am.shiro.chesto.notification.ProgressNotification;
import i.am.shiro.chesto.notification.SuccessNotification;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 7/28/2017.
 */

public final class DownloadService extends IntentService {

    private static final int NOTIFICATION_ID = 1;

    private ServiceNotificationManager notificationManager;

    private int downloadsRemaining;

    public DownloadService() {
        super("DownloadService");
    }

    public static void queue(Context context, Post post) {
        Intent starter = new Intent(context, DownloadService.class);
        starter.putExtra("id", post.getId());
        starter.putExtra("url", post.getOriginalFileUrl());
        starter.putExtra("filename", post.getFileName());
        context.startService(starter);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = new ServiceNotificationManager(this, NOTIFICATION_ID);
        notificationManager.startForeground(new ProgressNotification(downloadsRemaining));
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        notificationManager.notify(new ProgressNotification(++downloadsRemaining));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) throw new NullPointerException("DownloadService intent is null");

        final int id = intent.getIntExtra("id", -1);
        final String url = intent.getStringExtra("url");
        final String filename = intent.getStringExtra("filename");

        NotificationManager uniqueNotificationManager = new NotificationManager(this, id);
        try {
            File sourceFile = getSourceFile(url);
            File targetFile = getTargetFile(filename);
            copy(sourceFile, targetFile);

            Uri fileUri = Uri.fromFile(targetFile);
            Intent newImageIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri);
            sendBroadcast(newImageIntent);

            uniqueNotificationManager.notify(new SuccessNotification(targetFile));
        } catch (Exception e) {
            uniqueNotificationManager.notify(new ErrorNotification());
            Timber.e(e, "Download error: %s", url);
        }

        notificationManager.notify(new ProgressNotification(--downloadsRemaining));
    }

    private File getSourceFile(String url) throws Exception {
        return Glide.with(this)
                .download(url)
                .submit()
                .get();
    }

    private File getTargetFile(String filename) {
        File picturesDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        File saveDir = new File(picturesDir, "Chesto");
        if (!saveDir.mkdirs()) {
            Timber.d("getTargetFile: saveDir not created");
        }
        return new File(saveDir, filename);
    }

    private void copy(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inChannel.close();
        outChannel.close();
    }
}
