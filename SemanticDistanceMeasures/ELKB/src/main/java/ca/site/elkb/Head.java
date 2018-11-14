/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package ca.site.elkb;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a <i>Roget's Thesaurus</i> Head. A Head is defined by the
 * following attributes:
 * <UL>
 * <li>Head number</li>
 * <li>Head name</li>
 * <li>Class number</li>
 * <li>Section num</li>
 * <li>list of paragraphs</li>
 * <li>number of paragraphs</li>
 * <li>number of semicolon groups</li>
 * <li>number of words and phrases</li>
 * <li>number of cross-references</li>
 * <li>number of see references</li>
 * </UL>
 * The relative postions of the noun, adjective verb, adverb and interjection
 * paragraphs in the array of paragarphs is kept by the <TT>nStart</TT>, <TT>adjStart</TT>,
 * <TT>vbStart</TT>, <TT>advStart</TT>, and <TT>intStart</TT>
 * attributes.
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 */

public class Head {
	
	private int headNum;

	private String headName;

	private int classNum;

	private int sectionNum;

	private int subSectionNum;

	private int headGroupNum;

	private ArrayList<Paragraph> nParaList;

	private ArrayList<Paragraph> adjParaList;

	private ArrayList<Paragraph> vbParaList;

	private ArrayList<Paragraph> advParaList;

	private ArrayList<Paragraph> intParaList;
	
	private ArrayList<Paragraph> phrParaList;
	
	private ArrayList<Paragraph> prefParaList;
	
	private ArrayList<Paragraph> pronParaList;

	private int nStart;

	private int adjStart;

	private int vbStart;

	private int advStart;

	private int intStart;
	
	private int phrStart;
	
	private int prefStart;
	
	private int pronStart;

	private int cRefCount;

	private int seeCount;

	// Should I repeat these for each POS?
	private int wordCount;

	private int sgCount;

	private int paraCount;

	private int nCount;

	private int adjCount;

	private int vbCount;

	private int advCount;

	private int intCount;
	
	private int phrCount;
	
	private int prefCount;
	
	private int pronCount;

	private int nParaCount;

	private int adjParaCount;

	private int vbParaCount;

	private int advParaCount;

	private int intParaCount;
	
	private int phrParaCount;
	
	private int prefParaCount;
	
	private int pronParaCount;

	private int nSGCount;

	private int adjSGCount;

	private int vbSGCount;

	private int advSGCount;

	private int intSGCount;
	
	private int phrSGCount;
	
	private int prefSGCount;
	
	private int pronSGCount;

	private int nCRefCount;

	private int adjCRefCount;

	private int vbCRefCount;

	private int advCRefCount;

	private int intCRefCount;
	
	private int phrCRefCount;
	
	private int prefCRefCount;
	
	private int pronCRefCount;

	private int nSeeCount;

	private int adjSeeCount;

	private int vbSeeCount;

	private int advSeeCount;

	private int intSeeCount;
	
	private int phrSeeCount;
	
	private int prefSeeCount;
	
	private int pronSeeCount;

	/**
	 * Default constructor.
	 */
	public Head() {
		headNum = 0;
		headName = new String();
		classNum = 0;
		sectionNum = 0;

		nParaList = new ArrayList<Paragraph>();
		adjParaList = new ArrayList<Paragraph>();
		vbParaList = new ArrayList<Paragraph>();
		advParaList = new ArrayList<Paragraph>();
		intParaList = new ArrayList<Paragraph>();
		phrParaList = new ArrayList<Paragraph>();
		prefParaList = new ArrayList<Paragraph>();
		pronParaList = new ArrayList<Paragraph>();

		nStart = -1;
		adjStart = -1;
		vbStart = -1;
		advStart = -1;
		intStart = -1;
		phrStart = -1;
		prefStart = -1;
		pronStart = -1;

		paraCount = 0;
		wordCount = 0;
		sgCount = 0;
		cRefCount = 0;
		seeCount = 0;

		nCount = 0;
		adjCount = 0;
		vbCount = 0;
		advCount = 0;
		intCount = 0;
		phrCount = 0;
		prefCount = 0;
		pronCount = 0;

		nParaCount = 0;
		adjParaCount = 0;
		vbParaCount = 0;
		advParaCount = 0;
		intParaCount = 0;
		phrParaCount = 0;
		prefParaCount = 0;
		pronParaCount = 0;

		nSGCount = 0;
		adjSGCount = 0;
		vbSGCount = 0;
		advSGCount = 0;
		intSGCount = 0;
		phrSGCount = 0;
		prefSGCount = 0;
		pronSGCount = 0;

		nCRefCount = 0;
		adjCRefCount = 0;
		vbCRefCount = 0;
		advCRefCount = 0;
		intCRefCount = 0;
		phrCRefCount = 0;
		prefCRefCount = 0;
		pronCRefCount = 0;

		nSeeCount = 0;
		adjSeeCount = 0;
		vbSeeCount = 0;
		advSeeCount = 0;
		intSeeCount = 0;
		phrSeeCount = 0;
		prefSeeCount = 0;
		pronSeeCount = 0;
	}

