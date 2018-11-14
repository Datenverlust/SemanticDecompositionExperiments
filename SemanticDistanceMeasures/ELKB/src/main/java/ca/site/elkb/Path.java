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
 * Represents a path in <i>Roget's Thesaurus</i> between two words or phrases. 
 * This is mostly used in an old version of SemDist and may not be too useful.
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 */

// This class needs work :-(
// Described as very sloppy!
public class Path implements Comparable<Object> {
	private ArrayList<String> rtPath;

	/**
	 * Default constructor.
	 */
	public Path() {
		rtPath = new ArrayList<String>();
	}

	/**
	 * Constructor that initialized this <TT>Path</TT> object with a Path.
	 * 
	 * @param path
	 */
	public Path(ArrayList<String> path) {
		rtPath = path;
	}

	/**
	 * Returns the number of elements in this Path.
	 * 
	 * @return length of path
	 */
	public int length() {
		if (rtPath.isEmpty() == false) {
			// must consider keywords and pathInfo
			return rtPath.size() - 8;
		} else {
			return 0;
		}
	}

	/**
	 * Returns the length in this Path. Size is length - 1.
	 * 
	 * @return length of path - 1
	 */
	public int size() {
		return length() - 1;
	}

	/**
	 * Compares two paths. They are first compared according to their length. If
	 * they are still equal, they are then sorted according to keywords.
	 */
	public int compareTo(Object other) {
		int result;
		Path otherPath = (Path) other;
		result = length() - otherPath.length();

		if (result == 0) {
			result = getKeyWord1().compareTo(otherPath.getKeyWord1());
			if (result == 0) {
				result = getKeyWord2().compareTo(otherPath.getKeyWord2());
			}
		}

		return result;
	}

	/**
	 * Returns the keyword of the the first word or phrase in this Path.
	 * 
	 * @return key word 1
	 */
	public String getKeyWord1() {
		String sKeyWord = new String();
		if (rtPath.isEmpty() == false) {
			sKeyWord = rtPath.get(0);
		}
		return sKeyWord;
	}

	/**
	 * Returns the part-of-speech of the the first word or phrase in this Path.
	 * 
	 * @return POS of word 1
	 */
	public String getPos1() {
		return (rtPath.isEmpty() == false ? rtPath.get(1) : "");
	}

	/**
	 * Returns the keyword of the the second word or phrase in this Path.
	 * 
	 * @return key word 2
	 */
	public String getKeyWord2() {
		String sKeyWord = new String();
		if (rtPath.isEmpty() == false) {
			sKeyWord = rtPath.get(2);
		}
		return sKeyWord;
	}

	/**
	 * Returns the part-of-speech of the the second word or phrase in this Path.
	 * 
	 * @return POS of word 2
	 */
	public String getPos2() {
		return (rtPath.isEmpty() == false ? rtPath.get(3) : "");
	}

	/**
	 * Returns the location in the ontology of the first word or phrase in this
	 * Path.
	 * 
	 * @return path info 1
	 */
	public String getPathInfo1() {
		String sPathInfo = new String();
		if (rtPath.isEmpty() == false) {
			sPathInfo = rtPath.get(4);
		}
		return sPathInfo;
	}

	/**
	 * Returns the location in the ontology of the second word or phrase in this
	 * Path.
	 * 
	 * @return path info 2
	 */
	public String getPathInfo2() {
		String sPathInfo = new String();
		if (rtPath.isEmpty() == false) {
			sPathInfo = rtPath.get(5);
		}
		return sPathInfo;
	}

	/**
	 * Returns the first word or phrase in this Path.
	 * 
	 * @return word 1
	 */
	public String getWord1() {
		String sWord1 = new String();
		if (rtPath.isEmpty() == false) {
			sWord1 = rtPath.get(6);
		}
		return sWord1;
	}

	/**
	 * Returns the second word or phrase in this Path.
	 * 
	 * @return word 2
	 */
	public String getWord2() {
		String sWord2 = new String();
		if (rtPath.isEmpty() == false) {
			sWord2 = rtPath.get(7);
		}
		return sWord2;
	}

	/**
	 * Returns the path between the first and second word or phrase.
	 * 
	 * @return path
	 */
	public String getPath() {
		String sPath = new String();

		if (rtPath.isEmpty() == false) {
			Iterator<String> iter = rtPath.iterator();
			int iTotal = length();
			int iMiddle = iTotal / 2;

			// drop the keyWord and pathInfo
			for (int i = 0; i < 8; i++) {
				iter.next();
			}

			for (int i = 0; i < iMiddle; i++) {
				sPath += iter.next() + " --> ";
			}

			sPath += iter.next();

			// Paths are symmetric, thus we can repeat the procedure
			for (int i = 0; i < iMiddle; i++) {
				sPath += " <-- " + iter.next();
			}
		}

		return sPath;
	}

	/**
	 * Converts to a string representation the <TT>Path</TT> object.
	 */
	public String toString() {
		String info = new String();
		info += "Path between " + getKeyWord1() + " and " + getKeyWord2();
		info += " (length = " + (length() - 1) + ")\n";
		info += getPathInfo1() + "\n";
		info += getPathInfo2() + "\n";
		info += getPath();
		return info;
	}

}
