
package com.accelops.pegasus.flume

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.accelops.libra.event.EventAttributes
import com.accelops.libra.event.EventTables
import org.apache.flume.Event
import org.apache.flume.event.SimpleEvent
import org.junit.Test

/**
 * Created by kai.zhang on 12/15/2015.
 */
class TimeCateInterceptorTest extends groovy.util.GroovyTestCase {

    @Test
    void testIntercept() {

        Event event = new SimpleEvent();
        String jsonEvent = "{\"collectorId\":1,\"computer\":\"devTest\",\"count\":1,\"customer\":\"Super\"," +
                "\"deviceTime\":1434576605000,\"eventId\":7411376876101163832,\"eventParsedOk\":1," +
                "\"eventRuleTrigger\":1,\"eventSeverity\":5,\"eventSeverityCat\":\"MEDIUM\"," +
                "\"eventType\":\"ET_GENERIC_WARNING\",\"phCustId\":1,\"phEventCategory\":3," +
                "\"phRecvTime\":1434576605000,\"procName\":\"phRuleWorker\"," +
                "\"relayDevIpAddr\":\"192.168.64.1\",\"reptDevIpAddr\":\"192.168.64.1\"," +
                "\"reptDevName\":\"devTest\",\"reptModel\":\"TestComany\",\"reptVendor\":\"TestCompany\"}";
        event.setBody(jsonEvent.getBytes());
        TimeCateInterceptor interceptor = new TimeCateInterceptor();
        interceptor.initialize();
        interceptor.intercept(event);

        Map<String, String> headers = event.getHeaders();

        //System.out.println("Headers");
        assertFalse(headers.isEmpty());
        assertTrue(headers.containsKey(FlumeHeader.timestamp.name()));
        assertEquals("1434576605000", headers.get(FlumeHeader.timestamp.name()));
        assertTrue(headers.containsKey("category"));
        assertEquals(EventTables.internal.name(), headers.get(FlumeHeader.category.name()));

        ObjectMapper mapper;
        mapper = new ObjectMapper(new JsonFactory());
        JsonNode eventInJson = mapper.readTree(event.getBody());
        eventInJson.has(EventAttributes.ingestTime.name());

        interceptor.close();

    }

}
