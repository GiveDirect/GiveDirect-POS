package io.givedirect.givedirectpos.model.persistence.account;

import android.support.annotation.NonNull;

public class DisconnectedAccount extends Account {

    public DisconnectedAccount(@NonNull String accountId) {
        setAccount_id(accountId);
    }

    @Override
    public boolean isOnNetwork() {
        return false;
    }
}
