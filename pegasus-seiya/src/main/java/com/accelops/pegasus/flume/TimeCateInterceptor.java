package com.accelops.pegasus.flume;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.accelops.libra.event.EventAttributes;
import com.accelops.libra.event.EventCategories;
import com.accelops.libra.event.EventTable;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.io.IOException;
import java.util.List;

/**
 * Created by kai.zhang on 12/14/2015.
 */

public class TimeCateInterceptor implements Interceptor {

    private ObjectMapper mapper;

    public void initialize() {
        mapper = new ObjectMapper(new JsonFactory());
    }

    private void addExtraHeader(JsonNode eventInJson, Event event) {

        String timeInMillis = eventInJson.path(EventAttributes.phRecvTime.name()).asText();
        EventCategories category = EventCategories.getEventCategoryById(
                eventInJson.path(EventAttributes.phEventCategory.name()).asInt());
        int bTest = eventInJson.path(EventAttributes.testEvent.name()).asInt();

        event.getHeaders().put(FlumeHeader.timestamp.name(), timeInMillis);
        event.getHeaders().put(FlumeHeader.category.name(), EventTable.getTableByCategory(category, bTest).name());

    }

    private void addExtraField(JsonNode eventInJson, Event event) {

        ((ObjectNode) eventInJson).put(EventAttributes.ingestTime.name(), Long.toString(System.currentTimeMillis()));

        try {
            event.setBody(mapper.writeValueAsBytes(eventInJson));
        } catch (JsonProcessingException e) {
        }

    }

    public Event intercept(Event event) {
        try {

            JsonNode eventInJson = mapper.readTree(event.getBody());

            addExtraHeader(eventInJson, event);
            addExtraField(eventInJson, event);


        } catch (IOException | IllegalArgumentException e) {

            event.getHeaders().put(FlumeHeader.category.name(), EventTable.error.name());
            event.getHeaders().put(FlumeHeader.timestamp.name(), Long.toString(System.currentTimeMillis()));

        }

        return event;

    }

    public List<Event> intercept(List<Event> list) {
        for (Event e : list) {
            intercept(e);
        }
        return list;
    }

    public void close() {
    }

    public static class Builder implements Interceptor.Builder {

        public Interceptor build() {
            return new TimeCateInterceptor();
        }

        public void configure(Context context) {
            // no-op
        }
    }
}