	/**
	 * Constructor which sets the Head number and name, as well as the Class and
	 * Section number.
	 * 
	 * @param num
	 * @param name
	 * @param clNum
	 * @param section
	 */
	public Head(int num, String name, int clNum, int section) {
		this();
		headNum = num;
		headName = name;
		classNum = clNum;
		sectionNum = section;
	}

	/**
	 * Constructor that builds the <TT>Head</TT> object using the information
	 * contained in a file. The default location of the head files for the
	 * <i>ELKB</i> is <TT>$HOME/roget_elkb/heads</TT> directory.
	 * 
	 * @param fname
	 */
	public Head(String fname) {
		this();
		loadFromFile(fname);
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
	 * Returns the Class number of this Head.
	 * 
	 * @return class number
	 */
	public int getClassNum() {
		return classNum;
	}

	/**
	 * Sets the Class number of this Head.
	 * 
	 * @param num
	 */
	public void setClassNum(int num) {
		classNum = num;
	}

	/**
	 * Returns the Section number of this Head.
	 * 
	 * @return section number
	 */
	public int getSectionNum() {
		return sectionNum;
	}

	/**
	 * Sets the Section number of this Head.
	 * 
	 * @param num
	 */
	public void setSectionNum(int num) {
		sectionNum = num;
	}
	
	/**
	 * Returns the Sub-Section number of this Head.
	 * 
	 * @return sub section number
	 */
	public int getSubSectionNum() {
		return subSectionNum;
	}

	/**
	 * Sets the Sub-Section number of this Head.
	 * 
	 * @param num
	 */
	public void setSubSectionNum(int num) {
		subSectionNum = num;
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
	 * Sets the Head Group number of this Head.
	 * 
	 * @param num
	 */
	public void setHeadGroupNum(int num) {
		headGroupNum = num;
	}
	
	/**
	 * Increments the paragraph count of this Head.
	 */
	public void incrementParaCount() {
		paraCount++;
	}

	/**
	 * Returns the index of the first noun paragraph in the array of <TT>Pragraph</TT>
	 * objects of this Head.
	 * 
	 * @return noun start paragraph number
	 */
	public int getNStart() {
		return nStart;
	}

	/**
	 * Sets Noun start paragraph.
	 * 
	 * @param start
	 */
	private void setNStart(int start) {
		nStart = start;
	}

	/**
	 * Returns the index of the first adjective paragraph in the array of <TT>Pragraph</TT>
	 * objects of this Head.
	 * 
	 * @return adjective start paragraph number
	 */
	public int getAdjStart() {
		return adjStart;
	}

	/**
	 * Sets Adjective start paragraph.
	 * 
	 * @param start
	 */
	private void setAdjStart(int start) {
		adjStart = start;
	}

	/**
	 * Returns the index of the first verb paragraph in the array of <TT>Pragraph</TT>
	 * objects of this Head.
	 * 
	 * @return verb start paragraph number
	 */
	public int getVbStart() {
		return vbStart;
	}

	/**
	 * Sets Verb start paragraph.
	 * 
	 * @param start
	 */
	private void setVbStart(int start) {
		vbStart = start;
	}

	/**
	 * Returns the index of the first adverb paragraph in the array of <TT>Pragraph</TT>
	 * objects of this Head.
	 * 
	 * @return adverb start paragraph number
	 */
	public int getAdvStart() {
		return adjStart;
	}

	/**
	 * Set Adverb start paragraph.
	 * 
	 * @param start
	 */
	private void setAdvStart(int start) {
		advStart = start;
	}

	/**
	 * Returns the index of the first interjection paragraph in the array of
	 * <TT>Pragraph</TT> objects of this Head.
	 * 
	 * @return interjection start paragraph number
	 */
	public int getIntStart() {
		return intStart;
	}

	/**
	 * Set Interjection start paragraph.
	 * 
	 * @param start
	 */
	private void setIntStart(int start) {
		intStart = start;
	}
	
	/**
	 * Returns the index of the first phrase paragraph in the array of
	 * <TT>Pragraph</TT> objects of this Head.
	 * 
	 * @return phrases paragraph start number
	 */
	public int getPhrStart() {
		return phrStart;
	}

	/**
	 * Set Phrase start paragraph.
	 * 
	 * @param start
	 */
	private void setPhrStart(int start) {
		phrStart = start;
	}
	
	/**
	 * Returns the index of the first preface paragraph in the array of
	 * <TT>Pragraph</TT> objects of this Head.
	 * 
	 * @return preface start paragraph number
	 */
	public int getPrefStart() {
		return prefStart;
	}

	/**
	 * Set Preface start paragraph.
	 * 
	 * @param start
	 */
	private void setPrefStart(int start) {
		prefStart = start;
	}
	
	/**
	 * Returns the index of the first pronoun paragraph in the array of
	 * <TT>Pragraph</TT> objects of this Head.
	 * 
	 * @return pronoun start paragraph number
	 */
	public int getPronStart() {
		return pronStart;
	}

	/**
	 * Set Pronoun start paragraph.
	 * 
	 * @param start
	 */
	private void setPronStart(int start) {
		pronStart = start;
	}

	/**
	 * Returns the number of words of this Head.
	 * 
	 * @return number of words
	 */
	public int getWordCount() {
		return wordCount;
	}

	/**
	 *  Returns the number of noun word and phrases of this Head.
	 *  
	 * @return number of nouns
	 */
	public int getNCount() {
		return nCount;
	}

	/**
	 * Returns the number of adjective word and phrases of this Head.
	 * 
	 * @return number of adjectives
	 */
	public int getAdjCount() {
		return adjCount;
	}

	/**
	 * Returns the number of verb word and phrases of this Head.
	 * 
	 * @return number of verbs
	 */
	public int getVbCount() {
		return vbCount;
	}

	/**
	 * Returns the number of adverb word and phrases of this Head.
	 * 
	 * @return number of adverbs
	 */
	public int getAdvCount() {
		return advCount;
	}

	/**
	 * Returns the number of interjection word and phrases of this Head.
	 * 
	 * @return number of interjections
	 */
	public int getIntCount() {
		return intCount;
	}
	
	/**
	 * Returns the number of phrase word and phrases of this Head.
	 * 
	 * @return number of phrases
	 */
	public int getPhrCount() {
		return phrCount;
	}
	
	/**
	 * Returns the number of pronoun word and phrases of this Head.
	 * 
	 * @return number of pronouns
	 */
	public int getPronCount() {
		return pronCount;
	}
	
	/**
	 * Returns the number of pref word and phrases of this Head.
	 * 
	 * @return number of prefaces
	 */
	public int getPrefCount() {
		return prefCount;
	}
	
	/**
	 * The number of paragraphs for a given part of speech.
	 * 
	 * @param pos
	 * @return number of paragraphs
	 */
	public int getParaCount(String pos){
		if(pos.equals("N.")){
			return nParaCount;
		}
		if(pos.equals("VB.")){
			return vbParaCount;
		}
		if(pos.equals("ADJ.")){
			return adjParaCount;
		}
		if(pos.equals("ADV.")){
			return advParaCount;
		}
		if(pos.equals("INT.")){
			return intParaCount;
		}
		if(pos.equals("PHR.")){
			return phrParaCount;
		}
		if(pos.equals("PREF.")){
			return prefParaCount;
		}
		if(pos.equals("PRON.")){
			return pronParaCount;
		}
		return -1;
	}

	/**
	 * Returns the number of paragraphs of this Head.
	 * 
	 * @return paragraph count
	 */
	public int getParaCount() {
		return paraCount;
	}

	/**
	 * Returns the number of noun paragraphs of this Head.
	 * 
	 * @return noun paragraph count
	 */
	public int getNParaCount() {
		return nParaCount;
	}

	/**
	 * Returns the number of adjective paragraphs of this Head.
	 * 
	 * @return adjective paragraph count
	 */
	public int getAdjParaCount() {
		return adjParaCount;
	}

	/**
	 * Returns the number of verb paragraphs of this Head.
	 * 
	 * @return verb paragraph count
	 */
	public int getVbParaCount() {
		return vbParaCount;
	}

	/**
	 * Returns the number of adverb paragraphs of this Head.
	 * 
	 * @return adverb paragraph count
	 */
	public int getAdvParaCount() {
		return advParaCount;
	}

	/**
	 * Returns the number of interjection paragraphs of this Head.
	 * 
	 * @return interjection paragraph count
	 */
	public int getIntParaCount() {
		return intParaCount;
	}
	
	/**
	 * Returns the number of phrase paragraphs of this Head.
	 * 
	 * @return phrase paragraph count
	 */
	public int getPhrParaCount() {
		return phrParaCount;
	}
	
	/**
	 * Returns the number of pronoun paragraphs of this Head.
	 * 
	 * @return pronoun paragraph count
	 */
	public int getPronParaCount() {
		return pronParaCount;
	}
	
	/**
	 * Returns the number of pref paragraphs of this Head.
	 * 
	 * @return preface paragraph count
	 */
	public int getPrefParaCount() {
		return prefParaCount;
	}

	/**
	 * Returns the number of semicolon groups of this Head.
	 * 
	 * @return semicolon group count
	 */
	public int getSGCount() {
		return sgCount;
	}

	/**
	 * Returns the number of noun semicolon groups of this Head.
	 * 
	 * @return noun SG count
	 */
	public int getNSGCount() {
		return nSGCount;
	}

	/**
	 * Returns the number of adjective semicolon groups of this Head.
	 * 
	 * @return adjective SG count
	 */
	public int getAdjSGCount() {
		return adjSGCount;
	}

	/**
	 * Returns the number of verb groups of this Head.
	 * 
	 * @return verb SG count
	 */
	public int getVbSGCount() {
		return vbSGCount;
	}

	/**
	 * Returns the number of adverb groups of this Head.
	 * 
	 * @return adverb SG count
	 */
	public int getAdvSGCount() {
		return advSGCount;
	}

	/**
	 * Returns the number of interjection semicolon groups of this Head.
	 * 
	 * @return interjection SG count
	 */
	public int getIntSGCount() {
		return intSGCount;
	}
	
	/**
	 * Returns the number of phrase semicolon groups of this Head.
	 * 
	 * @return phrase SG count
	 */
	public int getPhrSGCount() {
		return phrSGCount;
	}
	
	/**
	 * Returns the number of pronoun semicolon groups of this Head.
	 * 
	 * @return pronoun SG count
	 */
	public int getPronSGCount() {
		return pronSGCount;
	}
	
	/**
	 * Returns the number of pref semicolon groups of this Head.
	 * 
	 * @return preface SG count
	 */
	public int getPrefSGCount() {
		return prefSGCount;
	}

	/**
	 * Returns the number of cross-references of this Head.
	 * 
	 * @return cross reference count
	 */
	public int getCRefCount() {
		return cRefCount;
	}

	/**
	 * Returns the number of noun cross-references of this Head.
	 * 
	 * @return noun cross reference count
	 */
	public int getNCRefCount() {
		return nCRefCount;
	}

	/**
	 * Returns the number of adjective cross-references of this Head.
	 * 
	 * @return adjective cross reference count
	 */
	public int getAdjCRefCount() {
		return adjCRefCount;
	}

	/**
	 * Returns the number of verb cross-references of this Head.
	 * 
	 * @return verb cross reference count
	 */
	public int getVbCRefCount() {
		return vbCRefCount;
	}

	/**
	 * Returns the number of adverb cross-references of this Head.
	 * 
	 * @return adverb cross reference count
	 */
	public int getAdvCRefCount() {
		return advCRefCount;
	}

	/**
	 * Returns the number of interjection cross-references of this Head.
	 * 
	 * @return interjection cross reference count
	 */
	public int getIntCRefCount() {
		return intCRefCount;
	}
	
	/**
	 * Returns the number of phrase cross-references of this Head.
	 * 
	 * @return phrase cross reference count
	 */
	public int getPhrCRefCount() {
		return phrCRefCount;
	}
	
	/**
	 * Returns the number of pronoun cross-references of this Head.
	 * 
	 * @return pronoun cross reference count
	 */
	public int getPronCRefCount() {
		return pronCRefCount;
	}
	
	/**
	 * Returns the number of pref cross-references of this Head.
	 * 
	 * @return preface cross reference count
	 */
	public int getPrefCRefCount() {
		return prefCRefCount;
	}

	/**
	 * Returns the number of see references of this Head.
	 * 
	 * @return see count
	 */
	public int getSeeCount() {
		return seeCount;
	}

	/**
	 * Returns the number of noun see references of this Head.
	 * 
	 * @return noun see count
	 */
	public int getNSeeCount() {
		return nSeeCount;
	}

	/**
	 * Returns the number of adjective references of this Head.
	 * 
	 * @return adjective see count
	 */
	public int getAdjSeeCount() {
		return adjSeeCount;
	}

	/**
	 * Returns the number of verb references of this Head.
	 * 
	 * @return verb see count
	 */
	public int getVbSeeCount() {
		return vbSeeCount;
	}

	/**
	 * Returns the number of adverb references of this Head.
	 * 
	 * @return adverb see count
	 */
	public int getAdvSeeCount() {
		return advSeeCount;
	}

	/**
	 * Returns the number of interjection references of this Head.
	 * 
	 * @return interjection see count
	 */
	public int getIntSeeCount() {
		return intSeeCount;
	}
	
	/**
	 * Returns the number of Phrase references of this Head.
	 * 
	 * @return phrase see count
	 */
	public int getPhrSeeCount() {
		return phrSeeCount;
	}
	
	/**
	 * Returns the number of Pronoun references of this Head.
	 * 
	 * @return pronoun see count
	 */
	public int getPronSeeCount() {
		return pronSeeCount;
	}
	
	/**
	 * Returns the number of pref references of this Head.
	 * 
	 * @return preface see count
	 */
	public int getPrefSeeCount() {
		return prefSeeCount;
	}

	/**
	 * Adds a paragraph with a given POS to the paragraph list.
	 * 
	 * @param para
	 * @param pos
	 */
	public void addPara(Paragraph para, String pos) {
		if (pos.equals("N.")) {
			nParaCount++;
			para.setParaNum(nParaCount);
			nParaList.add(para);
			nCount += para.getWordCount();
			nSGCount += para.getSGCount();
			nCRefCount += para.getCRefCount();
			nSeeCount += para.getSeeCount();
		} else if (pos.equals("ADJ.")) {
			adjParaCount++;
			para.setParaNum(adjParaCount);
			adjParaList.add(para);
			adjCount += para.getWordCount();
			adjSGCount += para.getSGCount();
			adjCRefCount += para.getCRefCount();
			adjSeeCount += para.getSeeCount();
		} else if (pos.equals("VB.")) {
			vbParaCount++;
			para.setParaNum(vbParaCount);
			vbParaList.add(para);
			vbCount += para.getWordCount();
			vbSGCount += para.getSGCount();
			vbCRefCount += para.getCRefCount();
			vbSeeCount += para.getSeeCount();
		} else if (pos.equals("ADV.")) {
			advParaCount++;
			para.setParaNum(advParaCount);
			advParaList.add(para);
			advCount += para.getWordCount();
			advSGCount += para.getSGCount();
			advCRefCount += para.getCRefCount();
			advSeeCount += para.getSeeCount();
		} else if (pos.equals("INT.")) {
			intParaCount++;
			para.setParaNum(intParaCount);
			intParaList.add(para);
			intCount += para.getWordCount();
			intSGCount += para.getSGCount();
			intCRefCount += para.getCRefCount();
			intSeeCount += para.getSeeCount();
		} else if (pos.equals("PHR.")){
			phrParaCount++;
			para.setParaNum(phrParaCount);
			phrParaList.add(para);
			phrCount += para.getWordCount();
			phrSGCount += para.getSGCount();
			phrCRefCount += para.getCRefCount();
			phrSeeCount += para.getSeeCount();
		} else if (pos.equals("PREF.")){
			prefParaCount++;
			para.setParaNum(prefParaCount);
			prefParaList.add(para);
			prefCount += para.getWordCount();
			prefSGCount += para.getSGCount();
			prefCRefCount += para.getCRefCount();
			prefSeeCount += para.getSeeCount();
		} else if (pos.equals("PRON.")){
			pronParaCount++;
			para.setParaNum(pronParaCount);
			pronParaList.add(para);
			pronCount += para.getWordCount();
			pronSGCount += para.getSGCount();
			pronCRefCount += para.getCRefCount();
			pronSeeCount += para.getSeeCount();
		}

		// Adjust the various counts
		// paraCount++;
		wordCount += para.getWordCount();
		sgCount += para.getSGCount();
		cRefCount += para.getCRefCount();
		seeCount += para.getSeeCount();
	}

	/**
	 * Returns the a <TT>Paragraph</TT> object specified by the paragraph
	 * number and part-of-speech.
	 * 
	 * @param paraNum
	 * @param pos
	 * @return Paragraph
	 */
	public Paragraph getPara(int paraNum, String pos) {
		Paragraph para = new Paragraph();

		if (pos.equals("N.")) {
			para = nParaList.get(paraNum);
		} else if (pos.equals("ADJ.")) {
			para = adjParaList.get(paraNum);
		} else if (pos.equals("VB.")) {
			para = vbParaList.get(paraNum);
		} else if (pos.equals("ADV.")) {
			para = advParaList.get(paraNum);
		} else if (pos.equals("INT.")) {
			para = intParaList.get(paraNum);
		} else if (pos.equals("PHR.")) {
			para = phrParaList.get(paraNum);
		} else if (pos.equals("PREF.")) {
			para = prefParaList.get(paraNum);
		} else if (pos.equals("PRON.")) {
			para = pronParaList.get(paraNum);
		}

		return para;
	}

	/**
	 * Returns the a <TT>Paragraph</TT> object specified by the paragraph key
	 * and part-of-speech.
	 * 
	 * @param paraKey
	 * @param pos
	 * @return Paragraph
	 */
	public Paragraph getPara(String paraKey, String pos) {
		Paragraph para = new Paragraph();
		Paragraph paraObj = new Paragraph();
		paraObj.setParaKey(paraKey);
		paraObj.setPOS(pos);
		int paraNum;

		// Need some error handling here for the case that the
		// key is not found!!!
		if (pos.equals("N.")) {
			paraNum = nParaList.indexOf(paraObj);
			para = nParaList.get(paraNum);
		} else if (pos.equals("ADJ.")) {
			paraNum = adjParaList.indexOf(paraObj);
			para = adjParaList.get(paraNum);
		} else if (pos.equals("VB.")) {
			paraNum = vbParaList.indexOf(paraObj);
			para = vbParaList.get(paraNum);
		} else if (pos.equals("ADV.")) {
			paraNum = advParaList.indexOf(paraObj);
			para = advParaList.get(paraNum);
		} else if (pos.equals("INT.")) {
			paraNum = intParaList.indexOf(paraObj);
			para = intParaList.get(paraNum);
		} else if (pos.equals("PHR.")) {
			paraNum = phrParaList.indexOf(paraObj);
			para = phrParaList.get(paraNum);
		} else if (pos.equals("PREF.")) {
			paraNum = prefParaList.indexOf(paraObj);
			para = prefParaList.get(paraNum);
		} else if (pos.equals("PRON.")) {
			paraNum = pronParaList.indexOf(paraObj);
			para = pronParaList.get(paraNum);
		}

		return para;
	}

	/**
	 * Loads a head xml file using the HeadHandler class.
	 * 
	 * @param fileName
	 */
	private void loadFromFile(String fileName){
		try {
			//System.out.println("Loading from: " + fileName);
			
			//System.setProperty("org.xml.sax.driver","org.apache.crimson.parser.XMLReaderImpl");
			
			XMLReader xr = XMLReaderFactory.createXMLReader();
			HeadHandler handler = new HeadHandler(this);
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);
			
			xr.parse(fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * We assume here that a pos tag is sent to this function when there has
	 * been a change to the pos of a paragraph in the Thesaurus
	 * 
	 * @param pos
	 */
	public void setPOSStart(String pos) {
		if (pos.equals("N.")) {
			//nStart = paraCount;
			setNStart(paraCount);
		} else if (pos.equals("ADJ.")) {
			//adjStart = paraCount;
			setAdjStart(paraCount);
		} else if (pos.equals("VB.")) {
			//vbStart = paraCount;
			setVbStart(paraCount);
		} else if (pos.equals("ADV.")) {
			//advStart = paraCount;
			setAdvStart(paraCount);
		} else if (pos.equals("INT.")) {
			//intStart = paraCount;
			setIntStart(paraCount);
		} else if (pos.equals("PHR.")) {
			//phrStart = paraCount;
			setPhrStart(paraCount);
		} else if (pos.equals("PREF.")) {
			//prefStart = paraCount;
			setPrefStart(paraCount);
		} else if (pos.equals("PRON.")) {
			//pronStart = paraCount;
			setPronStart(paraCount);
		}
	}

	/**
	 * Converts to a string representation the <TT>Head</TT> object. The
	 * following following format is used - <TT>Head:headNum:headName:classNum:sectionNum:paraCount:sgCount:wordCount</TT>.
	 */
	public String toString() {
		StringBuffer sbInfo = new StringBuffer();

		sbInfo.append("Head:");
		sbInfo.append(getHeadNum());
		sbInfo.append(":");
		sbInfo.append(getHeadName());
		sbInfo.append(":");
		sbInfo.append(getClassNum());
		sbInfo.append(":");
		sbInfo.append(getSectionNum());
		sbInfo.append(":");
		sbInfo.append(getParaCount());
		sbInfo.append(":");
		sbInfo.append(getSGCount());
		sbInfo.append(":");
		sbInfo.append(getWordCount());
		sbInfo.append(":");

		return sbInfo.toString();
	}

	/**
	 * Prints the contents of this Head to the standard output.
	 */
	public void print() {
		System.out.println("Class: " + classNum);
		System.out.println("Section: " + sectionNum);
		System.out.println("Head: " + headNum + " " + headName);
		printParaList(nParaList, nStart);
		printParaList(adjParaList, adjStart);
		printParaList(vbParaList, vbStart);
		printParaList(advParaList, advStart);
		printParaList(intParaList, intStart);
		printParaList(phrParaList, phrStart);
		printParaList(prefParaList, prefStart);
		printParaList(pronParaList, pronStart);
	}

	/**
	 * Prints a list of paragraphs.
	 * 
	 * @param paraList
	 * @param offset
	 */
	private void printParaList(ArrayList<Paragraph> paraList, int offset) {
		Iterator<Paragraph> iter = paraList.iterator();
		int index;
		while (iter.hasNext()) {
			System.out.println();
			Paragraph para = iter.next();
			index = para.getParaNum() + offset;
			System.out.println(index + ". " + para.getPOS());
			para.print();
		}
	}

	/**
	 * Prints all the words and phrases of this Head separated on a separate
	 * line to the standard output.
	 */
	public void printAllWords() {
		Paragraph para;

		ArrayList<Paragraph> allPara = new ArrayList<Paragraph>();

		allPara.addAll(nParaList);
		allPara.addAll(adjParaList);
		allPara.addAll(vbParaList);
		allPara.addAll(advParaList);
		allPara.addAll(intParaList);
		allPara.addAll(phrParaList);
		allPara.addAll(prefParaList);
		allPara.addAll(pronParaList);

		Iterator<Paragraph> pIter = allPara.iterator();
		while (pIter.hasNext()) {
			para = pIter.next();
			para.printAllWords();
		}
	}

	/**
	 * Prints all the semicolon groups of this Head separated on a separate line
	 * to the standard output.
	 */
	public void printAllSG() {
		Paragraph para;

		ArrayList<Paragraph> allPara = new ArrayList<Paragraph>();

		allPara.addAll(nParaList);
		allPara.addAll(adjParaList);
		allPara.addAll(vbParaList);
		allPara.addAll(advParaList);
		allPara.addAll(intParaList);
		allPara.addAll(phrParaList);
		allPara.addAll(prefParaList);
		allPara.addAll(pronParaList);

		Iterator<Paragraph> pIter = allPara.iterator();
		while (pIter.hasNext()) {
			para = pIter.next();
			para.printAllSG();
		}
	}

}
