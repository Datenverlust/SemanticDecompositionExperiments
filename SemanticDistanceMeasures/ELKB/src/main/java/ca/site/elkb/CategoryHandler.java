/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package ca.site.elkb;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class loads the categories from an xml file.  The class
 * requires an instance of the Category class to be passed in
 * the constructor.  Methods from the category class are used
 * by this class.
 * 
 * @author Alistsair Kennedy
 * @version 1.4 2013
 */
public class CategoryHandler extends DefaultHandler {
	private Section rtSection;
	private String subSectInfo;
	private int subSectNum;
	private ArrayList<String> sGroupInfo;
	private int headGroupNum;
	private int iSection;
	private RogetClass rtClass;
	private Category category;
	
	/**
	 * Initializes the CategoryHandler and is passed an instance
	 * of the Category class.
	 * 
	 * @param c
	 */
	public CategoryHandler(Category c) {
		rtSection = new Section();
		subSectInfo = new String();
		subSectNum = 0;
		headGroupNum = 0;
		sGroupInfo = new ArrayList<String>();
		iSection = 0;
		rtClass = new RogetClass();
		category = c;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if(localName.equals("thesaurus")){
			//do nothing
		}
		else if(localName.equals("class")){
			String strClassNum = atts.getValue("number");
			String strClassName = atts.getValue("name");
			category.classCountIncrement();
			iSection = 0;
			rtClass = new RogetClass(category.getClassCount(), strClassNum,
					strClassName);
		}
		else if(localName.equals("section")){
			String strSectNum = atts.getValue("number");
			String strSectName = atts.getValue("name");
			iSection++;
			category.sectionCountIncrement();
			subSectInfo = "";
			rtSection = new Section(iSection, strSectNum, strSectName);
		}
		else if(localName.equals("subsection")){
			subSectInfo = atts.getValue("name");
			subSectNum = Integer.parseInt(atts.getValue("number"));
			category.subSectionCountIncrement();
		}
		else if(localName.equals("headGroup")){
			String start = atts.getValue("first");
			String end = atts.getValue("last");
			headGroupNum = Integer.parseInt(atts.getValue("number"));
			sGroupInfo = new ArrayList<String>();
			for(int i = Integer.parseInt(start); i <= Integer.parseInt(end); i++){
				sGroupInfo.add(i + "");
			}
			category.headGroupCountIncrement();
		}
		else if(localName.equals("head")){
			String headName = atts.getValue("name");
			String headNumber = atts.getValue("number");
			HeadInfo rogetHead = new HeadInfo(Integer.parseInt(headNumber), headName, category.getClassCount(), iSection,
					subSectNum, headGroupNum, subSectInfo, sGroupInfo);
			category.headCountIncrement();
			category.addToHeadList(rogetHead);
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName.equals("class")){
			category.addToClassList(rtClass);
		}
		if(localName.equals("section")){
			rtClass.addSection(rtSection);
		}
	}

}
