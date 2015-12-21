
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

    @Test
    void testInterceptor2() {
        Event event = new SimpleEvent();
        String jsonEvent = "{\"appTransportProto\":\"Syslog\",\"collectorId\":1,\"count\":1,\"customer\":\"Super\",\"destIpAddr\":\"10.0.0.157\",\"destName\":\"kai-super-451-1019\",\"deviceTime\":1450382401000,\"eventAction\":0,\"eventId\":3906591201803194825,\"eventParsedOk\":1,\"eventRuleTrigger\":1,\"eventSeverity\":1,\"eventSeverityCat\":\"LOW\",\"eventType\":\"Apache-Web-Request-Success\",\"httpMethod\":\"GET\",\"httpStatusCode\":\"200\",\"httpVersion\":\"HTTP/1.1\",\"parserName\":\"ApacheViaSnareParser\",\"phCustId\":1,\"phEventCategory\":0,\"phRecvTime\":1450382401000,\"procName\":\"phMonitorSupervisor\",\"rawEventMsg\":\"<142>Dec 17 20:00:01 kai-super-451-1019 ApacheLog: 10.0.0.157 - - [17/Dec/2015:20:00:01 +0000] \\\"GET /phoenix/rest/sync/task?custId=1&agentId=1&time=1450382401&phProcessName=phMonitorSupervisor HTTP/1.1\\\" 200 96\",\"recvBytes\":96,\"relayDevIpAddr\":\"10.0.0.157\",\"reptDevIpAddr\":\"10.0.0.157\",\"reptDevName\":\"kai-super-451-1019\",\"reptModel\":\"Apache Tomcat\",\"reptVendor\":\"Apache\",\"srcIpAddr\":\"10.0.0.157\",\"srcName\":\"kai-super-451-1019\",\"totFlows\":1,\"uriStem\":\"/phoenix/rest/sync/task\",\"user\":\"-\",\"ingestTime\":\"1450382461567\"}";
        event.setBody(jsonEvent.getBytes());
        TimeCateInterceptor interceptor = new TimeCateInterceptor();
        interceptor.initialize();
        interceptor.intercept(event);

        Map<String, String> headers = event.getHeaders();

        System.out.println("Headers");
        for(Map.Entry<String,String> entry : headers) {
            System.out.println(entry.key + ": " + entry.value);
        }
    }
}
