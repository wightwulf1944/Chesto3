package i.am.shiro.chesto;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * Created by Shiro on 5/3/2017.
 */

public class ChestoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) onCreateDebug();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .compactOnLaunch()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private void onCreateDebug() {
        Timber.plant(new Timber.DebugTree());
    }
}
