package com.distributed.response;

public abstract class ResponseMessage {
    protected String length;
    protected String type;

    public abstract void decodeResponse(String response);

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
