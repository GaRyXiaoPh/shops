package com.cmd.wallet.blockchain.eos.domain.request.chain;

import com.cmd.wallet.blockchain.eos.domain.common.transaction.PackedTransaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RequiredKeysRequest {

    private PackedTransaction transaction;

    private List<String> availableKeys;

    public RequiredKeysRequest(PackedTransaction transaction, List<String> availableKeys) {
        this.transaction = transaction;
        this.availableKeys = availableKeys;
    }

    public PackedTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(PackedTransaction transaction) {
        this.transaction = transaction;
    }

    @JsonProperty("available_keys")
    public List<String> getAvailableKeys() {
        return availableKeys;
    }

    public void setAvailableKeys(List<String> availableKeys) {
        this.availableKeys = availableKeys;
    }
}
