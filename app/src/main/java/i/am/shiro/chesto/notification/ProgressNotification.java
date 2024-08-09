package i.am.shiro.chesto.notification;

import static androidx.core.app.NotificationCompat.CATEGORY_SERVICE;
import static androidx.core.app.NotificationCompat.PRIORITY_LOW;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import i.am.shiro.chesto.R;
import i.am.shiro.chesto.framework.Notification;

public final class ProgressNotification implements Notification {

    private int downloadsRemaining;

    public ProgressNotification(int downloadsRemaining) {
        this.downloadsRemaining = downloadsRemaining;
    }

    @NonNull
    @Override
    public android.app.Notification onCreateNotification(Context context) {
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Downloading")
                .setContentText(String.format("%s remaining", downloadsRemaining))
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setLocalOnly(true)
                .setOngoing(true)
                .setPriority(PRIORITY_LOW)
                .setCategory(CATEGORY_SERVICE)
                .setProgress(0, 0, true)
                .build();
    }
}
