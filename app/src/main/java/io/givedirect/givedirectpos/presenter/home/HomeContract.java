package io.givedirect.givedirectpos.presenter.home;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.presenter.common.Presenter;

public interface HomeContract {
    interface HomeView {
        void showLoading(boolean isLoading);

        void showSetupScreen();

        void showTransactionScreen();

        void startPaymentFlow(@NonNull NfcUtil.GiveDirectNFCWallet giveDirectNFCWallet);

        void showLoadError();

        void showNfcParseError();

        void showMalformedNfcError();

        void startWriteWristbandFlow(boolean hasInitializedMerchantAddress);

        void startAboutFlow();

        @Nullable
        NfcUtil.GiveDirectNFCWallet parseNFC(@NonNull String authSeed);
    }

    interface HomePresenter extends Presenter {
        void onMerchantAddressSet();

        void onUserScannedNFC();

        void onUserRetryLoad();

        void onUserRequestWriteWristband();

        void onUserRequestAbout();
    }
}
