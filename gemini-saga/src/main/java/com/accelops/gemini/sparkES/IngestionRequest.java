package com.accelops.gemini.sparkES;

import com.accelops.libra.event.EventTable;
import com.accelops.libra.utils.Constant;
import com.accelops.libra.utils.TimeRange;

import java.util.*;

/**
 * Created by kai.zhang on 1/22/2016.
 */
public class IngestionRequest {

    private int requestId;
    //private TreeMap<Integer, Integer> todoRanges = new TreeMap<Integer, Integer>();
    private ArrayList<TimeRange> todoList = new ArrayList<>();
    private TreeSet<EventTable> eventTables = new TreeSet<>();

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return this.requestId;
    }

    public void addRange(long low, long high) {
        int start = (int)(low/ Constant.SECONDS_IN_DAY);
        int end = (int)Math.ceil((double)high/Constant.SECONDS_IN_DAY);
        if(start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        todoList.add(new TimeRange(start, end));
    }

    public void addEventTable(EventTable eventTable) {
        eventTables.add(eventTable);
    }

    public void addEventTable(String eventTable) {
        addEventTable(EventTable.getTableByString(eventTable));
    }


}
