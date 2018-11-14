/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package ca.site.elkb;

import java.io.*;
import java.util.*;

/**
 * Represents the Text of <i>Roget's Thesaurus</i>. The following information
 * is maintained for the Text:
 * <UL>
 * <li>number of Heads</li>
 * <li>number of Paragraphs</li>
 * <li>number of words and phrases</li>
 * <li>number of Semicolon Groups</li>
 * <li>number of Cross-references</li>
 * <li>number of See references</li>
 * </UL>
 * This information is also kept for all nouns, adjectives, verbs, adverbs and
 * interjections.
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 */

public class RogetText implements Serializable {

	private static final long serialVersionUID = 9175428851557575758L;

	// Attributes
	private ArrayList<Head> headList;

	private int wordCount;

	private int headCount;

	private int paraCount;

	private int sgCount;

	private int cRefCount;

	private int seeCount;

	private int nCount;

	private int adjCount;

	private int vbCount;

	private int advCount;

	private int intCount;
	
	private int phrCount;
	
	private int pronCount;
	
	private int prefCount;

	private int nParaCount;

	private int adjParaCount;

	private int vbParaCount;

	private int advParaCount;

	private int intParaCount;
	
	private int phrParaCount;
	
	private int pronParaCount;
	
	private int prefParaCount;

	private int nSGCount;

	private int adjSGCount;

	private int vbSGCount;

	private int advSGCount;

	private int intSGCount;
	
	private int phrSGCount;
	
	private int pronSGCount;
	
	private int prefSGCount;

	private int nCRefCount;

	private int adjCRefCount;

	private int vbCRefCount;

	private int advCRefCount;

	private int intCRefCount;
	
	private int phrCRefCount;
	
	private int pronCRefCount;
	
	private int prefCRefCount;

	private int nSeeCount;

	private int adjSeeCount;

	private int vbSeeCount;

	private int advSeeCount;

	private int intSeeCount;
	
	private int phrSeeCount;
	
	private int pronSeeCount;
	
	private int prefSeeCount;

	private String sPath;

	/**
	 * Initializes variables.
	 */
	private void init() {
		wordCount = 0;
		headCount = 0;
		paraCount = 0;
		sgCount = 0;
		cRefCount = 0;
		seeCount = 0;

		nCount = 0;
		adjCount = 0;
		vbCount = 0;
		advCount = 0;
		intCount = 0;
		phrCount = 0;
		pronCount = 0;
		prefCount = 0;

		nParaCount = 0;
		adjParaCount = 0;
		vbParaCount = 0;
		advParaCount = 0;
		intParaCount = 0;
		phrParaCount = 0;
		pronParaCount = 0;
		prefParaCount = 0;

		nSGCount = 0;
		adjSGCount = 0;
		vbSGCount = 0;
		advSGCount = 0;
		intSGCount = 0;
		phrSGCount = 0;
		pronSGCount = 0;
		prefSGCount = 0;

		nCRefCount = 0;
		adjCRefCount = 0;
		vbCRefCount = 0;
		advCRefCount = 0;
		intCRefCount = 0;
		phrCRefCount = 0;
		pronCRefCount = 0;
		prefCRefCount = 0;

		nSeeCount = 0;
		adjSeeCount = 0;
		vbSeeCount = 0;
		advSeeCount = 0;
		intSeeCount = 0;
		phrSeeCount = 0;
		pronSeeCount = 0;
		prefSeeCount = 0;
	}

	/**
	 * Adds an empty head to headList
	 */
	private void addEmptyHead() {
		// Add an empty Head so that our index is correct
		Head newHead = new Head();
		headList.add(newHead);
	}

	/**
	 * Default constructor.
	 */
	public RogetText() {
		init();
		headList = new ArrayList<Head>();
		addEmptyHead();
	}

	/**
	 * Constructor which specifies the number of Heads contained in this
	 * RogetText.
	 * 
	 * @param capacity
	 */
	public RogetText(int capacity) {
		init();
		headList = new ArrayList<Head>(capacity);
		addEmptyHead();
	}

	/**
	 * Constructor that builds the <TT>RogetText</TT> object by specifying the
	 * number of Heads and using the information contained files which end with
	 * <TT>.txt</TT>.
	 * 
	 * @param capacity
	 * @param fileName
	 */
	public RogetText(int capacity, String fileName) {
		this(capacity);
		loadFromFile(capacity, fileName, ".xml");
	}

	/**
	 * Constructor which specifies the directory in which the Heads are found.
	 * 
	 * @param path
	 */
	public RogetText(String path) {
		this();
		sPath = path;
	}

