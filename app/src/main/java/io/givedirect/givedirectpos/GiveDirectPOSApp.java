package io.givedirect.givedirectpos;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.moshi.Moshi;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.givedirect.givedirectpos.dagger.component.AppComponent;
import io.givedirect.givedirectpos.dagger.component.DaggerAppComponent;
import timber.log.Timber;

public class GiveDirectPOSApp extends Application implements HasActivityInjector {
    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Inject
    Moshi moshi;

    @Nullable
    private static GiveDirectPOSApp application;

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        application = this;
        appComponent = DaggerAppComponent
                .builder()
                .application(this)
                .build();
        appComponent.inject(this);

        if (EnvironmentConstants.IS_PRODUCTION) {
            Timber.plant(new CrashReportingTree());
        } else {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @NonNull
    public static GiveDirectPOSApp getInstance() {
        if (application == null) {
            application = new GiveDirectPOSApp();
        }

        return application;
    }

    public Moshi getMoshi() {
        return moshi;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    private class CrashReportingTree extends Timber.Tree {

        @Override
        protected void log(int priority,
                           @Nullable String tag,
                           @NonNull String message,
                           @Nullable Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            // TODO: Production crash reporting
            /*FakeCrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t);
                }
            }*/
        }
    }
}
