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
 * Represents a <i>Roget's Thesaurus</i> Paragraph. A Paragraph is defined by
 * the following attributes:
 * <UL>
 * <li>Head number</li>
 * <li>Paragraph name</li>
 * <li>Paragraph keyword</li>
 * <li>Part-of-speech</li>
 * <li>list of Semicolon Groups</li>
 * <li>number of Semicolon Groups</li>
 * <li>number of words and phrases</li>
 * <li>number of Cross-references</li>
 * <li>number of See references</li>
 * </UL>
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 */

public class Paragraph {
	// Attributes
	private int headNum;

	private int paraNum;

	private String paraKey;

	private String pos;

	// private ArrayList wordIndex[];
	private ArrayList<SG> sgList;

	private int sgCount;

	private int wordCount;

	private int cRefCount;

	private int seeCount;

	/**
	 * Default constructor.
	 */
	public Paragraph() {
		headNum = 0;
		paraNum = 0;
		paraKey = new String();
		pos = new String();
		sgList = new ArrayList<SG>();
		sgCount = 0;
	}

	/**
	 * Constructor which sets the Head number, Paragraph number and
	 * part-of-speech.
	 * 
	 * @param head
	 * @param para
	 * @param p
	 */
	public Paragraph(int head, int para, String p) {
		this();
		headNum = head;
		paraNum = para;
		pos = p;
	}

	/**
	 * Constructor which sets the Head number, Paragraph number, keyword, and
	 * part-of-speech.
	 * 
	 * @param head
	 * @param para
	 * @param key
	 * @param p
	 */
	public Paragraph(int head, int para, String key, String p) {
		this(head, para, p);
		paraKey = key;
	}

	/**
	 * Returns the Head number of this Paragraph.
	 * 
	 * @return head number
	 */
	public int getHeadNum() {
		return headNum;
	}

	/**
	 * Sets the Head number of this Paragraph.
	 * 
	 * @param num
	 */
	public void setHeadNum(int num) {
		headNum = num;
	}

	/**
	 * Returns the number of this Paragraph.
	 * 
	 * @return paragraph number
	 */
	public int getParaNum() {
		return paraNum;
	}

	/**
	 * Sets the number of this Paragraph.
	 * 
	 * @param num
	 */
	public void setParaNum(int num) {
		paraNum = num;
	}

	/**
	 * Returns the keyword of this Paragraph.
	 * 
	 * @return paragraph key
	 */
	public String getParaKey() {
		return paraKey;
	}

	/**
	 * Sets the keyword of this Paragraph.
	 * 
	 * @param key
	 */
	public void setParaKey(String key) {
		paraKey = key;
	}

	/**
	 * Returns the part-of-speech of this Paragraph.
	 * 
	 * @return POS
	 */
	public String getPOS() {
		return pos;
	}

	/**
	 * Sets the part-of-speech of this Paragraph.
	 * 
	 * @param p
	 */
	public void setPOS(String p) {
		pos = p;
	}

	/**
	 * Returns the number of words in this Paragraph.
	 * 
	 * @return number of words
	 */
	public int getWordCount() {
		return wordCount;
	}

	/**
	 * Returns the number of Cross-references in this Paragraph.
	 * 
	 * @return number of references
	 */
	public int getCRefCount() {
		return cRefCount;
	}

	/**
	 * Returns the number of See references in this Paragraph.
	 * 
	 * @return number of see references
	 */
	public int getSeeCount() {
		return seeCount;
	}

	/**
	 * Returns the number of Semicolon Groups in this Paragraph.
	 * 
	 * @return semicolon group count
	 */
	public int getSGCount() {
		return sgCount;
	}

	/**
	 * Returns the array of Semicolon Groups of this Paragraph.
	 * 
	 * @return ArrayList of semicolon groups
	 */
	public ArrayList<SG> getSGList() {
		return sgList;
	}

	/**
	 * Adds a Semicolon Group, repreented as a string, to the Paragraph. This
	 * method parses a string similar to: <CODE>^sg>^i>frenzy, ^/i>monomania,
	 * furore, frenetic condition,;^/sg></CODE>.
	 * 
	 * @param sg
	 */
	public void addSG(String sg) {
		// remove the <sg> </sg> tags
		sg = sg.replaceAll("<sg>", "");
		sg = sg.replaceAll("</sg>", "");

		sgCount++;

		SG sgObj = new SG(sgCount, paraNum, headNum, sg, pos);
		sgList.add(sgObj);
		wordCount += sgObj.getWordCount();
		cRefCount += sgObj.getCRefCount();
		seeCount += sgObj.getSeeCount();

		// Shortest semicolon group ?
		// Longest semicolon group ?
	}
	
	/**
	 * Adds a Semicolon Group, repreented as a string, to the Paragraph. This
	 * method takes an already created semicolon group SG as an argument.
	 * 
	 * @param sgObj
	 */
	public void addSG(SG sgObj) {
		sgCount++;

		sgList.add(sgObj);
		wordCount += sgObj.getWordCount();
		cRefCount += sgObj.getCRefCount();
		seeCount += sgObj.getSeeCount();

		// Shortest semicolon group ?
		// Longest semicolon group ?
	}

	/**
	 * Returns the the first Semicolon Group in this Paragraph which contains
	 * the given word.
	 * 
	 * @param word
	 * @return semicolon group
	 */
	public SG getSG(String word) {
		// Get a semicolon group in the paragraph that contains
		// the word or phrase equal to the string word
		// Will have to include keywords also
		SG elkbSG = new SG();
		ArrayList<String> wordList = new ArrayList<String>();

		Iterator<SG> iter = sgList.iterator();
		while (iter.hasNext()) {
			elkbSG = iter.next();
			wordList = elkbSG.getAllWordList();
			if (wordList.contains(word)) {
				break;
			}
		}
		return elkbSG;
	}

