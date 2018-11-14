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
 * Represents a <i>Roget's Thesaurus</i> Semicolon Group. For example:
 * <ul>
 * <li>zeal, ardour, ernestness, seriousness;</li>
 * </ul>
 * A Semicolon Group is defined by the following attributes:
 * <UL>
 * <li>Head number</li>
 * <li>Paragraph number</li>
 * <li>Paragraph keyword</li>
 * <li>Part-of-speech</li>
 * <li>Semicolon Group number</li>
 * <li>number of Cross-references</li>
 * <li>number of See references</li>
 * <li>number of See references</li>
 * <li>list of word and phrases</li>
 * <li>list of special tags for the words and phrases</li>
 * <li>list of references</li>
 * </UL>
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 */

public class SG {
	// Attributes
	private int sgNum;

	private ArrayList<String> wordList;

	private ArrayList<String> styleTagList;

	private ArrayList<SemRel> semRelList;

	private int cRefCount;

	private int seeCount;

	private int paraNum;

	private int headNum;

	private String paraKey;

	private String pos;

	/**
	 * Default constructor.
	 */
	public SG() {
		sgNum = 0;
		wordList = new ArrayList<String>();
		styleTagList = new ArrayList<String>();
		semRelList = new ArrayList<SemRel>();
		paraNum = 0;
		headNum = 0;
		cRefCount = 0;
		seeCount = 0;
	}

	/**
	 * Constructor takes a sg num
	 * 
	 * @param num
	 */
	public SG(int num) {
		this();
		sgNum = num;
	}
	
	

	/**
	 * Constructor that sets the Semicolon Group number and the words and phases
	 * that it contains.
	 * 
	 * @param num
	 * @param text
	 */
	public SG(int num, String text) {
		this(num);
		setText(text);
	}

	/**
	 * Constructor that sets the Semicolon Group number, Paragraph number, Head
	 * number, the words and phases of the Semicolon Group and the
	 * part-of-speech.
	 * 
	 * @param numSG
	 * @param numP
	 * @param numH
	 * @param text
	 * @param p
	 */
	public SG(int numSG, int numP, int numH, String text, String p) {
		this();
		sgNum = numSG;
		paraNum = numP;
		headNum = numH;
		pos = p;
		setText(text);
	}


	/**
	 * Constructor that sets the Semicolon Group number, Paragraph number, Head
	 * number, the words and phases of the Semicolon Group and the
	 * part-of-speech.
	 * 
	 * @param numSG
	 * @param numP
	 * @param numH
	 * @param currentSGWords
	 * @param p
	 */
	public SG(int numSG, int numP, int numH, ArrayList<String> currentSGWords, String p) {
		this();
		sgNum = numSG;
		paraNum = numP;
		headNum = numH;
		pos = p;
		for(int i = 0; i < currentSGWords.size(); i++){
			addWord(currentSGWords.get(i));
		}
	}
	
	/**
	 * Returns the number of this Semicolon Group.
	 * 
	 * @return semicolon group number
	 */
	public int getSGNum() {
		return sgNum;
	}

	/**
	 * Sets the number of this Semicolon Group.
	 * 
	 * @param num
	 */
	public void setSGNum(int num) {
		sgNum = num;
	}

	/**
	 * Returns the Paragraph number of this Semicolon Group.
	 * 
	 * @return paragraph number
	 */
	public int getParaNum() {
		return paraNum;
	}

	/**
	 * Sets the Paragraph number of this Semicolon Group.
	 * 
	 * @param num
	 */
	public void setParaNum(int num) {
		paraNum = num;
	}

	/**
	 * Returns the Head number of this Semicolon Group.
	 * 
	 * @return head number
	 */
	public int getHeadNum() {
		return headNum;
	}

	/**
	 * Sets the Head number of this Semicolon Group.
	 * 
	 * @param num
	 */
	public void setHeadNum(int num) {
		headNum = num;
	}

	/**
	 * Returns the Paragraph keyword of this Semicolon Group.
	 * 
	 * @return paragraph key
	 */
	public String getParaKey() {
		return paraKey;
	}

	/**
	 * Sets the Paragraph keyword of this Semicolon Group.
	 * 
	 * @param key
	 */
	public void setParaKey(String key) {
		paraKey = key;
	}

	/**
	 * Returns the part-of-speech of this Semicolon Group.
	 * 
	 * @return part of speech
	 */
	public String getPOS() {
		return pos;
	}

	/**
	 * Sets the part-of-speech of this Semicolon Group.
	 * 
	 * @param p
	 */
	public void setPOS(String p) {
		pos = p;
	}

	/**
	 * Returns the number Cross-references in this Semicolon Group.
	 * 
	 * @return number of cross references
	 */
	public int getCRefCount() {
		return cRefCount;
	}

	/**
	 * Returns the number See refereces in this Semicolon Group.
	 * 
	 * @return number of See references
	 */
	public int getSeeCount() {
		return seeCount;
	}

