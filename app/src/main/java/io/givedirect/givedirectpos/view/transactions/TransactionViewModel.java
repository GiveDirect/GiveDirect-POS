package io.givedirect.givedirectpos.view.transactions;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import io.givedirect.givedirectpos.R;

class TransactionViewModel {
    @NonNull
    private String headline = "";

    @ColorRes
    private int headlineColorRes = R.color.colorPrimaryText;

    @NonNull
    private List<Row> rowList;

    private TransactionViewModel(@NonNull String headline,
                                 int headlineColorRes,
                                 @NonNull List<Row> rowList) {
        this.headline = headline;
        this.headlineColorRes = headlineColorRes;
        this.rowList = rowList;
    }

    @NonNull
    String getHeadline() {
        return headline;
    }

    @ColorRes
    int getHeadlineColorRes() {
        return headlineColorRes;
    }

    @NonNull
    List<Row> getRowList() {
        return rowList;
    }

    static class TransactionViewModelBuilder {
        @NonNull
        String headline = "";

        @ColorRes
        int headlineColorRes = R.color.white;

        @NonNull
        private final List<Row> rowList = new ArrayList<>();

        TransactionViewModelBuilder withHeadline(@NonNull String headline) {
            this.headline = headline;
            return this;
        }

        TransactionViewModelBuilder withHeadlineColor(@ColorRes int headlineColorRes) {
            this.headlineColorRes = headlineColorRes;
            return this;
        }

        TransactionViewModelBuilder withRow(@StringRes int labelRes, @NonNull String value) {
            rowList.add(new Row(labelRes, value));
            return this;
        }

        TransactionViewModelBuilder withAddress(@StringRes int labelRes, @NonNull String value) {
            rowList.add(new Row(labelRes, value, true));
            return this;
        }

        TransactionViewModel build() {
            return new TransactionViewModel(headline, headlineColorRes, rowList);
        }
    }

    public static class Row {
        @StringRes
        private int label;

        @NonNull
        private final String value;

        private boolean containsAddress;

        Row(@StringRes int label, @NonNull String value) {
            this(label, value, false);
        }

        Row(@StringRes int label, @NonNull String value, boolean containsAddress) {
            this.label = label;
            this.value = value;
            this.containsAddress = containsAddress;
        }

        @StringRes
        int getLabel() {
            return label;
        }

        @NonNull
        String getValue() {
            return value;
        }

        boolean containsAddress() {
            return containsAddress;
        }
    }
}