	/**
	 * Constructor that builds the <TT>RogetText</TT> object by specifying the
	 * number of Heads and using the information contained files which end with
	 * the given extension.
	 * 
	 * @param capacity
	 * @param fileName
	 * @param extension
	 */
	public RogetText(int capacity, String fileName, String extension) {
		this(capacity);
		loadFromFile(capacity, fileName, extension);
	}

	/**
	 * Loads head info from a file.
	 * 
	 * @param capacity
	 * @param fileName
	 * @param extension
	 */
	private void loadFromFile(int capacity, String fileName, String extension) {
		for (int i = 1; i <= capacity; i++) {
			// System.out.print("#");
			try {
				Head headObj = new Head(fileName + i + extension);
				addHead(headObj);
			} catch (Exception e) {
				Head headObj = new Head(headCount + 1, "EMPTY", 0, 0);
				addHead(headObj);
				System.out.println(fileName + i + extension);
				System.out.println(e);
			}
		}
	}

	/**
	 * Adds a <TT>Head</TT> object to this RogetText.
	 * 
	 * @param headObj
	 */
	public void addHead(Head headObj) {
		// System.out.println(headObj);
		headList.add(headObj);
		System.out.println("adding object");
		headCount++;

		// Adjust the other values
		paraCount += headObj.getParaCount();
		sgCount += headObj.getSGCount();
		wordCount += headObj.getWordCount();
		cRefCount += headObj.getCRefCount();
		seeCount += headObj.getSeeCount();

		nCount += headObj.getNCount();
		adjCount += headObj.getAdjCount();
		vbCount += headObj.getVbCount();
		advCount += headObj.getAdvCount();
		intCount += headObj.getIntCount();
		phrCount += headObj.getPhrCount();
		prefCount += headObj.getPrefCount();
		pronCount += headObj.getPronCount();

		nParaCount += headObj.getNParaCount();
		adjParaCount += headObj.getAdjParaCount();
		vbParaCount += headObj.getVbParaCount();
		advParaCount += headObj.getAdvParaCount();
		intParaCount += headObj.getIntParaCount();
		phrParaCount += headObj.getPhrParaCount();
		prefParaCount += headObj.getPrefParaCount();
		pronParaCount += headObj.getPronParaCount();

		nSGCount += headObj.getNSGCount();
		adjSGCount += headObj.getAdjSGCount();
		vbSGCount += headObj.getVbSGCount();
		advSGCount += headObj.getAdvSGCount();
		intSGCount += headObj.getIntSGCount();
		phrSGCount += headObj.getPhrSGCount();
		prefSGCount += headObj.getPrefSGCount();
		pronSGCount += headObj.getPronSGCount();

		nCRefCount += headObj.getNCRefCount();
		adjCRefCount += headObj.getAdjCRefCount();
		vbCRefCount += headObj.getVbCRefCount();
		advCRefCount += headObj.getAdvCRefCount();
		intCRefCount += headObj.getIntCRefCount();
		phrCRefCount += headObj.getPhrCRefCount();
		prefCRefCount += headObj.getPrefCRefCount();
		pronCRefCount += headObj.getPronCRefCount();

		nSeeCount += headObj.getNSeeCount();
		adjSeeCount += headObj.getAdjSeeCount();
		vbSeeCount += headObj.getVbSeeCount();
		advSeeCount += headObj.getAdvSeeCount();
		intSeeCount += headObj.getIntSeeCount();
		phrSeeCount += headObj.getPhrSeeCount();
		prefSeeCount += headObj.getPrefSeeCount();
		pronSeeCount += headObj.getPronSeeCount();
	}

	/**
	 * Adds a Head which is contained in the specified file to this RogetText.
	 * 
	 * @param fileName
	 */
	public void addHead(String fileName) {
		Head headObj = new Head(fileName);
		addHead(headObj);
	}

	/**
	 * Returns the Head with the specified number.
	 * 
	 * @param headNum
	 * @return Head
	 */
	public Head getHead(int headNum) {
		// This method has changed 16.11.01
		// return (Head) headList.get(headNum);
		Head head = new Head(sPath + headNum + ".xml");
		return head;
	}

	/**
	 * Returns the number of Heads in this RogetText.
	 * 
	 * @return number of heads
	 */
	public int getHeadCount() {
		return headCount;
	}

	/**
	 * Returns the number of Paragraphs in this RogetText.
	 * 
	 * @return number of paragraphs
	 */
	public int getParaCount() {
		return paraCount;
	}

