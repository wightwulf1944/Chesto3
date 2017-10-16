package i.am.shiro.chesto;

import android.app.Application;
import android.os.StrictMode;

import i.am.shiro.chesto.models.Danbooru;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

/**
 * Created by Shiro on 5/3/2017.
 */

public class ChestoApplication extends Application {

    private static Danbooru danbooru;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());

            StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setThreadPolicy(threadPolicy);

            StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setVmPolicy(vmPolicy);
        }

        Realm.init(this);

        initDanbooru();
    }

    private void initDanbooru() {
        String baseUrl = "http://safebooru.donmai.us";

        Scheduler ioScheduler = Schedulers.io();
        RxJava2CallAdapterFactory callAdapter = RxJava2CallAdapterFactory.createWithScheduler(ioScheduler);

        MoshiConverterFactory converter = MoshiConverterFactory.create();

        danbooru = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(callAdapter)
                .addConverterFactory(converter)
                .build()
                .create(Danbooru.class);
    }

    public static Danbooru danbooru() {
        return danbooru;
    }
}
