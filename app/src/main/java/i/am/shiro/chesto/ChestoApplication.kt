package i.am.shiro.chesto

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber

/**
 * Created by Shiro on 5/3/2017.
 */
class ChestoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) onCreateDebug()

        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .compactOnLaunch()
            .build()
        Realm.setDefaultConfiguration(config)
    }

    private fun onCreateDebug() {
        Timber.plant(Timber.DebugTree())
    }
}
