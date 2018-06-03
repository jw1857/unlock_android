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


public class bPOIXMLParser extends DefaultHandler{
    public bPOI currentbPOI;
    public String currentSubElement;
    public  ArrayList<String> imagesList;
    public static ArrayList<bPOI> bPOIList;
    public Context context;
    bPOIXMLParser(Context c){
        this.context = c;
    }

    public static void main(String[] args) {

    }
    public  ArrayList<bPOI> getbPOIList() {
        DefaultHandler handler = new bPOIXMLParser(context);
        //System.out.println(context.getResources().get(R.raw.pois));
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(context.getResources().openRawResource(R.raw.bpois),handler);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return bPOIList;
    }

    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException{
        String elementName = localName;
        imagesList = new ArrayList<>();
        if ("".equals(elementName)) {
            elementName = qName;
        }
        switch (elementName) {
            case "bPOI":
                currentbPOI = new bPOI();
                break;
            case "latitude":
                currentSubElement = "latitude";
                break;
            case "longitude":
                currentSubElement = "longitude";
                break;
            case "businesstype":
                currentSubElement = "businesstype";
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
                case "bPOI":
                    currentbPOI.setTitle((attributeValue));
                    break;
                case "businesstype":
                    currentbPOI.setType(attributeValue);
                    break;
                case "video":
                    currentbPOI.setVideoLink(attributeValue);
                    break;
                case "mainImage":
                    currentbPOI.setMainImageLink(attributeValue);
                    break;
                case "audio":
                    currentbPOI.setAudioLink(attributeValue);
                    break;
                case "text":
                    currentbPOI.setText(attributeValue);
                    break;
                case "image":
                    int number = Integer.parseInt(attributeValue);
                    if (number==0){
                        imagesList=null;
                    }
                    for (int i=1;i<=number;i++) {
                        imagesList.add(attrs.getValue(i));
                    }
                    currentbPOI.setImageLinks(imagesList);
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
        if (elementName.equals("bPOI")) {

            bPOIList.add(currentbPOI);
            currentbPOI = null;
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException{
        String newContent = new String(ch, start, length);
        switch (currentSubElement) {
            case "latitude":
                currentbPOI.setLat(Double.parseDouble(newContent));
                break;
            case "longitude":
                currentbPOI.setLng(Double.parseDouble(newContent));
                break;
            default:
                break;
        }
    }


    public void startDocument() throws SAXException{
        bPOIList = new ArrayList<>();
    }

    public void endDocument() throws SAXException{
        System.out.println("Finished parsing, stored " + bPOIList.size() + " POIs.");
        for (POI p : bPOIList) {
            System.out.println("Name: " + p.getTitle());
            System.out.println("Lat: " + p.getLat());
            System.out.println("Lng: " + p.getLng());
            System.out.println("Locked: " + p.getLockStatus());
        }
    }


}

