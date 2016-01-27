package com.accelops.libra.utils;

import java.util.Comparator;

/**
 * Created by kai.zhang on 1/25/2016.
 */
public class TimeRangeComparator implements Comparator<TimeRange> {
    @Override
    public int compare(TimeRange o1, TimeRange o2) {
        return o1.getStart() - o2.getStart();
    }
}
