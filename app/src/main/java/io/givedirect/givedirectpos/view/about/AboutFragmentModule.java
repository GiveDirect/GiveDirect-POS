package io.givedirect.givedirectpos.view.about;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import io.givedirect.givedirectpos.dagger.module.BaseFragmentModule;
import io.givedirect.givedirectpos.dagger.scope.PerFragment;

@Module(includes = BaseFragmentModule.class)
abstract class AboutFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(AboutFragment aboutFragment);
}
