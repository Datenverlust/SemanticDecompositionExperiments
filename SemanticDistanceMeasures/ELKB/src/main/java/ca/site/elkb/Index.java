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

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * Represents the computer index of the words and phrases of <i>Roget's
 * Thesaurus</i>. According to Kirkpatrick (1998) "The index consists of a list
 * of items, each of which is followed by one or more references to the text.
 * These references consist of a Head number, a <i>keyword</i> in italics, and
 * a part of speech label (n. for nouns, adj. for adjectives, vb. for verbs,
 * adv. for adverbs, and int. for interjections). The <i>keyword</i> is given
 * to identify the paragraph which contains the word you have looked up; it also
 * gives and indication of the ideas contained in that paragraph, so it can be
 * used as a clue where a word has several meanings and therefire several
 * references." An example of an Index Entry is:
 * <ul>
 * <b>stork</b>
 * <ul>
 * <i>obstetrics</i> 167 n.<br>
 * <i>bird</i> 365 n.
 * </ul>
 * </ul>
 * In this example <b>stork</b> is an Index Item and <i>obstetrics</i> 167 n.
 * is a Reference. This <TT>Index</TT> object consists of a hashtable of Index
 * Entries, hashed on the String value of the Index Item. For every key (Index
 * Item) the value is a list of Reference objects. The hashtable is implemented
 * using a HashMap.
 * 
 * @serial itemCount;
 * @serial refCount;
 * @serial itemsMap;
 * @serial refList;
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 */

