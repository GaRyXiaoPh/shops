package com.cmd.wallet.blockchain.eos.domain.request.chain;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;


@JsonFormat
@Builder
public class NewAccountRequest {

    private String creator;

    private String name;

    private OwnerActive owner;

    private OwnerActive active;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OwnerActive getOwner() {
        return owner;
    }

    public void setOwner(OwnerActive owner) {
        this.owner = owner;
    }

    public OwnerActive getActive() {
        return active;
    }

    public void setActive(OwnerActive active) {
        this.active = active;
    }
}