	/**
	 * Returns the number of noun Paragraphs in this RogetText.
	 * 
	 * @return number of noun paragraphs
	 */
	public int getNParaCount() {
		return nParaCount;
	}

	/**
	 * Returns the number of adjective Paragraphs in this RogetText.
	 * 
	 * @return number of adjective paragraphs
	 */
	public int getAdjParaCount() {
		return adjParaCount;
	}

	/**
	 * Returns the number of verb Paragraphs in this RogetText.
	 * 
	 * @return number of verb paragraphs
	 */
	public int getVbParaCount() {
		return vbParaCount;
	}

	/**
	 * Returns the number of adverb Paragraphs in this RogetText.
	 * 
	 * @return number of adverb paragraphs
	 */
	public int getAdvParaCount() {
		return advParaCount;
	}

	/**
	 * Returns the number of interjection Paragraphs in this RogetText.
	 * 
	 * @return number of interjection paragraphs
	 */
	public int getIntParaCount() {
		return intParaCount;
	}
	
	/**
	 * Returns the number of phrase Paragraphs in this RogetText.
	 * 
	 * @return number of phrase paragraphs
	 */
	public int getPhrParaCount() {
		return phrParaCount;
	}
	
	/**
	 * Returns the number of preface Paragraphs in this RogetText.
	 * 
	 * @return number of preface paragraphs
	 */
	public int getPrefParaCount() {
		return prefParaCount;
	}
	
	/**
	 * Returns the number of pronoun Paragraphs in this RogetText.
	 * 
	 * @return number of pronoun paragraphs
	 */
	public int getPronParaCount() {
		return pronParaCount;
	}

	/**
	 * Returns the number of Semicolon Groups in this RogetText.
	 * 
	 * @return number of semicolon groups
	 */
	public int getSGCount() {
		return sgCount;
	}

	/**
	 * Returns the number of noun Semicolon Groups in this RogetText.
	 * 
	 * @return number of noun semicolon groups
	 */
	public int getNSGCount() {
		return nSGCount;
	}

	/**
	 * Returns the number of ajective Semicolon Groups in this RogetText.
	 * 
	 * @return number of adjective semicolon groups
	 */
	public int getAdjSGCount() {
		return adjSGCount;
	}

	/**
	 * Returns the number of verb Semicolon Groups in this RogetText.
	 * 
	 * @return number of verb semicolon groups
	 */
	public int getVbSGCount() {
		return vbSGCount;
	}

	/**
	 * Returns the number of adverb Semicolon Groups in this RogetText.
	 * 
	 * @return number of adverb semicolon groups
	 */
	public int getAdvSGCount() {
		return advSGCount;
	}

	/**
	 * Returns the number of interjection Semicolon Groups in this RogetText.
	 * 
	 * @return number of interjection semicolon groups
	 */
	public int getIntSGCount() {
		return intSGCount;
	}
	
	/**
	 * Returns the number of phrase Semicolon Groups in this RogetText.
	 * 
	 * @return number of phrase semicolon groups
	 */
	public int getPhrSGCount() {
		return phrSGCount;
	}
	
	/**
	 * Returns the number of preface Semicolon Groups in this RogetText.
	 * 
	 * @return number of preface semicolon groups
	 */
	public int getPrefSGCount() {
		return prefSGCount;
	}
	
	/**
	 * Returns the number of pronoun Semicolon Groups in this RogetText.
	 * 
	 * @return number of pronoun semicolon groups
	 */
	public int getPronSGCount() {
		return pronSGCount;
	}

	/**
	 * Returns the number of See referencs in this RogetText.
	 * 
	 * @return number of see references
	 */
	public int getSeeCount() {
		return seeCount;
	}

	/**
	 * Returns the number of noun See referencs in this RogetText.
	 * 
	 * @return number of noun see references
	 */
	public int getNSeeCount() {
		return nSeeCount;
	}

	/**
	 * Returns the number of adjective See referencs in this RogetText.
	 * 
	 * @return number of adjective see references
	 */
	public int getAdjSeeCount() {
		return adjSeeCount;
	}

	/**
	 * Returns the number of verb See referencs in this RogetText.
	 * 
	 * @return number of verb see references
	 */
	public int getVbSeeCount() {
		return vbSeeCount;
	}

	/**
	 * Returns the number of adverb See referencs in this RogetText.
	 * 
	 * @return number of adverb see references
	 */
	public int getAdvSeeCount() {
		return advSeeCount;
	}

	/**
	 * Returns the number of interjection See referencs in this RogetText.
	 * 
	 * @return number of interjection see references
	 */
	public int getIntSeeCount() {
		return intSeeCount;
	}
	
