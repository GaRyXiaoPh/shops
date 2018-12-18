package com.cmd.wallet.blockchain.eos.domain.response.history.controlledaccounts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ControlledAccounts {

    private List<String> controlledAccounts;

    public List<String> getControlledAccounts() {
        return controlledAccounts;
    }

    @JsonProperty("controlled_accounts")
    public void setControlledAccounts(List<String> controlledAccounts) {
        this.controlledAccounts = controlledAccounts;
    }

}
