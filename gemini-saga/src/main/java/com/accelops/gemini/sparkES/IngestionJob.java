package com.accelops.gemini.sparkES;

import java.nio.ByteBuffer;

/**
 * Created by kai.zhang on 1/26/2016.
 */
public class IngestionJob {

    private IngestionRequest request;

    public IngestionJob() {}

    public IngestionJob(IngestionRequest request) {
        this.request = request;
    }

    public void setIngestionRequest(IngestionRequest request) {
        this.request = request;
    }

    public int getRequestId() {
        return request.getRequestId();
    }

    public void getStatus(ByteBuffer resultBuffer) {
    }
}
