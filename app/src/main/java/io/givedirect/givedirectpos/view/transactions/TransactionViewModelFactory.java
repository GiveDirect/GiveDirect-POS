package io.givedirect.givedirectpos.view.transactions;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import org.threeten.bp.format.FormatStyle;

import io.givedirect.givedirectpos.R;
import io.givedirect.givedirectpos.model.persistence.transaction.Operation;
import io.givedirect.givedirectpos.model.persistence.transaction.Transaction;
import io.givedirect.givedirectpos.model.util.AssetUtil;
import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.view.util.TextUtils;
import timber.log.Timber;

class TransactionViewModelFactory {

    static TransactionViewModel getViewModel(@NonNull Operation operation,
                                             @NonNull String viewingAccount,
                                             @NonNull Context context) {
        String memo = null;
        Transaction transaction = operation.getTransaction();
        if (transaction != null) {
            memo = transaction.getMemo();
        }

        String operationName = context.getString(operation.getOperationType().getFriendlyName());
        String date = TextUtils.parseDateTimeZuluDate(operation.getCreated_at(), FormatStyle.MEDIUM);

        TransactionViewModel.TransactionViewModelBuilder builder =
                new TransactionViewModel.TransactionViewModelBuilder();

        // Always add date as the first row.
        builder.withRow(R.string.date_label, date);

        switch(operation.getOperationType()) {
            case CREATE_ACCOUNT: {
                boolean isTransferIn = !viewingAccount.equals(operation.getFunder());
                builder
                        .withHeadline(getAmountText(
                                isTransferIn,
                                operation.getStarting_balance(),
                                AssetUtil.LUMEN_ASSET_CODE,
                                context))
                        .withHeadlineColor(getAmountColor(isTransferIn))
                        .withRow(R.string.operation_type_label, operationName)
                        .withAddress(
                                getAddressLabel(isTransferIn),
                                getPraId(isTransferIn ?
                                                operation.getFunder()
                                                : operation.getAccount(),
                                        context));
                break;
            }
            case PAYMENT: {
                boolean isTransferIn = !viewingAccount.equals(operation.getFrom());
                builder
                        .withHeadline(getAmountText(isTransferIn, operation.getAmount(),
                                operation.getAsset_code(), context))
                        .withHeadlineColor(getAmountColor(isTransferIn))
                        .withRow(R.string.operation_type_label, operationName)
                        .withAddress(
                                getAddressLabel(isTransferIn),
                                getPraId(isTransferIn ?
                                                operation.getFrom()
                                                : operation.getTo(),
                                        context));
                break;
            }
            case PATH_PAYMENT: {
                boolean isTransferIn = !viewingAccount.equalsIgnoreCase(operation.getFrom());
                builder
                        .withHeadline(getAmountText(isTransferIn,
                                isTransferIn ? operation.getAmount() : operation.getSource_amount(),
                                isTransferIn ? operation.getAsset_code() : operation.getSource_asset_code(),
                                context))
                        .withHeadlineColor(getAmountColor(isTransferIn))
                        .withRow(R.string.operation_type_label, operationName)
                        .withAddress(
                                getAddressLabel(isTransferIn),
                                getPraId(isTransferIn ?
                                                operation.getFrom()
                                                : operation.getTo(),
                                        context));
                break;
            }
            case MANAGE_OFFER:
            case CREATE_PASSIVE_OFFER: {
                builder
                        .withHeadline(operationName)
                        .withRow(R.string.offer_id_label, operation.getOffer_id())
                        .withRow(R.string.selling_asset_code_label, operation.getSelling_asset_code())
                        .withRow(R.string.buying_asset_code_label, operation.getBuying_asset_code())
                        .withRow(R.string.selling_amount_label, AssetUtil.getAssetAmountString(operation.getAmount()))
                        .withRow(R.string.buying_price_label, operation.getPrice());
                break;
            }
            case SET_OPTIONS: {
                builder.withHeadline(operationName);
                break;
            }
            case CHANGE_TRUST:
            case ALLOW_TRUST: {
                builder
                        .withHeadline(operationName)
                        .withAddress(R.string.trustee_label, getPraId(operation.getTrustee(), context))
                        .withAddress(R.string.trustor_label, getPraId(operation.getTrustor(), context))
                        .withRow(R.string.asset_label, operation.getAsset_code());
                break;
            }
            case ACCOUNT_MERGE: {
                builder
                        .withHeadline(operationName)
                        .withAddress(R.string.merged_into_label, getPraId(operation.getInto(), context));
                break;
            }
            case INFLATION: {
                builder.withHeadline(operationName);
                break;
            }
            case MANAGE_DATA: {
                builder
                        .withHeadline(operationName)
                        .withRow(R.string.entry_name_label, operation.getName())
                        .withRow(R.string.entry_value_label, operation.getValue());
                break;
            }
            case UNKNOWN: {
                Timber.e("Unhandled operation type, id: %s", operation.getId());
                break;
            }
        }

        if (!TextUtils.isEmpty(memo)) {
            builder.withRow(R.string.memo_label, memo);
        }

        return builder.build();
    }

    private static String getAmountText(boolean isTransferIn,
                                        double amount,
                                        @NonNull String assetCode,
                                        @NonNull Context context) {
        int prefixRes = isTransferIn ? R.string.receive_amount_prefix
                : R.string.send_amount_prefix;
        return context.getString(prefixRes, AssetUtil.getAssetAmountString(amount), assetCode);
    }

    @ColorRes
    private static int getAmountColor(boolean isTransferIn) {
        return isTransferIn ? R.color.transfer_in : R.color.transfer_out;
    }

    @StringRes
    private static int getAddressLabel(boolean isTransferIn) {
        return isTransferIn ? R.string.sender_label : R.string.recipient_label;
    }

    private static String getPraId(@NonNull String address,
                                   @NonNull Context context) {
        return context.getString(R.string.pra_title, NfcUtil.getPraId(address));
    }
}
