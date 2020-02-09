package com.distributed.response;

import java.net.UnknownHostException;

public abstract class ResponseMessage {
    protected String length;
    protected String type;

    public abstract void decodeResponse(String response) throws UnknownHostException;

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
