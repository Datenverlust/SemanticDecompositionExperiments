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
 * Main class of the <i>Roget's Thesaurus Electronic Lexical KnowledgeBase</i>.
 * It is made up of three major components:
 * <UL>
 * <LI>the Index</LI>
 * <LI>the Tabular Synopsis of Categories</LI>
 * <LI>the Text</LI>
 * </UL>
 * 
 * Required files:
 * <UL>
 * <LI><TT>elkbIndex.dat</TT>: The Index in binary file format.</LI>
 * <LI><TT>rogetMap.rt</TT>: The <i>Tabular Synopsis of Categories</i>.</LI>
 * <LI><TT>./heads/head*</TT>: The 990 heads</LI>
 * <LI><TT>AmBr.lst</TT>: The American to British spelling word list.</LI>
 * <LI><TT>noun.exc, adj.exc, verb.exc, adv.exc</TT>: exception lists used
 * for the morphological transformations.</LI>
 * </UL>
 * These files are found in the <TT>$HOME/roget_elkb</TT> directory.
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 */

public class RogetELKB {

	/***************************************************************************
	 * Location of user's <TT>Home</TT> directory.
	 **************************************************************************/

	final String path = this.getClass().getResource("../../").getPath();
	//final String path = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Roget" + File.separator;
	/***************************************************************************
	 * Location of the <i>ELKB</i> de.kimanufaktur.nsm.semanticDistanceMeasures.data directory.
	 **************************************************************************/
	public  final String ELKB_PATH = System.getProperty("elkb.path",
			path.substring(0,path.length()-3) + "roget_elkb");

	/***************************************************************************
	 * Location of the <i>ELKB</i> Index.
	 **************************************************************************/
	public static final String INDEX = "/elkbIndex.dat";

	/***************************************************************************
	 * Location of the <i>ELKB Tabular Synopsis of Categories</i>.
	 **************************************************************************/
	public static final String CATEG = "/rogetMap.xml";

	/***************************************************************************
	 * Location of the Heads.
	 **************************************************************************/
	public static final String HEADS = "/heads/head";
	
	/***************************************************************************
	 * Year 1987.
	 **************************************************************************/
	public final String PATH_1987 = ELKB_PATH + "/1987";
	
	/***************************************************************************
	 * Year 1911.
	 **************************************************************************/
	public final String PATH_1911 = ELKB_PATH + "/1911";
	
	/***************************************************************************
	 * Year 1911X.
	 **************************************************************************/
	public final String PATH_1911X = ELKB_PATH + "/1911X";
	
	/***************************************************************************
	 * Year 1987X.
	 **************************************************************************/
	public final String PATH_1987X = ELKB_PATH + "/1987X";
	
	/***************************************************************************
	 * Year 1911X.
	 **************************************************************************/
	public final String PATH_1911R = ELKB_PATH + "/1911R";
	
	/***************************************************************************
	 * Year 1987X.
	 **************************************************************************/
	public final String PATH_1987R = ELKB_PATH + "/1987R";
	
	/***************************************************************************
	 * holds real path.
	 **************************************************************************/
	public static String PATH_ELKB = "";

	/***************************************************************************
	 * The <i>ELKB</i> Index.
	 **************************************************************************/
	public Index index;

	/***************************************************************************
	 * The <i>ELKB Tabular Synopisis of Categories</i>.
	 **************************************************************************/
	public Category category;

	/***************************************************************************
	 * The <i>ELKB</i> Text.
	 **************************************************************************/
	public RogetText text;

	/***************************************************************************
	 * The <i>ELKB</i> Relationships.
	 **************************************************************************/
	private int iHead = 0;

	private String sKey = new String();

	private String sPOS = new String();

	/**
	 * Default constructor.
	 */
	public RogetELKB(){
		this("1911");
	}
	
	public RogetELKB(int year) {
		this(""+year);
	}
	
	/**
	 * Non-default constructor.
	 * 
	 * @param year
	 */
	public RogetELKB(String year) {
		this(year, true);
	}
	
