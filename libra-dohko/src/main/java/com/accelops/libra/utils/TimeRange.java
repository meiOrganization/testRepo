package com.accelops.libra.utils;

/**
 * Created by kai.zhang on 1/25/2016.
 */
public class TimeRange {

    private int start;
    private int end;

    public TimeRange() {start = 0; end = 0;}
    public TimeRange(int s, int e) {
        this.start = s;
        this.end = e;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

}
