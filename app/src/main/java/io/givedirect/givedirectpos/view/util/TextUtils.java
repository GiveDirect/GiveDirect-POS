package io.givedirect.givedirectpos.view.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.Locale;

import timber.log.Timber;

public class TextUtils {

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public static String parseDateTimeZuluDate(@Nullable String zuluDate,
                                               @NonNull FormatStyle formatStyle) {
        if (TextUtils.isEmpty(zuluDate)) {
            Timber.e("DateTime was empty!");
            return "Unknown";
        }

        return getDateTimeFromInstant(Instant.parse(zuluDate), formatStyle);
    }

    public static String parseDateTimeEpochSeconds(long millis, @NonNull FormatStyle formatStyle) {
        return getDateTimeFromInstant(Instant.ofEpochSecond(millis), formatStyle);
    }

    private static String getDateTimeFromInstant(@NonNull Instant instant,
                                          @NonNull FormatStyle formatStyle) {
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                .format(DateTimeFormatter.ofLocalizedDateTime(formatStyle)
                        .withLocale(Locale.getDefault()));
    }
}
