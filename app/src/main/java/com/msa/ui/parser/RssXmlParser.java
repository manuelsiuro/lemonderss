package com.msa.ui.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RssXmlParser {

    private static final String NS = null;
    private static final String TAG_ITEM        = "item";
    private static final String TAG_LINK        = "link";
    private static final String TAG_TITLE       = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PUB_DATE    = "pubDate";
    private static final String TAG_ENCLOSURE   = "enclosure";
    private static final String TAG_GUID        = "guid";
    private static final String ATTRIBUTE_URL   = "url";

    public List<Item> parse(String response) throws XmlPullParserException, IOException {
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, null);
        parser.nextTag();
        return readFeed(parser);
    }

    private List<Item> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Item> items = new ArrayList<>();

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {

                String name = parser.getName();

                if (name == null) {
                    continue;
                }

                if (name.equals(TAG_ITEM)) {
                    items.add(readEntry(parser));
                }
            }
            eventType = parser.next();
        }
        return items;
    }

    private Item readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {

        String link         = null;
        String title        = null;
        String description  = null;
        String pubDate      = null;
        String enclosure    = null;

        parser.require(XmlPullParser.START_TAG, NS, TAG_ITEM);

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            switch (parser.getName()) {
                case TAG_LINK:
                    link = readTag(parser, TAG_LINK);
                    break;
                case TAG_TITLE:
                    title = readTag(parser, TAG_TITLE);
                    break;
                case TAG_DESCRIPTION:
                    description = readTag(parser, TAG_DESCRIPTION);
                    break;
                case TAG_PUB_DATE:
                    pubDate = readTag(parser, TAG_PUB_DATE);
                    break;
                case TAG_ENCLOSURE:
                    enclosure = readTagAttribute(parser, TAG_ENCLOSURE, ATTRIBUTE_URL);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new Item(link, title, description, pubDate, enclosure);
    }

    private String readTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NS, tag);
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, NS, tag);
        return value;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private String readTagAttribute(XmlPullParser parser, String tag, String attribute) throws IOException, XmlPullParserException {
        String value = "";
        parser.require(XmlPullParser.START_TAG, NS, tag);

        if (parser.getName().equals(tag)) {
            value = parser.getAttributeValue(null, attribute);
            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, NS, tag);
        return value;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {

        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        int depth = 1;

        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

     public static class Item {

        public final String link;
        public final String title;
        public final String description;
        public final String pubDate;
        public final String enclosure;

        private Item(String link, String title, String description, String pubDate, String enclosure) {
            this.link = link;
            this.title = title;
            this.description = description;
            this.pubDate = pubDate;
            this.enclosure = enclosure;
        }
    }
}
