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
 * Represents a <i>Roget's Thesaurus</i> Head group. For example:
 * <UL>
 * 79 Generality &nbsp;&nbsp;&nbsp; 80 Speciality
 * </UL>
 * A <TT>Group</TT> can contain 1,2 or 3 <TT>HeadInfo</TT> objects.
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 */

public class Group {

	// Attributes
	private int headCount;

	private int headStart;

	private ArrayList<HeadInfo> headList;

	/**
	 * Default constructor.
	 */
	public Group() {
		headCount = 0;
		headStart = 0;
		headList = new ArrayList<HeadInfo>();
	}

	/**
	 * Constructor that takes an integer to indicate first Head number of the
	 * Group.
	 * 
	 * @param start
	 */
	public Group(int start) {
		headCount = 0;
		headStart = start;
		headList = new ArrayList<HeadInfo>();
	}

	/**
	 * Returns the array of <TT>HeadInfo</TT> objects.
	 * 
	 * @return ArrayList of heads
	 */
	public ArrayList<HeadInfo> getHeadList() {
		return headList;
	}

	/**
	 * Add a <TT>HeadInfo</TT> object to this Group.
	 * 
	 * @param head
	 */
	public void addHead(HeadInfo head) {
		headList.add(head);
		headCount++;
	}

	/**
	 * Returns the number of Heads in this Group.
	 * 
	 * @return head count
	 */
	public int getHeadCount() {
		return headCount;
	}

	/**
	 * Sets the number of the first Head in this Group.
	 * 
	 * @param start
	 */
	public void setHeadStart(int start) {
		headStart = start;
	}

	/**
	 * Returns the number of the first Head in this Group.
	 * 
	 * @return first head number
	 */
	public int getHeadStart() {
		return headStart;
	}

	/**
	 * Converts to a string representation the <TT>Group</TT> object.
	 */
	public String toString() {
		String info = new String();
		if (headCount >= 1) {
			info += headList.get(0);
		}
		if (headCount >= 2) {
			info += "\t\t" + headList.get(1);
		}
		if (headCount >= 3) {
			info += "\n\t\t" + headList.get(2);
		}
		return info;
	}

}
