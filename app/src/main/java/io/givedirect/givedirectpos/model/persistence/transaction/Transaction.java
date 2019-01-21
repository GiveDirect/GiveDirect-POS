package io.givedirect.givedirectpos.model.persistence.transaction;

import android.support.annotation.Nullable;

import io.givedirect.givedirectpos.model.persistence.Link;

public class Transaction {
    private TransactionLinks _links;
    private String id;
    private String paging_token;
    private String hash;
    private long ledger;
    private String created_at;
    private String account;
    private long account_sequence;
    private double max_fee;
    private double fee_paid;
    private long operationCount;
    private int result_code;
    private String result_code_s;
    private String memo;

    public TransactionLinks get_links() {
        return _links;
    }

    public void set_links(TransactionLinks _links) {
        this._links = _links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaging_token() {
        return paging_token;
    }

    public void setPaging_token(String paging_token) {
        this.paging_token = paging_token;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getLedger() {
        return ledger;
    }

    public void setLedger(long ledger) {
        this.ledger = ledger;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getAccount_sequence() {
        return account_sequence;
    }

    public void setAccount_sequence(long account_sequence) {
        this.account_sequence = account_sequence;
    }

    public double getMax_fee() {
        return max_fee;
    }

    public void setMax_fee(double max_fee) {
        this.max_fee = max_fee;
    }

    public double getFee_paid() {
        return fee_paid;
    }

    public void setFee_paid(double fee_paid) {
        this.fee_paid = fee_paid;
    }

    public long getOperationCount() {
        return operationCount;
    }

    public void setOperationCount(long operationCount) {
        this.operationCount = operationCount;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public String getResult_code_s() {
        return result_code_s;
    }

    public void setResult_code_s(String result_code_s) {
        this.result_code_s = result_code_s;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


    public static class TransactionLinks {
        @Nullable
        private Link account;
        @Nullable
        private Link effects;
        @Nullable
        private Link ledger;
        @Nullable
        private Link operations;
        @Nullable
        private Link precedes;
        @Nullable
        private Link self;

        @Nullable
        public Link getAccount() {
            return account;
        }

        public void setAccount(@Nullable Link account) {
            this.account = account;
        }

        @Nullable
        public Link getEffects() {
            return effects;
        }

        public void setEffects(@Nullable Link effects) {
            this.effects = effects;
        }

        @Nullable
        public Link getLedger() {
            return ledger;
        }

        public void setLedger(@Nullable Link ledger) {
            this.ledger = ledger;
        }

        @Nullable
        public Link getOperations() {
            return operations;
        }

        public void setOperations(@Nullable Link operations) {
            this.operations = operations;
        }

        @Nullable
        public Link getPrecedes() {
            return precedes;
        }

        public void setPrecedes(@Nullable Link precedes) {
            this.precedes = precedes;
        }

        @Nullable
        public Link getSelf() {
            return self;
        }

        public void setSelf(@Nullable Link self) {
            this.self = self;
        }

        @Nullable
        public Link getSucceeds() {
            return succeeds;
        }

        public void setSucceeds(@Nullable Link succeeds) {
            this.succeeds = succeeds;
        }

        @Nullable
        private Link succeeds;
    }
}
