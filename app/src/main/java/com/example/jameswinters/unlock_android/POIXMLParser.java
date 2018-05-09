package com.example.jameswinters.unlock_android;
import android.content.Context;
import android.content.res.Resources;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;


public class POIXMLParser extends DefaultHandler{
    public POI currentPOI;
    public String currentSubElement;
    public static ArrayList<POI> POIList;
    public Context context;
    POIXMLParser(Context c){
        this.context = c;
    }

    public static void main(String[] args) {

    }
    public  ArrayList<POI> getPOIList() {
        DefaultHandler handler = new POIXMLParser(context);
        //System.out.println(context.getResources().get(R.raw.pois));
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(context.getResources().openRawResource(R.raw.poicoords),handler);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return POIList;
    }

    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException{
        String elementName = localName;
        if ("".equals(elementName)) {
            elementName = qName;
        }
        switch (elementName) {
            case "POI":
                currentPOI = new POI();
                break;
            case "latitude":
                currentSubElement = "latitude";
                break;
            case "longitude":
                currentSubElement = "longitude";
                break;
            case "locked":
                currentSubElement = "locked";
            default:
                currentSubElement = "none";
                break;
        }
        if (attrs != null) {
            String attributeName = attrs.getLocalName(0);
            if ("".equals(attributeName)) {
                attributeName = attrs.getQName(0);
            }
            String attributeValue = attrs.getValue(0);
            switch (elementName) {
                case "POI":
                    currentPOI.setTitle((attributeValue));
                    break;
                case "locked":
                    currentPOI.setLockStatus(Boolean.parseBoolean(attributeValue));
                default:
                    break;
            }
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException{
        currentSubElement = "none";
        String elementName = localName;
        if ("".equals(elementName)) {
            elementName = qName;
        }
        if (elementName.equals("POI")) {
            POIList.add(currentPOI);
            currentPOI = null;
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException{
        String newContent = new String(ch, start, length);
        switch (currentSubElement) {
            case "latitude":
                currentPOI.setLat(Double.parseDouble(newContent));
                break;
            case "longitude":
                currentPOI.setLng(Double.parseDouble(newContent));
                break;
            default:
                break;
        }
    }


    public void startDocument() throws SAXException{
        POIList = new ArrayList<>();
    }

    public void endDocument() throws SAXException{
        System.out.println("Finished parsing, stored " + POIList.size() + " POIs.");
        for (POI thisStudent : POIList) {
            System.out.println("Name: " + thisStudent.getTitle());
            System.out.println("Lat: " + thisStudent.getLat());
            System.out.println("Lng: " + thisStudent.getLng());
            System.out.println("Locked: " + thisStudent.getLockStatus());
        }
    }



}