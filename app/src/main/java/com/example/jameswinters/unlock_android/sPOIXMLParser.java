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

public class sPOIXMLParser extends DefaultHandler {
    public sPOI currentsPOI;
    public String currentSubElement;
    public static ArrayList<sPOI> sPOIList;
    public ArrayList<String> imagesList;
    public Context context;
    sPOIXMLParser(Context c){
        this.context = c;
    }

    public static void main(String[] args) {

    }
    public  ArrayList<sPOI> getsPOIList() {
        DefaultHandler handler = new sPOIXMLParser(context);
        //System.out.println(context.getResources().get(R.raw.spois));
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(context.getResources().openRawResource(R.raw.spoicoords),handler);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return sPOIList;
    }

    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException{
        String elementName = localName;
        imagesList = new ArrayList<>();
        if ("".equals(elementName)) {
            elementName = qName;
        }
        switch (elementName) {
            case "sPOI":
                currentsPOI = new sPOI();
                break;
            case "latitude":
                currentSubElement = "latitude";
                break;
            case "longitude":
                currentSubElement = "longitude";
                break;
            case "locked":
                currentSubElement = "locked";
                break;
            case "parentName":
                currentSubElement = "parentName";
                break;
            case "video":
                currentSubElement = "video";
                break;
            case "mainImage":
                currentSubElement = "mainImage";
                break;
            case "image":
                currentSubElement = "image";
                break;
            case "audio":
                currentSubElement ="audio";
                break;
            case "text":
                currentSubElement ="text";
                break;
            case "visibility":
                currentSubElement = "visibility";
                break;
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
                case "sPOI":
                    currentsPOI.setTitle((attributeValue));
                    break;
                case "locked":
                    currentsPOI.setLockStatus(Boolean.parseBoolean(attributeValue));
                    break;
                case "parentName":
                    currentsPOI.setParentName(attributeValue);
                    break;
                case "video":
                    currentsPOI.setVideoLink(attributeValue);
                    break;
                case "mainImage":
                    currentsPOI.setMainImageLink(attributeValue);
                    break;
                case "audio":
                    currentsPOI.setAudioLink(attributeValue);
                    break;
                case "text":
                    currentsPOI.setText(attributeValue);
                    break;
                case "visibility":
                    currentsPOI.setVisibility(Boolean.parseBoolean(attributeValue));
                    break;
                case "image":
                    int number = Integer.parseInt(attributeValue);
                    if (number ==0){
                        imagesList=null;
                    }
                    for (int i=1;i<=number;i++) {
                        imagesList.add(attrs.getValue(i));
                    }
                    currentsPOI.setImageLinks(imagesList);
                    break;
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
        if (elementName.equals("sPOI")) {
            sPOIList.add(currentsPOI);
            currentsPOI = null;
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException{
        String newContent = new String(ch, start, length);
        switch (currentSubElement) {
            case "latitude":
                currentsPOI.setLat(Double.parseDouble(newContent));
                break;
            case "longitude":
                currentsPOI.setLng(Double.parseDouble(newContent));
                break;
            default:
                break;
        }
    }


    public void startDocument() throws SAXException{
        sPOIList = new ArrayList<>();
    }

    public void endDocument() throws SAXException{
        System.out.println("Finished parsing, stored " + sPOIList.size() + " sPOIs.");
        for (sPOI thisStudent : sPOIList) {
            System.out.println("Name: " + thisStudent.getTitle());
            System.out.println("Lat: " + thisStudent.getLat());
            System.out.println("Lng: " + thisStudent.getLng());
            System.out.println("Locked: " + thisStudent.getLockStatus());
            System.out.println("Parent Name: " + thisStudent.getParentName());
        }
    }



}
