package i.am.shiro.chesto.framework;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Implement this and use {@link NotificationManager#notify(Notification)}
 */
public interface Notification {

    @NonNull
    android.app.Notification onCreateNotification(Context context);
}