public class Index implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2147865523115930468L;

	// Attributes
	private int itemCount;

	private int refCount;

	private HashMap<String, String> itemsMap;

	private ArrayList<String> refList;

	private Variant rtVar = new Variant();

	private Morphy rtMorph = new Morphy();

	private boolean BREAK_PHRASES;

	/**
	 * Default constructor.
	 */
	public Index() {
		itemCount = 0;
		refCount = 0;
		itemsMap = new HashMap<String, String>();
		refList = new ArrayList<String>();
	}
	
	/**
	 * This converts the number, passed in string format to
	 * the correct POS
	 * 
	 * @param number
	 * @return POS
	 */
	public String convertToPOS(String number){
		if(number.equals("1")){
			return "N.";
		}
		if(number.equals("2")){
			return "VB.";
		}
		if(number.equals("3")){
			return  "ADJ.";
		}
		if(number.equals("4")){
			return  "ADV.";
		}
		if(number.equals("5")){
			return  "INT.";
		}
		if(number.equals("6")){
			return  "PHR.";
		}
		if(number.equals("7")){
			return  "PREF.";
		}
		if(number.equals("8")){
			return  "PRON.";
		}
		return "";
	}
	
	/**
	 * This converts the number, passed in string format to
	 * the correct POS
	 * 
	 * @param number
	 * @return POS
	 */
	public String convertToPOS(int number){
		if(number == 1){
			return "N.";
		}
		if(number == 2){
			return "VB.";
		}
		if(number == 3){
			return  "ADJ.";
		}
		if(number == 4){
			return  "ADV.";
		}
		if(number == 5){
			return  "INT.";
		}
		if(number == 6){
			return  "PHR.";
		}
		if(number == 7){
			return  "PREF.";
		}
		if(number == 8){
			return  "PRON.";
		}
		return "";
	}
	

	/**
	 * Returns the number corresponding to a given pos, or -1
	 * if an incorrect posis passed as an argument.
	 * 
	 * @param pos
	 * @return POS as integer
	 */
	public int convertToPOSNumber(String pos){
		if(pos.equals("N.")){
			return 1;
		}
		if(pos.equals("VB.")){
			return 2;
		}
		if(pos.equals("ADJ.")){
			return 3;
		}
		if(pos.equals("ADV.")){
			return 4;
		}
		if(pos.equals("INT.")){
			return 5;
		}
		if(pos.equals("PHR.")){
			return 6;
		}
		if(pos.equals("PREF.")){
			return 7;
		}
		if(pos.equals("PRON.")){
			return 8;
		}
		return -1;
	}

	/**
	 * Constructor that builds the <TT>Index</TT> object using the information
	 * contained in a file. The default file for the <i>ELKB</i> is <TT>elkbIndex.dat</TT>
	 * contained in the <TT>$HOME/roget_elkb</TT> directory.
	 * 
	 * @param filename
	 */
	public Index(String filename) {
		this();
		loadFromFile(filename);
	}

	/**
	 * Constructor that builds the <TT>Index</TT> object using the information
	 * contained in a file and sets the initial size of the index hashtable. The
	 * default file for the <i>ELKB</i> is <TT>elkbIndex.dat</TT> contained
	 * in the <TT>$HOME/roget_elkb</TT> directory.
	 * 
	 * @param fileName
	 * @param size
	 */
	public Index(String fileName, int size, boolean breakPhrases) {
		BREAK_PHRASES = breakPhrases;
		itemCount = 0;
		refCount = 0;
		itemsMap = new HashMap<String, String>(size);
		refList = new ArrayList<String>();
		// Should maybe also give option of specifying size of list...
		loadFromFile(fileName);
	}

	/**
	 * Parses an xml index file using the IndexHandler class.
	 * 
	 * @param fileName
	 */
	private void loadFromFile(String fileName) {
		try {
			System.out.println("Loading from: " + fileName);
			
			System.setProperty("org.xml.sax.driver","org.apache.crimson.parser.XMLReaderImpl");
			
			XMLReader xr = XMLReaderFactory.createXMLReader();
			IndexHandler handler = new IndexHandler(this);
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);
			
			xr.parse(fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * addEntry Associates an index entry with its references The references are
	 * stored as a long string of pointers separated by colons, ex:
	 * 1234:7632:8732:
	 * 
	 * If a phrase contains exactly two words, then it both of the words of the
	 * phrase are indexed ex: running track, running, track
	 * 
	 * @param item
	 * @param refs
	 */
	public void addEntry(String item, String refs) {
		addToEntries(item, refs);

		//code to deal with breaking up phrases.
		if(BREAK_PHRASES){
			StringTokenizer st = new StringTokenizer(item, " ");
	
			if(st.countTokens() >= 2){// && st.countTokens() <= 5){
				while(st.hasMoreTokens()){
					String entry1 = st.nextToken();
					addToEntries(entry1, refs);
				}
			}
		}
	}

	/**
	 * addToEntries very similar to addEntry except that only deals with single
	 * words and it is possible that the entry may already exist in the itemsMap
	 * 
	 * @param entry
	 * @param refs
	 */
	private void addToEntries(String entry, String refs) {
		if (itemsMap.containsKey(entry) == false) {
			itemsMap.put(entry, refs);
			itemCount++;
			// else the entry exists, a little more tricky
		} else {
			String entryRefs = itemsMap.get(entry);
			entryRefs = combineReferences(entryRefs, refs);
			itemsMap.put(entry, entryRefs);
		}
	}

	/**
	 * combineReferences combines two list of entries represented as Strings
	 * separated by colons into a single and unique list
	 * 
	 * @param list1
	 * @param list2
	 * @return combined lists
	 */
	private String combineReferences(String list1, String list2){
		return list1+list2;
	}

	/**
	 * Returns the number of entries in this index.
	 * 
	 * @return number of items
	 */
	public int getItemCount() {
		return itemCount;
	}

	/**
	 * Returns the number of references in this index.
	 * 
	 * @return number of references
	 */
	public int getRefCount() {
		return refCount;
	}

	/**
	 * Returns the number of unique references in this index.
	 * 
	 * @return size of reference list
	 */
	public int getUniqRefCount() {
		return refList.size();
	}

	/**
	 * Returns the number of items contained in the hash map of this index.
	 * 
	 * @return size of item map
	 */
	public int getItemsMapSize() {
		return itemsMap.size();
	}

	/**
	 * Returns <TT>true</TT> if the specified entry is contained in this
	 * index.
	 * 
	 * @param key
	 * @return true if entry found, false otherwise
	 */
	public boolean containsEntry(String key) {
		return itemsMap.containsKey(key);
	}

	/**
	 * Prints the index entry along with its references to the standard output.
	 * 
	 * @param key
	 */
	public void printEntry(String key) {
		printEntry(key, -1);
	}

	/**
	 * Prints the index entry along with its numbered references to the standard
	 * output. The number of the first reference must be specified. The number
	 * is printed in front of each reference.
	 * 
	 * @param key
	 * @param itemNo
	 */
	public void printEntry(String key, int itemNo) {
		TreeSet<String> ptrList = getEntry(key);
		if (ptrList.isEmpty()) {
			System.out.println("** " + key + " is not in the Index **");
		} else {
			System.out.println("** " + key + " **");
			// 1. Iterate through the set
			Iterator<String> iter = ptrList.iterator();
			while (iter.hasNext()) {
				// 2. Retrieve reference & print
				String strRef = getStrRef(iter.next());
				if (itemNo >= 0) {
					System.out.println(itemNo + ". " + strRef);
					itemNo++;
				} else {
					System.out.println(strRef);
				}
			}
		}
	}

	/**
	 * Returns the list of references for a given word or phrase in the index.
	 * 
	 * @param key
	 * @return Arraylist of entries
	 */
	public ArrayList<String> getEntryList(String key) {
		return getEntryList(key, -1);
	}

	/**
	 * Returns the list of references for a given word or phrase in the index
	 * preceded by a number to identify the reference. References are in the
	 * form of a paragraph and head number as identifiers.
	 * 
	 * @param key
	 * @param itemNo
	 * @return ArrayList of entries
	 */
	public ArrayList<String> getEntryList(String key, int itemNo) {
		ArrayList<String> entryList = new ArrayList<String>();
		TreeSet<String> ptrList = getEntry(key);

		// If we actually found something
		if (ptrList.isEmpty() == false) {
			// 1. Iterate through set
			Iterator<String> iter = ptrList.iterator();
			while (iter.hasNext()) {
				// 2. Retrieve reference & print
				String strRef = getStrRef(iter.next());
				if (itemNo >= 0) {
					entryList.add(itemNo + ". " + strRef);
					itemNo++;
				} else {
					entryList.add(strRef);
				}
			}
		}
		return entryList;
	}
	
	/**
	 * Returns the list of references for a given word or phrase in the index
	 * preceded by a number to identify the reference. References are in the
	 * form of an array of integers.
	 * 
	 * @param key
	 * @return ArrayList of entries in numerical form
	 */
	public ArrayList<int[]> getEntryListNumerical(String key) {
		return getEntryListNumerical(key, true);
	}

	/**
	 * Returns the list of references for a given word or phrase in the index
	 * preceded by a number to identify the reference. References are in the
	 * form of an array of integers. The boolean morphology is true if morphological
	 * variations on the word should be searched for, and false otherwise.
	 * 
	 * @param key
	 * @aram morphology
	 * @return ArrayList of entries in numerical form
	 */
	public ArrayList<int[]> getEntryListNumerical(String key, boolean morphology) {
		ArrayList<int[]> entryList = new ArrayList<int[]>();
		TreeSet<String> ptrList = getEntry(key, morphology);

		// If we actually found something
		if (ptrList.isEmpty() == false) {
			// 1. Iterate through set
			Iterator<String> iter = ptrList.iterator();
			while (iter.hasNext()) {
				// 2. Retrieve reference & print
				int[] strRef = getStrRefNumerical(iter.next());
				//int[] strRefInt = formatNew(strRef);
				entryList.add(strRef);
			}
		}
		return entryList;
	}
	
	/**
	 * Returns the list of references for a given word or phrase in the index
	 * preceded by a number to identify the reference. References are in the
	 * form of an array of integers. The boolean morphology is true if morphological
	 * variations on the word should be searched for, and false otherwise. Only
	 * returns results of the given POS
	 * 
	 * @param key
	 * @aram morphology
	 * @param POS
	 * @return ArrayList of entries in numerical form
	 */
	public ArrayList<int[]> getEntryListNumerical(String key, boolean morphology, String pos) {
		ArrayList<int[]> entryList = new ArrayList<int[]>();
		TreeSet<String> ptrList = getEntry(key, morphology);

		// If we actually found something
		if (ptrList.isEmpty() == false) {
			// 1. Iterate through set
			Iterator<String> iter = ptrList.iterator();
			while (iter.hasNext()) {
				// 2. Retrieve reference & print
				int[] strRef = getStrRefNumerical(iter.next());
				//int[] strRefInt = formatNew(strRef);
				if(convertToPOSNumber(pos) == strRef[5]){
					entryList.add(strRef);
				}
			}
		}
		return entryList;
	}



	/**
	 * addReference
	 * 
	 * @param strPtr
	 * @param sRef
	 * @return string of indexes
	 */
	public String addReference(String strPtr, String sRef) {
		// if object is not found
		if (!refList.contains(sRef)) {
			refList.add(sRef);
		}
		strPtr += refList.indexOf(sRef) + ":";
		refCount++;
		return strPtr;
	}

	/**
	 * Returns references to a word or phrase, but by default applies
	 * morphology rules.
	 * 
	 * @param key
	 * @return TreeSet of related words
	 */
	public TreeSet<String> getEntry(String key) {
		return getEntry(key, true);
	}
	/**
	 * Returns all references for a given word or phrase in the index. This is
	 * where the American to British spelling changes should be done, as well as
	 * the other tricks to access phrases. There are a few things to note: 1.
	 * Multiple spellings have been included in Roget's, for example tire and
	 * tyre. The meanings can be different for each spelling... 2. Often the
	 * space between phrases has been removed How come this method does not
	 * return null???
	 * 
	 * Returns all of the cross references for a given word or phrase
	 * 
	 * If the second argument is true then this method should return the first
	 * of the following words that is found: + the supplied word + the biritsh
	 * spelling of the word + the base form of the word (Morphy) A future
	 * version should return all entries found in the index.
	 * 
	 * @param key
	 * @param morphology
	 * @return TreeSet of entries
	 */
	public TreeSet<String> getEntry(String key, boolean morphology) {
		// 1. build list
		// 2. loop to find untill an entry is found

		TreeSet<String> wordList = new TreeSet<String>();
		TreeSet<String> ptrSet = new TreeSet<String>();
		String entry = new String();

		wordList.add(key);
		if(morphology){
			try {
				wordList.add(rtVar.amToBr(key));
			} catch (NullPointerException npe) {
				// continue execution
			} catch (Exception e) {
				System.out.println("Error: " + e);
			}
	
			try {
				wordList.addAll(rtMorph.getBaseForm(key));
			} catch (NullPointerException npe) {
				// continue execution
			} catch (Exception e) {
				System.out.println("Error: " + e);
			}
		}
		
		Iterator<String> iter = wordList.iterator();

		while (iter.hasNext()) {
			entry = iter.next();
			if (itemsMap.containsKey(entry)) {
				String ptrList = itemsMap.get(entry);

				StringTokenizer st = new StringTokenizer(ptrList, ":\n");
				while (st.hasMoreTokens()) {

					try {
						ptrSet.add(st.nextToken());
					} catch (NullPointerException npe) {
						// continue execution
					} catch (Exception e) {
						System.out.println("Error: " + e);
					} // end try - catch
				}
			}
		}

		return ptrSet;
	}

	/**
	 * Returns a string containing the part-of-speech of the references for a
	 * given index entry. For example, getRefPOS("respect") will return
	 * "N.VB.ADV."
	 * 
	 * @param key
	 * @return reference POS
	 */
	public String getRefPOS(String key) {
		StringBuffer sbRefPOS = new StringBuffer();
		String strRef;

		boolean bN = false;
		boolean bADJ = false;
		boolean bVB = false;
		boolean bADV = false;
		boolean bINT = false;
		boolean bPHR = false;
		boolean bPREF = false;
		boolean bPRON = false;

		// get all references
		TreeSet<String> ptrSet = getEntry(key);
		Iterator<String> iter = ptrSet.iterator();

		// identify POS
		while (iter.hasNext()) {
			strRef = getStrRef(iter.next());
			if (strRef.endsWith("N."))
				bN = true;
			if (strRef.endsWith("ADJ."))
				bADJ = true;
			if (strRef.endsWith("VB."))
				bVB = true;
			if (strRef.endsWith("ADV."))
				bADV = true;
			if (strRef.endsWith("INT."))
				bINT = true;
			if (strRef.endsWith("PHR."))
				bPHR = true;
			if (strRef.endsWith("PREF."))
				bPREF = true;
			if (strRef.endsWith("PRON."))
				bPRON = true;
		}

		if (bN)
			sbRefPOS.append("N.");
		if (bADJ)
			sbRefPOS.append("ADJ.");
		if (bVB)
			sbRefPOS.append("VB.");
		if (bADV)
			sbRefPOS.append("ADV.");
		if (bINT)
			sbRefPOS.append("INT.");
		if (bPHR)
			sbRefPOS.append("PHR.");
		if (bPREF)
			sbRefPOS.append("PREF.");
		if (bPRON)
			sbRefPOS.append("PRON.");
		if (!bN && !bADJ && !bVB && !bADV && !bINT && !bPHR && !bPREF && !bPRON)
			sbRefPOS.append("NULL.");

		return sbRefPOS.toString();
	}

	/**
	 * Returns a list of references in string format instead of pointers. For
	 * example box 194 N. instead of 778
	 * 
	 * @param key
	 * @return List of references
	 */
	public ArrayList<String> getStrRefList(String key) {

		ArrayList<String> entryList = new ArrayList<String>();
		TreeSet<String> ptrSet = getEntry(key);

		Iterator<String> iter = ptrSet.iterator();
		while (iter.hasNext()) {
			entryList.add(getStrRef(iter.next()));
		}

		return entryList;
	}

	/**
	 * Returns a reference in String format as printed in <i> Roget's Thesaurus</i>.
	 * For example: way 624 N.
	 * 
	 * @param strIndex
	 * @return reference
	 */
	public String getStrRef(String strIndex) {
		String[] parts = strIndex.split(",");
		Integer index = new Integer(parts[0]);
		String strRef = new String(refList.get(index.intValue()) + "," + parts[1] + "," + parts[2]);
		String[] parts2 = strRef.split(",");
		String toReturn = parts2[0] + " " + parts2[5] + " " + convertToPOS(parts2[6]);
		return toReturn;
	}
	
	/**
	 * Returns a reference in new String format as printed in <i> Roget's Thesaurus</i>.
	 * For example: 1 1 2 3 123 3 2 1 5.
	 * 
	 * @param strIndex
	 * @return reference in an integer array
	 */
	public int[] getStrRefNumerical(String strIndex) {
		String[] parts = strIndex.split(",");
		Integer index = new Integer(parts[0]);
		//Integer index = new Integer(strIndex);
		String strRef = new String(refList.get(index.intValue()) + "," + parts[1] + "," + parts[2]);
		
		String[] parts2 = strRef.split(",");
		int[] toReturn = {Integer.parseInt(parts2[1]), 
					Integer.parseInt(parts2[2]), 
					Integer.parseInt(parts2[3]), 
					Integer.parseInt(parts2[4]), 
					Integer.parseInt(parts2[5]), 
					Integer.parseInt(parts2[6]), 
					Integer.parseInt(parts2[7]), 
					Integer.parseInt(parts2[8]), 
					Integer.parseInt(parts2[9]) 
				};
		return toReturn;
	}

	/**
	 * Returns an array of <TT>Reference</TT> objects.
	 * 
	 * @param key
	 * @return ArrayList of references
	 */
	public ArrayList<Reference> getRefObjList(String key) {

		ArrayList<Reference> refObjList = new ArrayList<Reference>();
		TreeSet<String> ptrList = getEntry(key);

		Iterator<String> iter = ptrList.iterator();
		while (iter.hasNext()) {
			Reference ref = new Reference(getStrRef(iter.next()));
			ref.setIndexEntry(key);
			refObjList.add(ref);
		}

		return refObjList;
	}

	/**
	 * Returns a set of head numbers in which a word or phrase can be found.
	 * Heads are stored as Strings.
	 * 
	 * @param key
	 * @return TreeSet of head numbers
	 */
	public TreeSet<String> getHeadNumbers(String key) {
		TreeSet<String> refHeadNo = new TreeSet<String>();
		TreeSet<String> ptrList = getEntry(key);

		Iterator<String> iter = ptrList.iterator();
		while (iter.hasNext()) {
			Reference ref = new Reference(getStrRef(iter.next()));
			refHeadNo.add(String.valueOf(ref.getHeadNum()));
		}

		return refHeadNo;
	}

	/**
	 * Adds a reference.
	 * 
	 * @param strPtr
	 * @param sRef
	 * @param sgNum
	 * @param wordNum
	 * @return string of indexes
	 */
	public String addReference(String strPtr, String sRef, String sgNum, String wordNum) {
		//		 if object is not found
		if (!refList.contains(sRef)) {
			refList.add(sRef);
		}
		strPtr += refList.indexOf(sRef) + "," + sgNum + "," + wordNum + ":";
		refCount++;
		return strPtr;
	}

}

