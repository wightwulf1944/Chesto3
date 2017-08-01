package i.am.shiro.chesto.servicedownload;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import java.io.File;

import i.am.shiro.chesto.R;

import static android.support.v4.app.NotificationCompat.BigPictureStyle;
import static android.support.v4.app.NotificationCompat.Builder;
import static android.support.v4.app.NotificationCompat.CATEGORY_SERVICE;
import static android.support.v4.app.NotificationCompat.PRIORITY_LOW;

/**
 * Created by Subaru Tashiro on 8/1/2017.
 */

final class NotificationHelper {

    private static final int NOTIFICATION_ID = 1;

    private final Service parentService;
    private final NotificationManager manager;
    private int downloadsQueued;
    private int downloadsDone;
    private int downloadsSuccessful;
    private File latestDownloadedFile;

    NotificationHelper(Service service) {
        parentService = service;
        manager = (NotificationManager) parentService.getSystemService(Context.NOTIFICATION_SERVICE);
        parentService.startForeground(NOTIFICATION_ID, makeProgressNotification());
    }

    void notifyDownloadQueued() {
        downloadsQueued++;
        manager.notify(NOTIFICATION_ID, makeProgressNotification());
    }

    void notifyDownloadSuccess(File file) {
        downloadsDone++;
        manager.notify(NOTIFICATION_ID, makeProgressNotification());

        downloadsSuccessful++;
        latestDownloadedFile = file;
    }

    void notifyDownloadFailed(DownloadInfo downloadInfo) {
        downloadsDone++;
        manager.notify(NOTIFICATION_ID, makeProgressNotification());

        // TODO show failed notification with random id
    }

    void notifyQueueFinished() {
        if (downloadsSuccessful == 1) {
            manager.notify(NOTIFICATION_ID, makeSingleSuccessNotification());
        } else if (downloadsSuccessful > 1) {
            manager.notify(NOTIFICATION_ID, makeMultipleSuccessNotification());
        }
    }

    private Notification makeProgressNotification() {
        String contentInfo = String.format("%s/%s", downloadsDone, downloadsQueued);
        String contentText = "Saving image";
        if (downloadsQueued > 1) {
            contentText = contentText + "s";
        }

        return makeBaseNotification()
                .setContentText(contentText)
                .setOngoing(true)
                .setPriority(PRIORITY_LOW)
                .setCategory(CATEGORY_SERVICE)
                .setProgress(downloadsQueued, downloadsDone, false)
                .setContentInfo(contentInfo)
                .build();
    }

    private Notification makeSingleSuccessNotification() {
        Uri uri = Uri.fromFile(latestDownloadedFile);
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setDataAndType(uri, "image/*");
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(
                parentService,
                0,
                viewIntent,
                PendingIntent.FLAG_ONE_SHOT
        );

        String contentText = "Image saved. Tap to View.";

        String absolutePath = latestDownloadedFile.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
        BigPictureStyle bigPictureStyle = new BigPictureStyle()
                .setSummaryText(contentText)
                .bigPicture(bitmap);

        return makeBaseNotification()
                .setContentText(contentText)
                .setAutoCancel(true)
                .setContentIntent(viewPendingIntent)
                .setStyle(bigPictureStyle)
                .build();
    }

    private Notification makeMultipleSuccessNotification() {
        return makeBaseNotification()
                .setContentText("Images Saved")
                .setAutoCancel(true)
                .build();
    }

    private Builder makeBaseNotification() {
        String notificationTitle = parentService.getString(R.string.app_name);
        int notificationColor = ContextCompat.getColor(parentService, R.color.primary);

        return new Builder(parentService)
                .setContentTitle(notificationTitle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(notificationColor)
                .setLocalOnly(true);
    }
}
