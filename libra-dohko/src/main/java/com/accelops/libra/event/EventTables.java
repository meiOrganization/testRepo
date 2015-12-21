package com.accelops.libra.event;

/**
 * Created by kai.zhang on 12/14/2015.
 */
public enum EventTables {
    external,
    incident,
    internal,
    netflow,
    performance,
    beaconing,
    test,
    error;

    public static EventTables getTableByCategory(EventCategories category, int testEvent) {

        if(testEvent == 1)
            return test;

        switch(category) {

            case external:
                return external;
            case incident:case health_incident:case clear_incident:
                return incident;
            case audit:case internal:
                return internal;
            case netflow:
                return netflow;
            case performance:
                return performance;
            case beaconing:
                return beaconing;
            case perf_test:
                return test;
            default:
                return external;

        }

    }

}
