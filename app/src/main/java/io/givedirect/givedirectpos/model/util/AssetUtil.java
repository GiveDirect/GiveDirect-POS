package io.givedirect.givedirectpos.model.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DecimalFormat;

import io.givedirect.givedirectpos.model.persistence.account.Balance;

public class AssetUtil {
    public static DecimalFormat ASSET_FORMAT = new DecimalFormat("#,##0.#");
    public static final String LUMEN_ASSET_TYPE = "native";
    public static final String LUMEN_ASSET_CODE = "XLM";

    static {
        ASSET_FORMAT.setMaximumFractionDigits(7);
    }

    public static String getAssetCode(@Nullable String assetType,
                                      @Nullable String assetCode) {
        return LUMEN_ASSET_TYPE.equals(assetType) ? LUMEN_ASSET_CODE : assetCode;
    }

    public static String getAssetIssuer(@Nullable String assetType,
                                        @Nullable String assetIssuer) {
        return LUMEN_ASSET_TYPE.equals(assetType) ? LUMEN_ASSET_TYPE : assetIssuer;
    }

    public static boolean isLumenBalance(@NonNull Balance balance) {
        return LUMEN_ASSET_TYPE.equals(balance.getAsset_type());
    }

    public static String getAssetAmountString(double amount) {
        return ASSET_FORMAT.format(amount);
    }
}