	/**
	 * Returns the number of phrase See referencs in this RogetText.
	 * 
	 * @return number of phrase see references
	 */
	public int getPhrSeeCount() {
		return phrSeeCount;
	}
	
	/**
	 * Returns the number of preface See referencs in this RogetText.
	 * 
	 * @return number of preface see references
	 */
	public int getPrefSeeCount() {
		return prefSeeCount;
	}
	
	/**
	 * Returns the number of pronoun See referencs in this RogetText.
	 * 
	 * @return number of pronoun see references
	 */
	public int getPronSeeCount() {
		return pronSeeCount;
	}
	
	/**
	 * Returns the number of Cross-references in this RogetText.
	 * 
	 * @return number of crossreferences
	 */
	public int getCRefCount() {
		return cRefCount;
	}

	/**
	 * Returns the number of noun Cross-references in this RogetText.
	 * 
	 * @return number of noun crossreferences
	 */
	public int getNCRefCount() {
		return nCRefCount;
	}

	/**
	 * Returns the number of adjective Cross-references in this RogetText.
	 * 
	 * @return number of adjective crossreferences
	 */
	public int getAdjCRefCount() {
		return adjCRefCount;
	}

	/**
	 * Returns the number of verb Cross-references in this RogetText.
	 * 
	 * @return number of verb crossreferences
	 */
	public int getVbCRefCount() {
		return vbCRefCount;
	}

	/**
	 * Returns the number of adverb Cross-references in this RogetText.
	 * 
	 * @return number of adverb crossreferences
	 */
	public int getAdvCRefCount() {
		return advCRefCount;
	}

	/**
	 * Returns the number of interjection Cross-references in this RogetText.
	 * 
	 * @return number of interjection crossreferences
	 */
	public int getIntCRefCount() {
		return intCRefCount;
	}
	
	/**
	 * Returns the number of phrase Cross-references in this RogetText.
	 * 
	 * @return number of phrase crossreferences
	 */
	public int getPhrCRefCount() {
		return phrCRefCount;
	}
	
	/**
	 * Returns the number of preface Cross-references in this RogetText.
	 * 
	 * @return number of preface crossreferences
	 */
	public int getPrefCRefCount() {
		return prefCRefCount;
	}
	
	/**
	 * Returns the number of pronoun Cross-references in this RogetText.
	 * 
	 * @return number of pronoun crossreferences
	 */
	public int getPronCRefCount() {
		return pronCRefCount;
	}

	/**
	 * Returns the number of words and phrases in this RogetText.
	 * 
	 * @return number of words
	 */
	public int getWordCount() {
		return wordCount;
	}

	/**
	 * Returns the number of nouns in this RogetText.
	 * 
	 * @return number of nouns
	 */
	public int getNCount() {
		return nCount;
	}

	/**
	 * Returns the number of adjectives in this RogetText.
	 * 
	 * @return number of adjectives
	 */
	public int getAdjCount() {
		return adjCount;
	}

	/**
	 * Returns the number of verbs in this RogetText.
	 * 
	 * @return number of verbs
	 */
	public int getVbCount() {
		return vbCount;
	}

	/**
	 * Returns the number of adverbs in this RogetText.
	 * 
	 * @return number of adverbs
	 */
	public int getAdvCount() {
		return advCount;
	}

	/**
	 * Returns the number of interjections in this RogetText.
	 * 
	 * @return number of interjections
	 */
	public int getIntCount() {
		return intCount;
	}
	
	/**
	 * Returns the number of phrases in this RogetText.
	 * 
	 * @return number of phrases
	 */
	public int getPhrCount() {
		return phrCount;
	}
	
	/**
	 * Returns the number of preface in this RogetText.
	 * 
	 * @return number of preface
	 */
	public int getPrefCount() {
		return prefCount;
	}
	
	/**
	 * Returns the number of pronoun in this RogetText.
	 * 
	 * @return number of pronoun
	 */
	public int getPronCount() {
		return pronCount;
	}

	/**
	 * Converts to a string representation the <TT>RogetText</TT> object.
	 */
	public String toString() {
		String info = new String();
		info = super.toString();
		info += "@" + getHeadCount() + "@" + getParaCount();
		info += "@" + getSGCount() + "@" + getWordCount();
		return info;
	}

	/**
	 * Prints the contents of a Head specified by its number to the standard
	 * output.
	 * 
	 * @param headNum
	 */
	public void printHead(int headNum) {
		Head headObj = getHead(headNum);
		headObj.print();
	}
}

