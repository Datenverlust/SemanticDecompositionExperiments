/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package ca.site.elkb;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * This class loads the Index html file and used functions from 
 * the Index class to create a new index.
 * 
 * @author Alistsair Kennedy
 * @version 1.4 2013
 */
public class IndexHandler extends DefaultHandler {

	private String keyWord;
	private String paragraphWord;
	private boolean isKeyWord;
	private boolean isLocation;
	private Index index;
	private StringBuffer sb;
	
	/**
	 * Loads constructs the class and is passed an Index object
	 * who's methods will be called when building the new index. 
	 * 
	 * @param ind
	 */
	public IndexHandler(Index ind){
		super();
		index = ind;
		keyWord = "";
		paragraphWord = "";
		sb = new StringBuffer();
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (localName.equals("word")){
			isKeyWord = true;
		}
		if(localName.equals("location")){
			isLocation = true;
			paragraphWord = atts.getValue("para");
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("location")){
			isLocation = false;
			String location = sb.toString();
			String[] parts = location.split(" ");
			if(parts.length != 9){
				System.err.println("Parse Error: " + parts.length + " : " + keyWord + " - " + paragraphWord + " " + location);
			}
			else{
				String entry = paragraphWord + "," + parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + parts[4] + "," + parts[5] + "," + parts[6];
				String strPtr = "";
				strPtr = index.addReference(strPtr, entry, parts[7], parts[8]);
				index.addEntry(keyWord, strPtr);
			}
			sb = new StringBuffer();
		}
		if(localName.equals("word")){
			isKeyWord = false;
			keyWord = sb.toString();
			sb = new StringBuffer();

		}
		if (localName.equals("entry")){
			keyWord = "";
		}
	}

	public void characters(char[] chars, int start, int length) throws SAXException {
		if(isKeyWord){
			sb.append(new String(chars, start, length));
		}
		if(isLocation){
			sb.append(new String(chars, start, length));
		}
	}
	
}
