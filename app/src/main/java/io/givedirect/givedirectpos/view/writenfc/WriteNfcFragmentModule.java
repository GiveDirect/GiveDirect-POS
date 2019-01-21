package io.givedirect.givedirectpos.view.writenfc;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import io.givedirect.givedirectpos.dagger.module.BaseFragmentModule;
import io.givedirect.givedirectpos.dagger.scope.PerFragment;
import io.givedirect.givedirectpos.presenter.writenfc.WriteNfcContract;
import io.givedirect.givedirectpos.presenter.writenfc.WriteNfcPresenterModule;

@Module(includes = {BaseFragmentModule.class, WriteNfcPresenterModule.class})
public abstract class WriteNfcFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(WriteNfcFragment writeNfcFragment);

    @Binds
    @PerFragment
    abstract WriteNfcContract.WriteNfcView provideWriteNfcView(WriteNfcFragment writeNfcFragment);
}
