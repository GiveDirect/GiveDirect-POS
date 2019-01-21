package io.givedirect.givedirectpos.view.writenfc;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.givedirect.givedirectpos.dagger.module.BaseActivityModule;
import io.givedirect.givedirectpos.dagger.scope.PerActivity;
import io.givedirect.givedirectpos.dagger.scope.PerFragment;

@Module(includes = BaseActivityModule.class)
public abstract class WriteNfcActivityModule {
    @Binds
    @PerActivity
    abstract AppCompatActivity appCompatActivity(WriteNfcActivity writeNfcActivity);

    @PerFragment
    @ContributesAndroidInjector(modules = WriteNfcFragmentModule.class)
    abstract WriteNfcFragment writeNfcFragmentInjector();
}