	/**
	 * Returns the the first Semicolon Group in this Paragraph which contains
	 * the given word either on its own or in a phrase.
	 * 
	 * @param word
	 * @return semicolon group
	 */
	public SG getSGinPhrase(String word) {
		// Get a semicolon group in the paragraph that contains
		// the word or phrase equal to the string word
		// Will have to include keywords also
		SG elkbSG = new SG();
		ArrayList<String> wordList = new ArrayList<String>();

		Iterator<SG> iter = sgList.iterator();
		boolean found = false;
		while (iter.hasNext()) {
			elkbSG = iter.next();
			wordList = elkbSG.getAllWordList();
			for (int i = 0; i < wordList.size(); i++) {
				String sgWord = wordList.get(i);
				String[] sgWordParts = sgWord.split(" ");
				if (sgWordParts.length <= 2) {
					for (int j = 0; j < sgWordParts.length; j++) {
						if (sgWordParts[j].equals(word)) {
							found = true;
							// break;
							return elkbSG;
						}
					}
				}
			}
		}
		if (!found) {
			return null;
		}
		return elkbSG;
	}

	/**
	 * Returns the Semicolon Group at the specified position in the array of
	 * Semicolon Groups.
	 * 
	 * @param index
	 * @return semicolon group
	 */
	public SG getSG(int index) {
		// I should check if the index is out of bounds...
		return sgList.get(index);
	}

	/**
	 * Extracts the keyword from a Semicolon Group represented as a string. For
	 * example, the keyword <TT>existence</TT> is returned when the following
	 * string is supplied: <CODE>^sg>^i>existence, ^/i>being, entity;^/sg></CODE>
	 * 
	 * @param line
	 * @return paragraph key
	 */
	public String parseParaKey(String line) {
		String key = new String();
		if (line.startsWith("<sg>")) {
			StringTokenizer st = new StringTokenizer(line, ",");
			key = st.nextToken();
			// remove <sg>, <i>, </i>, "?" and "!"
			key = key.replaceAll("<sg>", "");
			key = key.replaceAll("<i>", "");
			key = key.replaceAll("</i>", "");
			key = key.replaceAll("\\?", "");
			key = key.replaceAll("!", "");
			key = key.trim();

		} else {
			// we should raise an exception, but....
			key = "";
		}
		return key;
	}

	/**
	 * Converts to a string representation the <TT>Paragraph</TT> object.
	 */
	public String toString() {
		String info = new String();
		info = super.toString();
		info += "@" + getHeadNum() + "@" + getParaNum();
		info += "@" + getParaKey() + "@" + getPOS();
		info += "@" + getSGCount() + "@" + getWordCount();
		return info;
	}

	/**
	 * Prints the contents of this Paragraph to the standard output.
	 */
	public void print() {
		System.out.print(format());
	}

	/**
	 * Converts to a string representation, similar to the printed format, the
	 * <TT>Paragraph</TT> object.
	 * 
	 * @return format information
	 */
	public String format() {
		String info = new String();
		Iterator<SG> iter = sgList.iterator();
		while (iter.hasNext()) {
			SG sgObj = iter.next();
			info += sgObj.format() + "\n";
		}
		return info;
	}

	/**
	 * Compares this paragraph to the specified object. The result is
	 * <code>true</code> if the argument is not <code>null</code> and is a
	 * <code>Paragraph</code> object that has the same <code>paraKey</code>
	 * as this object.
	 * 
	 * @param anObject
	 *            the object to compare this <code>Paragraph</code> against.
	 * @return <code>true</code> if the <code>Paragraph</code> have the same
	 *         <code>paraKey</code>; <code>false</code> otherwise.
	 * @see java.lang.String#compareTo(java.lang.String)
	 * @see java.lang.String#equalsIgnoreCase(java.lang.String)
	 */
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if ((anObject != null) && (anObject instanceof Paragraph)) {
			Paragraph paraObject = (Paragraph) anObject;
			return (paraKey.equals(paraObject.getParaKey()));
		}
		return false;
	}

	/**
	 * Returns all of the words and phrases in a paragraph.
	 * 
	 * @return ArrayList of all words
	 */
	public ArrayList<String> getAllWordList() {
		ArrayList<String> allWords = new ArrayList<String>();
		SG elkbSG;
		
		Iterator<SG> sIter = sgList.iterator();
		while (sIter.hasNext()) {
			elkbSG = sIter.next();
			allWords.addAll(elkbSG.getAllWordList());
		}

		return allWords;
	}

	/**
	 * Prints all of the words and phrases in the Paragraph on a separate line
	 * to the standard output. The words and phrasses are printed using the
	 * following format: <CODE>word:keyword:headnum:pos</CODE>
	 */
	public void printAllWords() {
		SG elkbSG;
		ArrayList<String> wordList;

		Iterator<SG> sIter = sgList.iterator();
		while (sIter.hasNext()) {
			elkbSG = sIter.next();
			wordList = elkbSG.getAllWordList();
			Iterator<String> wIter = wordList.iterator();
			while (wIter.hasNext()) {
				System.out.println(wIter.next() + ":" + getParaKey()
						+ ":" + getHeadNum() + ":" + getPOS());
			}
		}
	}

	/**
	 * Prints all the contents of all Semicolon Groups, including references,
	 * without any special formatting. Allows to compare Semicolon Groups to
	 * <i>WordNet</i> synsets.
	 */
	public void printAllSG() {
		SG elkbSG;
		ArrayList<String> wordList;

		Iterator<SG> sIter = sgList.iterator();
		while (sIter.hasNext()) {
			elkbSG = sIter.next();
			wordList = elkbSG.getAllWordList();
			System.out.println(wordList);
		}
	}

}
