/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package ca.site.elkb;

import java.util.*;

/**
 * Object used to store the information that defines a Head but not its words
 * and phrases. It contains the following attributes:
 * <UL>
 * <LI>Head number</LI>
 * <LI>Head name</LI>
 * <LI>Class number</LI>
 * <LI>Section number</LI>
 * <LI>Sub-section name</LI>
 * <LI>Head group, defined as a list of <TT>HeadInfo</TT> objects</LI>
 * </UL>
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 */

public class HeadInfo {
	private int headNum;

	private String headName;

	private int classNum;

	private int sectNum;
	
	private int subSectNum;
	
	private int headGroupNum;

	private String subSectName;

	private ArrayList<String> headGroup;

	/**
	 * Default constructor.
	 */
	public HeadInfo() {
		headName = new String();
		subSectName = new String();
		headGroup = new ArrayList<String>();
	}

	/**
	 * Constructor which sets the Head number and name, as well as the Class and
	 * Section number, Sub-section name and Head group list.
	 * 
	 * @param number
	 * @param name
	 * @param cn
	 * @param sn
	 * @param subName
	 * @param groupList
	 */
	public HeadInfo(int number, String name, int cn, int sn, String subName,
			ArrayList<String> groupList) {

		headNum = number;
		headName = name;
		classNum = cn;
		sectNum = sn;
		subSectName = subName;
		headGroup = groupList;
		subSectNum = 0;
		headGroupNum = 0;
	}
	
	/**
	 * Constructor which sets the Head number and name, as well as the Class and
	 * Section number, Sub-section name and Head group list.  Contains sub-section
	 * and head group numbers.
	 * 
	 * @param number
	 * @param name
	 * @param cn
	 * @param sn
	 * @param ssn
	 * @param hgn
	 * @param subName
	 * @param groupList
	 */
	public HeadInfo(int number, String name, int cn, int sn, int ssn, int hgn, String subName,
			ArrayList<String> groupList) {

		headNum = number;
		headName = name;
		classNum = cn;
		sectNum = sn;
		subSectNum = ssn;
		headGroupNum = hgn;
		subSectName = subName;
		headGroup = groupList;
	}

	/**
	 * Constructor which sets the Head number and name, as well as the Class and
	 * Section number, Sub-section name and Head group list.
	 * <p>
	 * This constructor parses strings containing the Sub-section name, Head
	 * group list and Head passed in the following format:
	 * 
	 * <PRE>
	 * 
	 * ^subSectionTitle>#Abstract#^/subSectionTitle> ^headGroup 1 2 >
	 * ^headword>#^b>#[001] #1# Existence #^/b>#^/headword>
	 * 
	 * </PRE>
	 * 
	 * The Class and Section numbers are represented by and int. The Sub-section
	 * is an optional element. It can also be an empty String.
	 * 
	 * @param sInfo
	 * @param cn
	 * @param sn
	 * @param subSectInfo
	 * @param sGroupInfo
	 */
	public HeadInfo(String sInfo, int cn, int sn, String subSectInfo,
			String sGroupInfo) {
		classNum = cn;
		sectNum = sn;
		parseHead(sInfo);
		if (subSectInfo.equals("") == false) {
			parseSubSect(subSectInfo);
		} else {
			subSectName = "�";
		}
		parseGroup(sGroupInfo);
	}

	/**
	 * parses Head name and number
	 * 
	 * @param sInfo
	 */
	private void parseHead(String sInfo) {
		StringTokenizer st = new StringTokenizer(sInfo, "#");
		st.nextToken();
		st.nextToken();
		st.nextToken();
		headNum = (new Integer(st.nextToken())).intValue();
		headName = st.nextToken().trim();
	}

	/**
	 * gets subsection info.
	 * 
	 * @param sInfo
	 */
	private void parseSubSect(String sInfo) {
		StringTokenizer st = new StringTokenizer(sInfo, "#");
		st.nextToken();
		subSectName = st.nextToken().trim();
	}

	// parses a string of type: <headGroup 1 2 >
	private void parseGroup(String sInfo) {
		headGroup = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(sInfo);
		st.nextToken();
		int iTokens = st.countTokens();

		for (int i = 1; i < iTokens; i++) {
			headGroup.add(st.nextToken().trim());
		}

	}

	/**
	 * Returns the number of this Head.
	 * 
	 * @return head number
	 */
	public int getHeadNum() {
		return headNum;
	}

	/**
	 * Returns the Class number of this Head.
	 * 
	 * @return class number
	 */
	public int getClassNum() {
		return classNum;
	}

	/**
	 * Returns the Section number of this Head.
	 * 
	 * @return section number
	 */
	public int getSectNum() {
		return sectNum;
	}
	
	/**
	 * Returns the Sub-Section number of this Head.
	 * 
	 * @return sub section number
	 */
	public int getSubSectNum() {
		return subSectNum;
	}
	
	/**
	 * Returns the Head Group number of this Head.
	 * 
	 * @return head group number
	 */
	public int getHeadGroupNum() {
		return headGroupNum;
	}

	/**
	 * Returns the Sub-section name of this Head.
	 * 
	 * @return sub section number
	 */
	public String getSubSectName() {
		return subSectName;
	}

	/**
	 * Returns the array of <TT>HeadGroup</TT> objects of this Head.
	 * 
	 * @return ArrayList of head groups
	 */
	public ArrayList<String> getHeadGroup() {
		return headGroup;
	}

	/**
	 * Sets the number of this Head.
	 * 
	 * @param num
	 */
	public void setHeadNum(int num) {
		headNum = num;
	}

	/**
	 * Returns the name of this Head.
	 * 
	 * @return head name
	 */
	public String getHeadName() {
		return headName;
	}

	/**
	 * Sets the name of this Head.
	 * 
	 * @param name
	 */
	public void setHeadName(String name) {
		headName = name;
	}

	/**
	 * Sets the number of this Head.
	 * 
	 * @param num
	 */
	public void setClassNum(int num) {
		classNum = num;
	}

	/**
	 * Sets the Section number of this Head.
	 * 
	 * @param num
	 */
	public void setSectNum(int num) {
		sectNum = num;
	}

	/**
	 * Sets the Section name of this Head.
	 * 
	 * @param name
	 */
	public void setSubSectName(String name) {
		subSectName = name;
	}

	/**
	 * Sets the array of <TT>HeadGroup</TT> objects of this Head.
	 * 
	 * @param group
	 */
	public void setHeadGroup(ArrayList<String> group) {
		headGroup = group;
	}

	/**
	 * Converts to a string representation the <TT>HeadInfo</TT> object.
	 */
	public String toString() {
		String info = new String();
		info += classNum + ", " + sectNum + ", " + subSectName + ", "
				+ headGroup + ", " + headNum + ", " + headName;
		return info;
	}

	/**
	 * prints out the toString method.
	 */
	public void print() {
		System.out.println(toString());
	}

}