	/**
	 * Constructor allows you to choose between an index with broken phrases and one without.
	 * 
	 * @param year
	 * @param brokenUpPhrases
	 */
	public RogetELKB(String year, boolean brokenUpPhrases) {
		if(year.equals("1987")){
			PATH_ELKB = PATH_1987;
		}
		else if(year.equals("1911")){
			PATH_ELKB = PATH_1911;
		}
		else if(year.equals("1987X1")){
			PATH_ELKB = PATH_1987X+"1";
		}
		else if(year.equals("1911X1")){
			PATH_ELKB = PATH_1911X+"1";
		}
		else if(year.equals("1987X5")){
			PATH_ELKB = PATH_1987X+"5";
		}
		else if(year.equals("1911X5")){
			PATH_ELKB = PATH_1911X+"5";
		}
		else if(year.equals("1987X")){
			PATH_ELKB = PATH_1987X;
		}
		else if(year.equals("1911X")){
			PATH_ELKB = PATH_1911X;
		}
		else if(year.equals("1987R")){
			PATH_ELKB = PATH_1987R;
		}
		else if(year.equals("1911R")){
			PATH_ELKB = PATH_1911R;
		}

		// 1. load the index
		System.err.println("Loading the Index...");
		try {
			String fileName = "/index/elkbIndex_noBreak.dat";
			if(brokenUpPhrases){
				fileName = "/index/elkbIndex_allBreak.dat";
			}
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(PATH_ELKB + fileName));
			//System.err.println(PATH_ELKB + fileName);

			index = (Index) in.readObject();
		} catch (Exception e) {
			System.err.print("Error: " + e);
			System.exit(1);
		}

		// 2. load the Text
		System.err.println("Loading the Text...");
		text = new RogetText(PATH_ELKB + HEADS);
		

