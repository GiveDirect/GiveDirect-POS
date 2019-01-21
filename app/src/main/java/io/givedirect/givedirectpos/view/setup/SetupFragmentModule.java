package io.givedirect.givedirectpos.view.setup;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import io.givedirect.givedirectpos.dagger.module.BaseFragmentModule;
import io.givedirect.givedirectpos.dagger.scope.PerFragment;
import io.givedirect.givedirectpos.presenter.setup.SetupContract;
import io.givedirect.givedirectpos.presenter.setup.SetupPresenterModule;

@Module (includes = {BaseFragmentModule.class, SetupPresenterModule.class})
public abstract class SetupFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(SetupFragment sendFragment);

    @Binds
    @PerFragment
    abstract SetupContract.SetupView provideSetupView(SetupFragment setupFragment);
}
