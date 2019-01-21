package io.givedirect.givedirectpos.view.about;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.givedirect.givedirectpos.dagger.module.BaseActivityModule;
import io.givedirect.givedirectpos.dagger.scope.PerActivity;
import io.givedirect.givedirectpos.dagger.scope.PerFragment;

@Module(includes = BaseActivityModule.class)
public abstract class AboutActivityModule {
    @Binds
    @PerActivity
    abstract AppCompatActivity appCompatActivity(AboutActivity aboutActivity);

    @PerFragment
    @ContributesAndroidInjector(modules = AboutFragmentModule.class)
    abstract AboutFragment aboutFragmentInjector();
}
