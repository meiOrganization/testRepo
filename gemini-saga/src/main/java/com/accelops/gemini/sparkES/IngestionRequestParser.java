package com.accelops.gemini.sparkES;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by kai.zhang on 1/22/2016.
 */
public class IngestionRequestParser {

    /*
    <IngestionRequest>
    <TimeRange>
    <Start>1447866656</Start>
    <End>1447870256</End>
    </TimeRange>
    <EventTables>
    <EventTable>external</EventTable>
    <EventTable>incident</EventTable>
    </EventTables>

    */
    public static IngestionRequest buildFromXml(String requestDef) throws IngestionRequestParsingException {

        InputSource is = new InputSource(new StringReader(requestDef));

        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new IngestionRequestParsingException(e.getMessage());
        }

        Element root = doc.getDocumentElement();
        IngestionRequest request = new IngestionRequest();
        parseRequestId(root, request);
        parseTimeRange(getOnlyElementByTagName(root, "TimeRange"), request);
        parseEventTable(getOnlyElementByTagName(root, "EventTables"), request);
        return null;
    }

    private static void parseRequestId(Element root, IngestionRequest request)
            throws IngestionRequestParsingException{
        String requestId = root.getAttribute("RequestId");
        if(requestId.isEmpty()) {
            throw new IngestionRequestParsingException("Request Id is empty.");
        }

        try {
            request.setRequestId(Integer.parseInt(requestId));
        } catch (NumberFormatException e) {
            throw new IngestionRequestParsingException("Failed to parse request Id:" + requestId);
        }
    }

    private static void parseTimeRange(Element element, IngestionRequest request)
            throws IngestionRequestParsingException{

        long startTime = 0;
        long endTime = 0;
        NodeList listTime = element.getChildNodes();
        for(int i = 0; i < listTime.getLength(); i++) {

            Node timeNode = listTime.item(i);

            if(timeNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            switch(timeNode.getNodeName()) {
                case "Start":
                    startTime = Long.parseLong(timeNode.getTextContent());
                    break;
                case "End":
                    endTime = Long.parseLong(timeNode.getTextContent());
                    break;
                default:
                    break;
            }
        }

        request.setRange(startTime, endTime);

    }

    private static void parseEventTable(Element element, IngestionRequest request)
            throws IngestionRequestParsingException {

        NodeList eventTableList = element.getChildNodes();
        int tableLength = eventTableList.getLength();
        if(tableLength == 0)
            throw new IngestionRequestParsingException("No event table assigned");

        for(int i = 0; i < tableLength; i++) {

            Node tableNode = eventTableList.item(i);
            if(tableNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if(tableNode.getNodeName().equals("EventTable"))
                request.addEventTable(tableNode.getTextContent());
        }

    }


    private static Element getOnlyElementByTagName(Element root, String tag)
            throws IngestionRequestParsingException {
        NodeList list = root.getElementsByTagName(tag);
        if (list.getLength() != 1) {
            throw new IngestionRequestParsingException(
                    "Get " + list.getLength() + " nodes, when retrieve tag " + tag);
        }

        Node node = list.item(0);
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new IngestionRequestParsingException(
                    "Get " + node.getNodeType() + " type, when retrieve tag " + tag);
        }

        return (Element) node;
    }
}
