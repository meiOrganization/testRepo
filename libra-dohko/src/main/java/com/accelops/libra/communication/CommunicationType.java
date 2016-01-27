package com.accelops.libra.communication;

/**
 * Created by kai.zhang on 1/21/2016.
 */
public enum CommunicationType {

    EXPORT_DATA(160),
    EXPORT_DATA_STATUS(161),
    STOP_PROCESS(201);

    private int eventId;

    CommunicationType(int eventId) {this.eventId = eventId;}

    public int getEventId() {
        return this.eventId;
    }

    public static CommunicationType getTypeFromId(int eventId) {

        switch (eventId) {
            case 160: return EXPORT_DATA;
            case 161: return EXPORT_DATA_STATUS;
            case 201: return STOP_PROCESS;
            default:
                throw new IllegalArgumentException (
                        "Unsupported event attribute: [" + eventId + "]"
                );
        }

    }

}
