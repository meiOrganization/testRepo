package com.accelops.gemini.sparkES;

import com.accelops.libra.event.EventTable;
import com.accelops.libra.utils.Constant;
import com.accelops.libra.utils.TimeRange;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by kai.zhang on 1/22/2016.
 */
public class IngestionRequest {

    private int requestId = -1;
    private int startDay = 0;
    private int endDay = 0;
    private int load = 0;
    private int progress = 0;
    private TreeSet<EventTable> eventTables = new TreeSet<>();

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return this.requestId;
    }

    public void setRange(long low, long high) {
        this.startDay = (int)Math.floor((double) low / Constant.SECONDS_IN_DAY);
        this.endDay = (int)Math.ceil((double) high / Constant.SECONDS_IN_DAY);
    }

    public void addEventTable(EventTable eventTable) {
        eventTables.add(eventTable);
    }

    public void addEventTable(String eventTable) {
        addEventTable(EventTable.getTableByString(eventTable));
    }


    public void getStatus(ByteBuffer resultBuffer) {
    }

    public synchronized int getProgress() {
        return (progress * 100 / load);
    }

    public synchronized void evaluateLoad() {
        load = (endDay - startDay + 1) * eventTables.size();
    }

    public void addJobs(final Queue<IngestionJob> jobQueue, final IngestionJobManager ingestionJobManager) {
        for(EventTable eventTable : eventTables) {
            for(int day = startDay; day <= endDay; ++day) {
                IngestionJobStatus jobStatus = ingestionJobManager.getJobStatus(eventTable, day);
                switch(jobStatus) {
                    case Unknown: {
                        jobQueue.add(new IngestionJob(eventTable, day));
                        break;
                    }
                    case Running: {
                        //Do nothing
                        break;
                    }
                    case Finished: {
                        progress++;
                    }
                }
            }
        }
    }

    public synchronized void  updateProgress(EventTable eventTable, int day) {
        if(eventTables.contains(eventTable) && day <= endDay && day >= startDay) {
            progress++;
        }
    }
}
