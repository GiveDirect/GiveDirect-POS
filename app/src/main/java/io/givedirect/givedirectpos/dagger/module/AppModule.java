package io.givedirect.givedirectpos.dagger.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.squareup.moshi.Moshi;

import org.stellar.sdk.Network;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;
import dagger.android.ContributesAndroidInjector;
import io.givedirect.givedirectpos.EnvironmentConstants;
import io.givedirect.givedirectpos.dagger.scope.PerActivity;
import io.givedirect.givedirectpos.model.api.CurlLoggingInterceptor;
import io.givedirect.givedirectpos.model.api.GiveDirectApi;
import io.givedirect.givedirectpos.model.api.HorizonApi;
import io.givedirect.givedirectpos.model.persistence.AppFlagDB;
import io.givedirect.givedirectpos.view.about.AboutActivity;
import io.givedirect.givedirectpos.view.about.AboutActivityModule;
import io.givedirect.givedirectpos.view.home.HomeActivity;
import io.givedirect.givedirectpos.view.home.HomeActivityModule;
import io.givedirect.givedirectpos.view.payment.PaymentActivity;
import io.givedirect.givedirectpos.view.payment.PaymentActivityModule;
import io.givedirect.givedirectpos.view.writenfc.WriteNfcActivity;
import io.givedirect.givedirectpos.view.writenfc.WriteNfcActivityModule;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Provides application-wide dependencies.
 */
@Module(includes = AndroidInjectionModule.class)
public abstract class AppModule {
    private static final String HORIZON_RETROFIT_QUALIFIER = "HorizonRetrofit";
    private static final String GIVE_DIRECT_RETROFIT_QUALIFIER = "GiveDirectRetrofit";

    @Provides
    @Singleton
    static AppFlagDB provideAppFlagDB(Application application) {
        return Room.databaseBuilder(application, AppFlagDB.class, AppFlagDB.DATABASE_NAME).build();
    }

    @Provides
    @Singleton
    static Moshi provideMoshi() {
        return new Moshi.Builder().build();
    }

    @Provides
    @Singleton
    static Cache provideHttpCache(Application application) {
        return new Cache(application.getCacheDir(), 10 * 1024 * 1024);
    }

    @Provides
    @Singleton
    static OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        if (!EnvironmentConstants.IS_PRODUCTION) {
            CurlLoggingInterceptor curlLoggingInterceptor = new CurlLoggingInterceptor();
            curlLoggingInterceptor.setCurlOptions("-i");
            client.addNetworkInterceptor(curlLoggingInterceptor)
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Accept", "application/json").build();
                        return chain.proceed(request);
                    });
        }
        client.cache(cache);
        return client.build();
    }

    @Provides
    @Singleton
    @Named(HORIZON_RETROFIT_QUALIFIER)
    static Retrofit provideHorizonRetrofit(Moshi moshi, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(EnvironmentConstants.HORIZON_API_ENDPOINT)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    @Named(GIVE_DIRECT_RETROFIT_QUALIFIER)
    static Retrofit provideGiveDirectRetrofit(Moshi moshi, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(EnvironmentConstants.GIVE_DIRECT_API_ENDPOINT)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    static HorizonApi providesHorizonApi(@Named(HORIZON_RETROFIT_QUALIFIER) Retrofit retrofit) {
        if (EnvironmentConstants.IS_PRODUCTION) {
            Network.usePublicNetwork();
        } else {
            Network.useTestNetwork();
        }
        return retrofit.create(HorizonApi.class);
    }

    @Provides
    static GiveDirectApi providesGiveDirectApi(@Named(GIVE_DIRECT_RETROFIT_QUALIFIER) Retrofit retrofit) {
        return retrofit.create(GiveDirectApi.class);
    }

    @PerActivity
    @ContributesAndroidInjector(modules = HomeActivityModule.class)
    abstract HomeActivity bindHomeActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = PaymentActivityModule.class)
    abstract PaymentActivity bindPaymentActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = WriteNfcActivityModule.class)
    abstract WriteNfcActivity bindWriteNfcActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = AboutActivityModule.class)
    abstract AboutActivity bindAboutActivity();
}
