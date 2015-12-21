package com.accelops.libra.event;

/**
 * Created by kai.zhang on 12/14/2015.
 */
public enum EventAttributes {
    phRecvTime(7),
    phEventCategory(16),
    testEvent(112),
    ingestTime(200);

    private int attributeId;

    EventAttributes(int attributeId) {
        this.attributeId = attributeId;
    }

    public int getAttributeId () {
        return this.attributeId;
    }

    public static EventAttributes getAttributeById(int attributeId) {

        switch (attributeId) {
            case 7: return phRecvTime;
            case 16: return phEventCategory;
            case 112: return testEvent;
            default:
                throw new IllegalArgumentException (
                        "Unsupported event attribute: [" + attributeId + "]"
                );
        }

    }
}
