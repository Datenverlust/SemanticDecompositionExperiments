/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package wordCluster;
import java.util.ArrayList;
import java.util.Collection;

import ca.site.elkb.HeadInfo;


/*******************************************************************************
 * ComparableArray: an ArrayList than can be compared according to its size
 * 
 * Author : Mario Jarmasz Created: October, 2003
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.2 Nov 2008
 ******************************************************************************/

public class ComparableArray<T> extends ArrayList<T> implements Comparable<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8044048522806367644L;
	
	private HeadInfo headInfo;

	/**
	 * Constructor
	 */
	public ComparableArray() {
		super();
		headInfo = new HeadInfo();
	}

	/**
	 * Constructor passed a Collection
	 * 
	 * @param c
	 */
	public ComparableArray(Collection<T> c) {
		super(c);
		headInfo = new HeadInfo();
	}

	/**
	 * Compares two ArrayList. They are equal is they contain the same elements
	 * and are of the same size
	 */
	@SuppressWarnings("unchecked")
	public int compareTo(Object other) {
		int result;
		ComparableArray<T> otherArray = (ComparableArray<T>) other;
		result = size() - otherArray.size();
		HeadInfo otherHI = otherArray.getHeadInfo();
		result = otherHI.getClassNum() - headInfo.getClassNum();

		// if they're the same size, they may not be equal!
		if (result == 0) {
			if (equals(otherArray) == true) {
				result = 0;
			} else {
				// same size, different elements, the first will
				// be bigger
				result = 1;
			}
		}
		return result;
	}
	
	/**
	 * set HeadInfo
	 * 
	 * @param hi
	 */
	public void setHeadInfo(HeadInfo hi){
		headInfo = hi;
	}
	
	/**
	 * get HeadInfo
	 * @return headInfo stored in the Comparable Array
	 */
	public HeadInfo getHeadInfo(){
		return headInfo;
	}

}
