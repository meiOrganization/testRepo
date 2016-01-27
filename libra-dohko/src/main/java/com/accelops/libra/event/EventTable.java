package com.accelops.libra.event;

/**
 * Created by kai.zhang on 12/14/2015.
 */
public enum EventTable {
    external,
    incident,
    internal,
    netflow,
    performance,
    beaconing,
    test,
    error;

    public static EventTable getTableByCategory(EventCategories category, int testEvent) {

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

    public static EventTable getTableByString(String tableName) {

        if(tableName == null)
            throw new IllegalArgumentException (
                    "TableName has not been assigned"
            );

        switch(tableName.toLowerCase()) {
            case "external": return external;
            case "incident": return incident;
            case "internal": return internal;
            case "netflow": return netflow;
            case "performance": return performance;
            case "beaconing": return beaconing;
            case "test": return test;
            case "error": return error;
            default:
                throw new IllegalArgumentException (
                        "Unsupported table name: " + tableName
                );
        }
    }

}
