package com.cmd.wallet.blockchain.eos.domain.request.chain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.util.List;

@JsonFormat
@Builder
public class OwnerActive {
    private Integer threshold;
    private List<Keys> keys;
    private List accounts;
    private List waits;

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public List<Keys> getKeys() {
        return keys;
    }

    public void setKeys(List<Keys> keys) {
        this.keys = keys;
    }

    public List getAccounts() {
        return accounts;
    }

    public void setAccounts(List accounts) {
        this.accounts = accounts;
    }

    public List getWaits() {
        return waits;
    }

    public void setWaits(List waits) {
        this.waits = waits;
    }
}