	/**
	 * Returns a symbolic adress of this Semicolon Group. It is representd as
	 * <TT>Head Number.Paragraph Number.Semicolon Group Number</TT>.
	 * 
	 * @return the offset
	 */
	public String getOffset() {
		String offset = getHeadNum() + "." + getParaNum() + "." + getSGNum();
		return offset;
	}

	/**
	 * Adds a word or phrase to this Semicolon Group.
	 * 
	 * @param word
	 */
	public void addWord(String word) {
		addWord(word, "");
	}

	/**
	 * Adds a word or phrase and its style tag to this Semicolon Group.
	 * 
	 * @param word
	 * @param tag
	 */
	public void addWord(String word, String tag) {
		wordList.add(word);
		styleTagList.add(tag);
	}

	/**
	 * Adds a relation to this Semicolon Group
	 * 
	 * @param rel
	 */
	public void addSemRel(SemRel rel) {
		semRelList.add(rel);
	}

	/**
	 * A cross-reference is received in the following format: 486 <i>cause doubt</i>
	 * 
	 * @param cref
	 */
	private void addCRef(String cref) {
		StringTokenizer st = new StringTokenizer(cref, "<>");
		String headNum = st.nextToken();
		headNum = headNum.trim();
		int num = Integer.parseInt(headNum);
		st.nextToken();
		String refName = st.nextToken();
		refName = refName.trim();
		SemRel rel = new SemRel("cref", num, refName);
		addSemRel(rel);
		cRefCount++;
	}

	/**
	 * A see reference must be supplied in the following format: <i>relevance</i>
	 * 
	 * @param seeStr
	 */
	private void addSee(String seeStr) {
		StringTokenizer st = new StringTokenizer(seeStr, "<>");
		st.nextToken();
		String refName = st.nextToken();
		refName = refName.trim();
		SemRel rel = new SemRel("see", headNum, refName);
		addSemRel(rel);
		seeCount++;
	}

	/**
	 * Returns the list of words and phrases, minus the references, contained in
	 * this Semicolon Group.
	 * 
	 * @return ArrayList of words
	 */
	public ArrayList<String> getWordList() {
		return wordList;
	}

	/**
	 * Returns the list of words and phrases, including the references,
	 * contained in this Semicolon Group.
	 * 
	 * @return ArrayList of words
	 */
	public ArrayList<String> getAllWordList() {
		ArrayList<String> allWords = new ArrayList<String>(wordList);
		// remove punctuation
		//allWords.remove(allWords.size() - 1);
		// now we must get the words from the semRelList
		Iterator<SemRel> iter = semRelList.iterator();
		while (iter.hasNext()) {
			SemRel rel = iter.next();
			String refName = rel.getRefName();
			allWords.add(refName);
		}

		return allWords;
	}

	/**
	 * Returns the list of style tags of this Semicolon Group.
	 * 
	 * @return ArrayList of tag styles
	 */
	public ArrayList<String> getStyleTagList() {
		return styleTagList;
	}

	/**
	 * Returns the list of relations of this Semicolon Group.
	 * 
	 * @return ArrayList of semantic relationships
	 */
	public ArrayList<SemRel> getSemRelList() {
		return semRelList;
	}

	/**
	 * Returns the number of words and phrases in this Semicolon Group.
	 * 
	 * @return number of words and phrases
	 */
	public int getWordCount() {
		return wordList.size();
	}

