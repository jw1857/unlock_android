package com.example.jameswinters.unlock_android;
import android.content.Context;


import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

//POIXMLParser parses the xml containing the gps coordinates, the title, and all content strings pointing to firebase storage,
//and makes a new ArrayList of POIs with the attributes parsed from the xml.
public class POIXMLParser extends DefaultHandler{
    public POI currentPOI;
    public String currentSubElement;
    public  ArrayList<String> imagesList;
    public static ArrayList<POI> POIList;
    public Context context;
    POIXMLParser(Context c){
        this.context = c;
    }//pass in the context so android resources can be accessed

    public static void main(String[] args) {

    }
    public  ArrayList<POI> getPOIList() {//returns the completed POIList for use in the app
        DefaultHandler handler = new POIXMLParser(context);
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
        imagesList = new ArrayList<>();
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
                case "POI":
                    currentPOI.setTitle((attributeValue));
                    break;
                case "locked":
                    currentPOI.setLockStatus(Boolean.parseBoolean(attributeValue));
                    break;
                case "video":
                    currentPOI.setVideoLink(attributeValue);
                    break;
                case "mainImage":
                    currentPOI.setMainImageLink(attributeValue);
                    break;
                case "audio":
                    currentPOI.setAudioLink(attributeValue);
                    break;
                case "text":
                    currentPOI.setText(attributeValue);
                    break;
                case "image":
                    //check how many images are stored and then add the firebase storage strings to arraylist of strings
                    int number = Integer.parseInt(attributeValue);
                    if(number==0){
                        imagesList=null;
                    }
                    for (int i=1;i<=number;i++) {
                        imagesList.add(attrs.getValue(i));
                    }
                    currentPOI.setImageLinks(imagesList);
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
        if (elementName.equals("POI")) {
        //add POI to POIList when at end of element
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
        for (POI p : POIList) {
            System.out.println("Name: " + p.getTitle());
            System.out.println("Lat: " + p.getLat());
            System.out.println("Lng: " + p.getLng());
            System.out.println("Locked: " + p.getLockStatus());
        }
    }


}