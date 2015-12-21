package com.accelops.commons.event;

/**
 * Created by kai.zhang on 12/14/2015.
 */
public enum EventCategories {
    external(0),
    incident(1),
    audit(2),
    internal(3),
    netflow(4),
    health_incident(5),
    performance(6),
    beaconing(7),
    perf_test(8),
    clear_incident(99);

    private int cateId;

    EventCategories(int cateId) {
        this.cateId = cateId;
    }

    public int getCateId() {
        return this.cateId;
    }

    public static EventCategories getEventCategoryById(int cateId) {
        switch (cateId) {
            case 0: return external;
            case 1: return incident;
        case 2: return audit;
            case 3: return internal;
            case 4: return netflow;
            case 5: return health_incident;
            case 6: return performance;
            case 7: return beaconing;
            case 8: return perf_test;
            case 99: return clear_incident;
            default:
                throw new IllegalArgumentException (
                        "Unsupported event category: [" + cateId + "]"
                );
        }
    }
}
