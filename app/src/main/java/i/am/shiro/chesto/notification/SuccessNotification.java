package i.am.shiro.chesto.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import java.io.File;

import i.am.shiro.chesto.BuildConfig;
import i.am.shiro.chesto.R;
import i.am.shiro.chesto.framework.Notification;

public final class SuccessNotification implements Notification {

    private final String CONTENT_TEXT = "Tap to view";

    private final File file;

    public SuccessNotification(File file) {
        this.file = file;
    }

    @NonNull
    @Override
    public android.app.Notification onCreateNotification(Context context) {
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Image saved")
                .setContentText(CONTENT_TEXT)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setLocalOnly(true)
                .setAutoCancel(true)
                .setContentIntent(getContentIntent(context))
                .setStyle(getStyle())
                .build();
    }

    private PendingIntent getContentIntent(Context context) {
        String authority = BuildConfig.APPLICATION_ID + ".fileprovider";
        Uri data = FileProvider.getUriForFile(context, authority, file);

        String type = context.getContentResolver().getType(data);

        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(data, type)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    private NotificationCompat.Style getStyle() {
        String absolutePath = file.getAbsolutePath();

        Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);

        return new NotificationCompat.BigPictureStyle()
                .setSummaryText(CONTENT_TEXT)
                .bigPicture(bitmap);
    }
}