	/**
	 * Sets the words and phrases used in this Semicolon Group. The words and
	 * phrases are supplied as an <i>ELKB</i> formatted string.
	 * 
	 * @param text
	 */
	public void setText(String text) {
		StringTokenizer st = new StringTokenizer(text, ",");
		String token = new String();
		
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			token = token.trim();

			// remove the </i> tags as they are often misplaced and
			// don't give us any information
			token = token.replaceAll("</i>", "");
			token = token.trim();

			// All the different tag cases
			// Create private method for every case
			if (token.startsWith("<i>")) {
				// we have an italicized word, take note of it
				token = token.replaceFirst("<i>", "");

				// an italicized word can also be followed by ! or ?
				// we will simply remove these, as we can only keep
				// one tag for now...
				token = token.replaceFirst("\\?", "");
				token = token.replaceFirst("!", "");
				token = token.trim();

				addWord(token, "<i>");

			} else if (token.startsWith("<etc>")) {
				// example: <etc>etc. adj.</etc>
				// remove <etc> and </etc>
				token = token.replaceFirst("<etc>", "");
				token = token.replaceFirst("</etc>", "");
				token = token.trim();
				addWord(token, "<etc>");

			} else if (token.startsWith("<cref>")) {
				// example: <cref>658 <i>hospital</i></cref>
				// remove <cref> and </cref>
				token = token.replaceFirst("<cref>", "");
				token = token.replaceFirst("</cref>", "");
				token = token.trim();
				addCRef(token);

			} else if (token.startsWith("<see>")) {
				// example: <see><i>frenzied</i></see>
				// remove <see> and </see>
				token = token.replaceFirst("<see>", "");
				token = token.replaceFirst("</see>", "");
				token = token.trim();
				addSee(token);

			} else if (token.endsWith("(<derog>)")) {
				// example: pommie (<derog>)
				// remove (<derog>)
				token = token.replaceFirst("\\(<derog>\\)", "");
				token = token.trim();
				addWord(token, "<derog>");

			} else if (token.endsWith("(<e>)")) {
				// example: d�racin�(<e>) (French tag)
				// remove (<e>)
				token = token.replaceFirst("\\(<e>\\)", "");
				token = token.trim();
				addWord(token, "<e>");

			} else if (token.matches(".*(<tdmk>).*")) {
				// example: Gallup poll (<tdmk>)
				// remove (<tdmk>)
				token = token.replaceFirst("\\(<tdmk>\\)s?", "");
				token = token.trim();
				addWord(token, "<tdmk>");

			} else if (token.matches(".*(<vulg>).*")) {
				// example: cock (<vulg>)
				// remove (<vulg>)
				token = token.replaceFirst("\\(<vulg>\\)s?", "");
				token = token.trim();
				addWord(token, "<vulg>");

			} else if (token.equals(";") || token.equals(".")
					|| token.equals("!") || token.equals("?")) {
				// parseLast
				addWord(token, "<punct>");

			} else if (token.endsWith("?")) {
				// example: how come?
				// note, nothing is being done with the ? token
				token = token.replaceFirst("\\?", "");
				token = token.trim();
				addWord(token, "?");

			} else if (token.endsWith("!")) {
				// example: off with his head!
				// note, nothing is being done with the ! token
				token = token.replaceFirst("!", "");
				token = token.trim();
				addWord(token, "!");

			} else {
				addWord(token);
			}
		}
	}

	/**
	 * Converts to a string representation the <TT>SG</TT> object.
	 */
	public String toString() {
		String info = new String();
		info = super.toString();
		Iterator<String> iter = wordList.iterator();
		while (iter.hasNext()) {
			info += "@" + iter.next();
		}
		info += "@" + getWordCount();
		return info;
	}

	/**
	 * Prints this Semicolon Group to the standard output.
	 */
	public void print() {
		System.out.println(format());
	}

	/**
	 * Returns a string containing all of the words and phrases in the Semicolon
	 * Group minus the references.
	 * 
	 * @return semicolon group in a string
	 */
	public String getGroup() {
		String info = new String();
		String sFinal = ";";
		int i = 0;

		Iterator<String> iter = wordList.iterator();
		while (iter.hasNext()) {

			if (styleTagList.get(i) != "<punct>") {
				info += iter.next();
				info += ", ";
			} else {
				sFinal = iter.next();
			}
			i++;
		}

		// Delete the extra ", "
		info = info.replaceFirst(", $", "");
		info += sFinal;

		return info;
	}

	/**
	 * Returns a string containing only the references of this Semicolon Group.
	 * 
	 * @return String of references
	 */
	public String getReference() {
		String sRel = new String();

		Iterator<SemRel> refIt = semRelList.iterator();
		while (refIt.hasNext()) {
			SemRel rel = refIt.next();
			// sRel += rel.getType() + ": ";
			sRel += rel.getRefName() + " ";
			sRel += rel.getHeadNum() + " ";
		}

		// Delete the extra ", "
		int length = sRel.length();
		if (length > 0) {
			sRel = sRel.replaceFirst(", $", "");
			// The next line should be included, but for the sake
			// of the lexical chains program, we will return
			// a string that can be parsed...
			// sRel = "#" + sRel + "# ";
			sRel += getPOS();
		}

		return sRel;
	}

	/**
	 * Returns this Semicolon Group formatted in a string, including references,
	 * style tags and punctuation.
	 * 
	 * @return semicolon group as a string
	 */
	public String format() {
		String info = new String();
		String sRel = new String();
		String sFinal = ";";
		int i = 0;

		Iterator<String> iter = wordList.iterator();
		while (iter.hasNext()) {

			if (styleTagList.get(i) != "<punct>") {
				info += iter.next();
				info += ", ";
			} else {
				sFinal = iter.next();
			}

			// if ( styleTagList.get(i) != "" ) {
			// info += " (" + styleTagList.get(i) + "), ";
			// } else {

			// }
			i++;
		}

		// Delete the extra ", "
		info = info.replaceFirst(", $", "");
		info += sFinal;

		Iterator<SemRel> refIt = semRelList.iterator();
		while (refIt.hasNext()) {
			SemRel rel = refIt.next();
			sRel += rel.getType() + ": ";
			sRel += rel.getHeadNum() + " ";
			sRel += rel.getRefName() + ", ";
		}

		// Delete the extra ", "
		int length = sRel.length();
		if (length > 0) {
			sRel = "<" + sRel;
			sRel = sRel.replaceFirst(", $", "");
			sRel += ">";
		}

		info += " " + sRel;

		return info;
	}

	/**
	 * Returns a list of Semicolon Groups with their symbolic addresses.
	 * 
	 * @return ArrayList of offsets
	 */
	public ArrayList<String> getOffsetList() {
		ArrayList<String> arr = getAllWordList();
		arr.add(0, getOffset());
		return arr;
	}

}
