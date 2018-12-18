package com.cmd.wallet.blockchain.eos.domain.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionTrace {

    private Act act;

    private String console;

    private Integer cpuUsage;

    private Integer elapsed;

    private List<Object> inlineTraces = null;

    private Receipt receipt;

    private Integer totalCpuUsage;

    private String trxId;

    private Boolean contextFree;
    private Integer blockNum;
    private String blockTime;
    private String producerBlockId;
    public Act getAct() {
        return act;
    }

    @JsonProperty("act")
    public void setAct(Act act) {
        this.act = act;
    }

    public String getConsole() {
        return console;
    }

    @JsonProperty("console")
    public void setConsole(String console) {
        this.console = console;
    }

    public Integer getCpuUsage() {
        return cpuUsage;
    }

    @JsonProperty("cpu_usage")
    public void setCpuUsage(Integer cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Integer getElapsed() {
        return elapsed;
    }

    @JsonProperty("elapsed")
    public void setElapsed(Integer elapsed) {
        this.elapsed = elapsed;
    }

    public List<Object> getInlineTraces() {
        return inlineTraces;
    }

    @JsonProperty("inline_traces")
    public void setInlineTraces(List<Object> inlineTraces) {
        this.inlineTraces = inlineTraces;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    @JsonProperty("receipt")
    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public Integer getTotalCpuUsage() {
        return totalCpuUsage;
    }

    @JsonProperty("total_cpu_usage")
    public void setTotalCpuUsage(Integer totalCpuUsage) {
        this.totalCpuUsage = totalCpuUsage;
    }

    public String getTrxId() {
        return trxId;
    }

    @JsonProperty("trx_id")
    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public Boolean getContextFree() {
        return contextFree;
    }
    @JsonProperty("context_free")
    public void setContextFree(Boolean contextFree){
        this.contextFree=contextFree;
    }

    public Integer getBlockNum() {
        return blockNum;
    }
    @JsonProperty("block_num")
    public void setBlockNum(Integer blockNum) {
        this.blockNum = blockNum;
    }

    public String getBlockTime() {
        return blockTime;
    }
    @JsonProperty("block_time")
    public void setBlockTime(String blockTime) {
        this.blockTime = blockTime;
    }
    public String getProducerBlockId() {
        return producerBlockId;
    }
    @JsonProperty("producer_block_id")
    public void setProducerBlockId(String producerBlockId) {
        this.producerBlockId = producerBlockId;
    }
}