		// 3. load the categories
		// categories can't be loaded yet.
		System.err.println("Loading the Categories...");
		category = new Category(PATH_ELKB + CATEG);
	}

	/**
	 * Allows the <i>ELKB</i> to be used via the command line.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		RogetELKB elkb = new RogetELKB("1911");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int i = 0;
		String choice;
		try {
			while (i != 3) {
				printMenu();
				choice = br.readLine();
				choice = choice.trim();
				i = Integer.parseInt(choice);

				switch (i) {
				case 1:
					elkb.lookUpWordInIndex();
					break;
				case 2:
					elkb.lookUpPairInIndex();
					break;
				case 3:
					System.out.println("\nGoodbye");
					break;
				default:
					System.out.println("\n#" + i + " is not a valid option");
				}
			}
		} catch (IOException ioe) {
			System.out.println("IO error:" + ioe);
		}
	}

	/***************************************************************************
	 * printMenu()
	 **************************************************************************/
	private static void printMenu() {
		System.out.println("\n         ROGET'S THESAURUS ELKB\n");
		System.out.println("1.  Look up a word or phrase");
		System.out.println("2.  Look up a pair of words or phrases");
		System.out.println("3.  Quit\n");
	}

	/**
	 * Prompts the user for input to either see the next input in series, or
	 * quit to stop seeing the series.
	 * 
	 * @return
	 */
	private String pause() {
		String sMsg = new String();
		System.out
				.println("\nPress [Enter] to continue or type 'quit' to terminate\n");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			sMsg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error:" + ioe);
		}

		return sMsg;
	}

	/**
	 * lookUpPairInIndex - looks up to words or phrases and calculates all the
	 * possible distances between the references.
	 */
	private void lookUpPairInIndex() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String word1 = new String();
		String word2 = new String();
		String more = new String();

		try {
			do {
				System.out.print("\nEnter a word or phrase: ");
				word1 = br.readLine();

				System.out.print("\nEnter a second word or phrase: ");
				word2 = br.readLine();

				// Obtain all paths
				TreeSet<Path> sortedPath = getAllPaths(word1, word2);

				if (sortedPath.size() != 0) {
					System.out.println("\nPath between " + word1 + " and "
							+ word2 + " (" + sortedPath.size()
							+ " paths in total)");
					System.out.println();

					// Display sorted paths
					Iterator<Path> iter = sortedPath.iterator();
					while (iter.hasNext()) {
						System.out.println(iter.next());
						System.out.println();
						String sMsg = pause();
						if (sMsg.equals("quit")) {
							break;
						}
					}
				} else {
					// If no paths are found display error message
					System.out.println();
					System.out.println(notInIndex(word1, word2));
				}

				System.out.print("\nCalculate more paths [y/n] ? ");
				more = br.readLine();
			} while (!(more.equals("n")));

		} catch (IOException ioe) {
			System.out.println("IO error:" + ioe);
		}
	}

	/**
	 * Takes an identifier in the form of "1.2.5.3.2" where numbers represent the class,
	 * section, subsection, head group, head, POS, paragraph, semicolon group, word.
	 * The results are for any grouping, it need not contain all 9 numbers.  If an
	 * entry is invalid it will return null;
	 * @param identifier
	 * @return ArrayList of Strings
	 */
	public ArrayList<String> getGrouping(String identifier){
		String[] sNumbers = identifier.split("\\.");
		int[] numbers = new int[sNumbers.length];
		for(int i = 0; i < sNumbers.length; i++){
			try{
				numbers[i] = Integer.parseInt(sNumbers[i]);
			}
			catch(Exception e){
				System.err.println("Parsing problem for identifier: " + identifier);
				return null;
			}
		}
			
		if(numbers.length == 9){
			return getWord(numbers[4], numbers[5], numbers[6], numbers[7], numbers[8]);
		}
		else if(numbers.length == 8){
			return getSG(numbers[4], numbers[5], numbers[6], numbers[7]);
		}
		else if(numbers.length == 7){
			return getPara(numbers[4], numbers[5], numbers[6]);
		}
		else if(numbers.length == 6){
			return getPOS(numbers[4], numbers[5]);
		}
		else if(numbers.length == 5){
			return getHead(numbers[4]);
		}
		else if(numbers.length == 4){
			return null; // not implemented yet
		}
		else if(numbers.length == 3){
			return null; // not implemented yet
		}
		else if(numbers.length == 2){
			return null; // not implemented yet
		}
		else if(numbers.length == 1){
			return null; // not implemented yet
		}
		
		return null;
	}
	

	private ArrayList<String> getWord(int head, int POS, int para, int sg, int wordNum){
		Head h = text.getHead(head);
		String partOfSpeech = index.convertToPOS(POS);
		Paragraph p = h.getPara(para, partOfSpeech);
		SG semicolonGroup = p.getSG(sg);
		ArrayList<String> words =semicolonGroup.getWordList();
		String wrd = words.get(wordNum);
		ArrayList<String> toReturn = new ArrayList<String>();
		toReturn.add(wrd);
		return toReturn;
	}
	
	public ArrayList<String> getSG(int head, int POS, int para, int sg){
		Head h = text.getHead(head);
		String partOfSpeech = index.convertToPOS(POS);
		Paragraph p = h.getPara(para-1, partOfSpeech);
		SG semicolonGroup = p.getSG(sg-1);
		return semicolonGroup.getWordList();
	}
	
	public ArrayList<String> getPara(int head, int POS, int para){
		Head h = text.getHead(head);
		String partOfSpeech = index.convertToPOS(POS);
		Paragraph p = h.getPara(para-1, partOfSpeech);
		return p.getAllWordList();
	}
	
	public ArrayList<String> getPOS(int head, int POS){
		ArrayList<String> toReturn = new ArrayList<String>();
		Head h = text.getHead(head);
		String partOfSpeech = index.convertToPOS(POS);
		int start = 0;
		if(POS == 1){
			start = h.getNStart();
		}
		else if(POS == 2){
			start = h.getVbStart();
		}
		else if(POS == 3){
			start = h.getAdjStart();
		}
		else if(POS == 4){
			start = h.getAdvStart();
		}
		//System.out.println(start);
		for(int i = start; i < start + h.getParaCount(partOfSpeech); i++){
			Paragraph p = h.getPara(i-start, partOfSpeech);
			toReturn.addAll(p.getAllWordList());
		}
		return toReturn;
	}
	
	public ArrayList<String> getHead(int head){
		ArrayList<String> toReturn = new ArrayList<String>();
		Head h = text.getHead(head);
		for(int i = 1; i < h.getNParaCount(); i++){
			Paragraph p = h.getPara(i, "N.");
			toReturn.addAll(p.getAllWordList());
		}
		for(int i = 1; i < h.getVbParaCount(); i++){
			Paragraph p = h.getPara(i, "VB.");
			toReturn.addAll(p.getAllWordList());
		}
		for(int i = 1; i < h.getAdjParaCount(); i++){
			Paragraph p = h.getPara(i, "ADJ.");
			toReturn.addAll(p.getAllWordList());
		}
		for(int i = 1; i < h.getAdvParaCount(); i++){
			Paragraph p = h.getPara(i, "ADV.");
			toReturn.addAll(p.getAllWordList());
		}
		return toReturn;
	}
	
	/**
	 * Returns all the paths between two words or phrases. The paths are sorted
	 * from the smallest to the biggest distance. If set of size 0 represents
	 * that an error occurred when determining the paths.
	 * 
	 * @param strWord1
	 * @param strWord2
	 * @return TreeSet of paths
	 */
	public TreeSet<Path> getAllPaths(String strWord1, String strWord2) {
		// obtain the references

		ArrayList<String> refList1 = index.getStrRefList(strWord1);
		ArrayList<String> refList2 = index.getStrRefList(strWord2);
		
		TreeSet<Path> sortedPath = new TreeSet<Path>();

		// make sure we obtain some result!
		if ((refList1.size() != 0 || refList2.size() != 0) == true) {

			int iCount1 = refList1.size();
			int iCount2 = refList2.size();

			for (int i = 0; i < iCount1; i++) {
				for (int j = 0; j < iCount2; j++) {
					// System.out.println("Calculating the path");
					Path rtPath = path(strWord1, refList1.get(i),
							strWord2, refList2.get(j));
					sortedPath.add(rtPath);
				}
			}
		}
		return sortedPath;
	}

	/**
	 * Returns all the paths between two words or phrases of a given
	 * part-of-speech. The part-of-speech can be any of N., VB., ADJ., ADV. The
	 * paths are sorted from the smallest to the biggest distance.
	 * 
	 * @param strWord1
	 * @param strWord2
	 * @param POS
	 * @return TreeSet of paths
	 */
	public TreeSet<Path> getAllPaths(String strWord1, String strWord2, String POS) {
		// obtain the references

		ArrayList<String> refList1 = index.getStrRefList(strWord1);
		// System.out.println(refList1);
		ArrayList<String> refList2 = index.getStrRefList(strWord2);
		// System.out.println(refList2);

		TreeSet<Path> sortedPath = new TreeSet<Path>();

		// make sure we obtain some result!
		if ((refList1.size() != 0 || refList2.size() != 0) == true) {

			int iCount1 = refList1.size();
			int iCount2 = refList2.size();

			for (int i = 0; i < iCount1; i++) {
				for (int j = 0; j < iCount2; j++) {
					// System.out.println("Calculating the path");
					Reference ref1 = new Reference(refList1.get(i));
					Reference ref2 = new Reference(refList2.get(j));

					if (ref1.getPos().equals(POS) && ref2.getPos().equals(POS)) {

						Path rtPath = path(strWord1, refList1.get(i),
								strWord2, refList2.get(j));
						sortedPath.add(rtPath);
					}
				}
			}
		}
		return sortedPath;
	}

	/**
	 * Used to help compute distances for the analogy problem.
	 * 
	 * Returns all the paths between two words or phrases of a given
	 * part-of-speech. The part-of-speech can be any of N., VB., ADJ., ADV.
	 * 
	 * The two words need not have the same part of speech!!!
	 * 
	 * The paths are sorted from the smallest to the biggest distance.
	 * 
	 * @param strWord1
	 * @param POS1
	 * @param strWord2
	 * @param POS2
	 * @return TreeSet of paths
	 */
	public TreeSet<Path> getAllPaths(String strWord1, String POS1, String strWord2,
			String POS2) {
		// obtain the references

		ArrayList<String> refList1 = index.getStrRefList(strWord1);
		ArrayList<String> refList2 = index.getStrRefList(strWord2);

		TreeSet<Path> sortedPath = new TreeSet<Path>();

		// make sure we obtain some result!
		if ((refList1.size() != 0 || refList2.size() != 0) == true) {

			int iCount1 = refList1.size();
			int iCount2 = refList2.size();

			for (int i = 0; i < iCount1; i++) {
				for (int j = 0; j < iCount2; j++) {
					// System.out.println("Calculating the path");
					Reference ref1 = new Reference(refList1.get(i));
					Reference ref2 = new Reference(refList2.get(j));

					if (ref1.getPos().equals(POS1)
							&& ref2.getPos().equals(POS2)) {

						Path rtPath = path(strWord1, refList1.get(i),
								strWord2, refList2.get(j));
						sortedPath.add(rtPath);
					}
				}
			}
		}
		return sortedPath;
	}

	/**
	 * Determines the thesaural relation that exists between a specific sense of
	 * a words or phrases and another word or phrase. There are two kinds of
	 * thesaural relations:
	 * <UL>
	 * <LI>T0: reiteration of the same string.</LI>
	 * <LI>T1: both words or phrases belong to the same head, paragraph and
	 * part-of-speech.</LI>
	 * This method can be used to build lexical chains. The parameters head
	 * number, part-of-speech and reference name as parameter are used to
	 * identify a specific sense of a word or phrase.
	 * 
	 * Now returns T1 if the two terms are in the same Head!
	 * 
	 * @param strWord1
	 * @param iHeadNum1
	 * @param sRefName1
	 * @param sPos1
	 * @param strWord2
	 * @return Relationship head
	 */
	public String t1RelationHeadOnly(String strWord1, int iHeadNum1,
			String sRefName1, String sPos1, String strWord2) {

		String sRelation = null;

		int iHeadNum2;
		
		// obtain the references
		ArrayList<String> refList2 = index.getStrRefList(strWord2);

		// T0 relation
		if (strWord1.equals(strWord2))
			sRelation = "T0";

		// check for thesaural relations, make sure we have words to look at!
		else if (refList2.size() != 0) {
			int iCount2 = refList2.size();

			findRelation: for (int j = 0; j < iCount2; j++) {
				Reference ref2 = new Reference(refList2.get(j));
				iHeadNum2 = ref2.getHeadNum();
				ref2.getRefName();

				// System.out.println(strWord1 + " ** " + sRefName1 + " ** " +
				// sRefName2);

				// T1 relation
				if (iHeadNum1 == iHeadNum2) {
					sRelation = "T1";
					// sRelation = "T1"
					// + "." + String.valueOf(iHeadNum1);
					break findRelation;
				}
			}
		}

		return sRelation;
	}

	/**
	 * Determines the thesaural relation that exists between a specific sense of
	 * a words or phrases and another word or phrase. There are two kinds of
	 * thesaural relations:
	 * <UL>
	 * <LI>T0: reiteration of the same string.</LI>
	 * <LI>T1: both words or phrases belong to the same head, paragraph and
	 * part-of-speech.</LI>
	 * This method can be used to build lexical chains. The parameters head
	 * number, part-of-speech and reference name as parameter are used to
	 * identify a specific sense of a word or phrase.
	 * 
	 * @param strWord1
	 * @param iHeadNum1
	 * @param sRefName1
	 * @param sPos1
	 * @param strWord2
	 * @return Relationship
	 */
	public String t1Relation(String strWord1, int iHeadNum1, String sRefName1,
			String sPos1, String strWord2) {

		String sRelation = null;

		int iHeadNum2;
		String sPos2;
		String sRefName2;

		// obtain the references
		ArrayList<String> refList2 = index.getStrRefList(strWord2);

		// T0 relation
		if (strWord1.equals(strWord2))
			sRelation = "T0";

		// check for thesaural relations, make sure we have words to look at!
		else if (refList2.size() != 0) {
			int iCount2 = refList2.size();

			findRelation: for (int j = 0; j < iCount2; j++) {
				Reference ref2 = new Reference(refList2.get(j));
				iHeadNum2 = ref2.getHeadNum();
				sPos2 = ref2.getPos();
				sRefName2 = ref2.getRefName();

				// System.out.println(strWord1 + " ** " + sRefName1 + " ** " +
				// sRefName2);

				// T1 relation
				if ((iHeadNum1 == iHeadNum2) && (sRefName1.equals(sRefName2))
						&& (sPos1.equals(sPos2))) {
					sRelation = "T1";
					// sRelation = "T1"
					// + "." + String.valueOf(iHeadNum1);
					break findRelation;
				}
			}
		}

		return sRelation;
	}

	/**
	 * Determines the thesaural relation that exists between two words or
	 * phrases. There are two kinds of thesaural relations:
	 * <UL>
	 * <LI>T0: reiteration of the same string.</LI>
	 * <LI>T1: both words or phrases belong to the same head, paragraph and
	 * part-of-speech.</LI>
	 * This method can be used to build lexical chains.
	 * 
	 * @param strWord1
	 * @param strWord2
	 * @return Relationship
	 */
	public String t1Relation(String strWord1, String strWord2) {
		String sRelation = null;
		int iHeadNum1;
		int iHeadNum2;

		// obtain the references
		ArrayList<String> refList1 = index.getStrRefList(strWord1);
		ArrayList<String> refList2 = index.getStrRefList(strWord2);

		// T0 relation
		if (strWord1.equals(strWord2))
			sRelation = "T0";

		// check for thesaural relations, make sure we have words to look at!
		else if ((refList1.size() == 0 || refList2.size() == 0) == false) {
			int iCount1 = refList1.size();
			int iCount2 = refList2.size();

			findRelation: for (int i = 0; i < iCount1; i++) {
				Reference ref1 = new Reference(refList1.get(i));
				// Restrict only to nouns for the time being
				// but lexical chains should be built using
				// all POS
				iHeadNum1 = ref1.getHeadNum();
				for (int j = 0; j < iCount2; j++) {
					Reference ref2 = new Reference(refList2.get(j));
					iHeadNum2 = ref2.getHeadNum();
					// T1 relation
					if ((iHeadNum1 == iHeadNum2)) {
						sRelation = "T1" + "." + String.valueOf(iHeadNum1);
						break findRelation;
					}

				}

			}
		}
		return sRelation;
	}

	/**
	 * Returns a message stating which words or phrases, at most two are not in
	 * index. A bit weird but useful for calculatinf the distance
	 * 
	 * @param strWord1
	 * @param strWord2
	 * @return not in index message
	 */
	private String notInIndex(String strWord1, String strWord2) {
		// obtain the references
		ArrayList<String> refList1 = index.getStrRefList(strWord1);
		ArrayList<String> refList2 = index.getStrRefList(strWord2);

		String notFound = new String();

		if (refList1.size() == 0 && refList2.size() == 0) {
			notFound = strWord1 + " and " + strWord2 + " are not in the Index.";
		} else if (refList1.size() == 0) {
			notFound = strWord1 + " is not in the Index.";
		} else if (refList2.size() == 0) {
			notFound = strWord2 + " is not in the Index.";
		}

		return notFound;
	}

	/**
	 * Calculates the path between two senses of words or phrases. The
	 * references are used to identify the senses. They must be supplied in the
	 * following format:
	 * <UL>
	 * <LI>cad 938 N.</LI>
	 * <LI>cat 365 N.</LI>
	 * </UL>
	 * 
	 * @param strWord1
	 * @param strRef1
	 * @param strWord2
	 * @param strRef2
	 * @return Path between word
	 */
	public Path path(String strWord1, String strRef1, String strWord2,
			String strRef2) {

		ArrayList<String> rtPath = new ArrayList<String>();
		ArrayList<HeadInfo> headList = category.getHeadList();
		ArrayList<RogetClass> classList = category.getClassList();
		Reference ref1 = new Reference(strRef1);
		Reference ref2 = new Reference(strRef2);
		boolean bSameSG = false;

		// one of the methods could fail... should index try-catch block
		try {

			// Obtain all of the path info for the 1st word
			int iHead1 = ref1.getHeadNum();
			String sKeyWord1 = ref1.getRefName();
			String sPos1 = ref1.getPos();

			HeadInfo headInfo1 = new HeadInfo();
			try {
				headInfo1 = headList.get(iHead1 - 1);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("RogetELKB [headList]: " + e);
			}

			String sHeadName1 = iHead1 + ". " + headInfo1.getHeadName();
			int iClass1 = headInfo1.getClassNum();
			int iSection1 = headInfo1.getSectNum();
			String sSubSection1 = headInfo1.getSubSectName();
			ArrayList<String> headGroup1 = headInfo1.getHeadGroup();

			RogetClass rogClass1 = new RogetClass();
			try {
				rogClass1 = classList.get(iClass1 - 1);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("RogetELKB [classList]: " + e);
			}

			String sRogClass1 = rogClass1.getStrClassNum() + ": "
					+ rogClass1.getClassName();
			ArrayList<Section> sectList1 = rogClass1.getSectionList();

			Section section1 = new Section();
			try {
				section1 = sectList1.get(iSection1 - 1);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("RogetELKB [sectList1]: " + e);
			}

			String sSection1 = section1.getStrSectionNum() + ": "
					+ section1.getSectionName();

			// Obtain all of the path info for the 2nd word
			int iHead2 = ref2.getHeadNum();
			String sKeyWord2 = ref2.getRefName();
			String sPos2 = ref2.getPos();

			HeadInfo headInfo2 = new HeadInfo();
			try {
				headInfo2 = headList.get(iHead2 - 1);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("RogetELKB [headList]: " + e);
			}

			String sHeadName2 = iHead2 + ". " + headInfo2.getHeadName();
			int iClass2 = headInfo2.getClassNum();
			int iSection2 = headInfo2.getSectNum();
			String sSubSection2 = headInfo2.getSubSectName();
			ArrayList<String> headGroup2 = headInfo2.getHeadGroup();

			RogetClass rogClass2 = new RogetClass();
			try {
				rogClass2 = classList.get(iClass2 - 1);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("RogetELKB [classList]: " + e);
			}

			String sRogClass2 = rogClass2.getStrClassNum() + ": "
					+ rogClass2.getClassName();
			ArrayList<Section> sectList2 = rogClass2.getSectionList();

			Section section2 = new Section();
			try {
				section2 = sectList2.get(iSection2 - 1);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("RogetELKB [sectList2]: " + e);
			}

			String sSection2 = section2.getStrSectionNum() + ": "
					+ section2.getSectionName();

			// Add the references and the full path to the top of ontology
			// and the two words to the path
			rtPath.add(strRef1); // 0
			rtPath.add(sPos1); // 1
			rtPath.add(strRef2); // 2
			rtPath.add(sPos2); // 3
			rtPath.add(headInfo1.toString()); // 4
			rtPath.add(headInfo2.toString()); // 5
			rtPath.add(strWord1); // 6
			rtPath.add(strWord2); // 7
			// this defines the path info. All other information starts
			// at position 8

			// System.out.println("Path between " + strRef1 + " and " +
			// strRef2);
			// System.out.println(headInfo1);
			// System.out.println(headInfo2);

			// Same Paragrapgh
			// length = 2
			if ((iHead1 == iHead2) && (sKeyWord1.equals(sKeyWord2))
					&& (sPos1.equals(sPos2))) {
				// 1st lets check if in the same SG

				Head head1 = text.getHead(iHead1);
				Paragraph para1 = head1.getPara(sKeyWord1, sPos1);
				SG SG1 = para1.getSG(strWord1);

				Head head2 = text.getHead(iHead2);
				Paragraph para2 = head2.getPara(sKeyWord2, sPos2);
				SG SG2 = para2.getSG(strWord2);

				// If the two semicolon groups are equal
				// length = 0
				if (SG1.format().equals(SG2.format())) {
					rtPath.add(SG1.format()); // root
					bSameSG = true;
				} else {
					// if not, simply in the same paragraph
					rtPath.add(sKeyWord1); // root
				}

				// Same POS
				// length = 4
			} else if ((iHead1 == iHead2)
					&& (sKeyWord1.equals(sKeyWord2) == false)
					&& (sPos1.equals(sPos2))) {
				rtPath.add(sKeyWord1);
				rtPath.add(sPos1); // root
				rtPath.add(sKeyWord2);

				// Same Head
				// length = 6
			} else if ((iHead1 == iHead2)
					&& (sKeyWord1.equals(sKeyWord2) == false)
					&& (sPos1.equals(sPos2) == false)) {
				rtPath.add(sKeyWord1);
				rtPath.add(sPos1);
				rtPath.add(sHeadName1); // root
				rtPath.add(sPos2);
				rtPath.add(sKeyWord2);

				// Same HeadGroup
				// length = 8
			} else if ((headGroup1.equals(headGroup2)) && (iHead1 != iHead2)) {
				rtPath.add(sKeyWord1);
				rtPath.add(sPos1);
				rtPath.add(sHeadName1);
				rtPath.add(headGroup1.toString()); // root
				rtPath.add(sHeadName2);
				rtPath.add(sPos2);
				rtPath.add(sKeyWord2);

				// Same SubSection
				// length = 10
			} else if ((iClass1 == iClass2) && (iSection1 == iSection2)
					&& (sSubSection1.equals(sSubSection2))
					&& (sSubSection1.equals("") == false) // we don't want
															// empty SubSection
					&& (headGroup1.equals(headGroup2) == false)) {
				rtPath.add(sKeyWord1);
				rtPath.add(sPos1);
				rtPath.add(sHeadName1);
				rtPath.add(headGroup1.toString());
				rtPath.add(sSubSection1); // root
				rtPath.add(headGroup2.toString());
				rtPath.add(sHeadName2);
				rtPath.add(sPos2);
				rtPath.add(sKeyWord2);

				// Same Section
				// length = 12
			} else if ((iClass1 == iClass2) && (iSection1 == iSection2)
					&& (sSubSection1.equals(sSubSection2) == false)
					|| (sSubSection1.equals("") && sSubSection2.equals(""))) {
				rtPath.add(sKeyWord1);
				rtPath.add(sPos1);
				rtPath.add(sHeadName1);
				rtPath.add(headGroup1.toString());
				rtPath.add(sSubSection1);
				rtPath.add(sSection1); // root
				rtPath.add(sSubSection2);
				rtPath.add(headGroup2.toString());
				rtPath.add(sHeadName2);
				rtPath.add(sPos2);
				rtPath.add(sKeyWord2);

				// Same Class
				// length = 14
			} else if ((iClass1 == iClass2) && (iSection1 != iSection2)) {
				rtPath.add(sKeyWord1);
				rtPath.add(sPos1);
				rtPath.add(sHeadName1);
				rtPath.add(headGroup1.toString());
				rtPath.add(sSubSection1);
				rtPath.add(sSection1);
				rtPath.add(sRogClass1); // root
				rtPath.add(sSection2);
				rtPath.add(sSubSection2);
				rtPath.add(headGroup2.toString());
				rtPath.add(sHeadName2);
				rtPath.add(sPos2);
				rtPath.add(sKeyWord2);

				// Different classes - infinite distance
				// length = 16
			} else if ((iClass1 != iClass2)) {
				rtPath.add(sKeyWord1);
				rtPath.add(sPos1);
				rtPath.add(sHeadName1);
				rtPath.add(headGroup1.toString());
				rtPath.add(sSubSection1);
				rtPath.add(sSection1);
				rtPath.add(sRogClass1);
				rtPath.add("T"); // root
				rtPath.add(sRogClass2);
				rtPath.add(sSection2);
				rtPath.add(sSubSection2);
				rtPath.add(headGroup2.toString());
				rtPath.add(sHeadName2);
				rtPath.add(sPos2);
				rtPath.add(sKeyWord2);

			} else {
				rtPath.add("NOT CALCULATED");
			}

			if (bSameSG == false) {
				// add the 1st word after all the header info
				rtPath.add(8, strWord1);
				rtPath.add(strWord2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Path(rtPath); // create a Path object
	}

	/**
	 * lookUpWordInIndex - looks up a word or phrase in the Index and returns
	 * all possible references.
	 */
	public void lookUpWordInIndex() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String word = new String();
		String sRefIndex;
		String sRef;
		ArrayList<String> refList;

		try {

			do {
				System.out.print("\nEnter a word or phrase: ");

				word = br.readLine();

				// Find all references for a given word

				System.out.println();
				index.printEntry(word, 1);
				refList = index.getStrRefList(word);
				if (refList.size() > 0) {

					// Look up reference
					System.out
							.print("Enter the number of the reference to be looked up: ");
					sRefIndex = br.readLine();
					Integer index = new Integer(sRefIndex);
					sRef = refList.get(index.intValue() - 1);

					ParseRef(sRef);

					Head elkbHead = text.getHead(iHead);
					Paragraph elkbPara = elkbHead.getPara(sKey, sPOS);
					SG elkbSG = elkbPara.getSG(word);

					System.out.println();
					System.out.println("========================== Head "
							+ iHead + " =========================");
					System.out.println(sPOS);
					elkbPara.print();
					System.out.println();
					System.out.println("SG: " + elkbSG.format());

				} // end if

				System.out.print("\nLook up another word or phrase [y/n] ? ");
				word = br.readLine();

			} while (!(word.equals("n")));

		} catch (IOException ioe) {
			System.out.println("IO error:" + ioe);
		}
	}

	/**
	 * parses a reference string.
	 * 
	 * @param sRef
	 */
	private void ParseRef(String sRef) {
		Reference ref = new Reference(sRef);
		sKey = ref.getRefName();
		iHead = ref.getHeadNum();
		sPOS = ref.getPos();
	}
}
