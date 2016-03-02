package com.accelops.gemini.sparkES;

import com.accelops.libra.event.EventTable;

import java.nio.ByteBuffer;

/**
 * Created by kai.zhang on 1/26/2016.
 */
public class IngestionJob {

    int day;
    EventTable eventTable;

    public IngestionJob() {}

    public IngestionJob(EventTable eventTable, int day) {

        this.day = day;
        this.eventTable = eventTable;

    }

    public void getStatus(ByteBuffer resultBuffer) {
    }

}
