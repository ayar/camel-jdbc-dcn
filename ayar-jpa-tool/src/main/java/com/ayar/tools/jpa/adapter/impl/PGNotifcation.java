package com.ayar.tools.jpa.adapter.impl;

public class PGNotifcation {

    int processId;
    String channelName;
    String payload ;

    public PGNotifcation(){

    }
    public PGNotifcation(int processId, String channelName, String payload) {
        this.processId = processId;
        this.channelName = channelName;
        this.payload = payload;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
