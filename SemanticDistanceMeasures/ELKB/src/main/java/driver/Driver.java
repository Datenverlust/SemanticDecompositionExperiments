/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package driver;
/********************************************************************
 * Driver : program that tests the various classes of the ELKB
 * Author : Mario Jarmasz and Alistair Kennedy
 * Created: September, 2000 - February, 2001
 * Required files:
 *    + elkbIndex.xml or newIndex.xml
 *    + head1.xml to head1044.xml in a ./heads directory
 *    + categories.xml
 *    + comm.txt
 *    
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 ********************************************************************/

import ca.site.elkb.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

class Driver {
	// Constants

	// Set Roget Thesaurus Path.
	public static final String USER_HOME = System.getProperty("user.home");

	public static final String ELKB_PATH = System.getProperty("elkb.path",
			USER_HOME + "/roget_elkb");

	public static final String PATH_1911 = ELKB_PATH + "/1911";

	public static final String PATH_1911X1 = ELKB_PATH + "/1911X1";

	public static final String PATH_1911X5 = ELKB_PATH + "/1911X5";

	public static final String INDEX = "/elkbIndex.dat";

	public static final String INDEX_FILE = "/index.xml";

	public static final String CATEG = "/rogetMap.xml";

	public static final String HEADS = "/heads/head";

	/***************************************************************************
	 * main(String args[])
	 **************************************************************************/
	public static void main(String args[]) {
		System.out.println("#==================#");
		System.out.println("# ELKB Driver v1.4 #");
		System.out.println("#==================#\n");

		Driver elkbDriver = new Driver();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int i = 0;
		String choice;
		try {
			while (i != 20) {
				elkbDriver.printMenu();
				choice = br.readLine();
				choice = choice.trim();
				i = Integer.parseInt(choice);

				switch (i) {
				case 1:
					elkbDriver.createIndex("1911", false);
					break;
				case 2:
					elkbDriver.createIndex("1911", true);
					break;
				case 3:
					elkbDriver.createIndex("1911X1", false);
					break;
				case 4:
					elkbDriver.createIndex("1911X1", true);
					break;
				case 5:
					elkbDriver.createIndex("1911X5", false);
					break;
				case 6:
					elkbDriver.createIndex("1911X5", true);
					break;
				/*
				 * case 2 : elkbDriver.testIndex() ; break; case 3 :
				 * elkbDriver.testIndexFile() ; break; case 4 :
				 * elkbDriver.testReference() ; break; case 5 :
				 * elkbDriver.testCategory() ; break; case 6 :
				 * elkbDriver.testRogetClass(); break; case 7 :
				 * elkbDriver.testSection() ; break; case 8 :
				 * elkbDriver.testSubSection(); break; case 9 :
				 * elkbDriver.testGroup() ; break; case 10 :
				 * elkbDriver.testHeadInfo() ; break; case 11 :
				 * elkbDriver.testRogetText() ; break; case 12 :
				 * elkbDriver.testHead() ; break; case 13 :
				 * elkbDriver.testParagraph() ; break; case 14 :
				 * elkbDriver.testSG() ; break; case 15 :
				 * elkbDriver.testSemRel() ; break; case 16 :
				 * elkbDriver.testELKBClass() ; break; case 17 :
				 * elkbDriver.printAllWords() ; break; case 18 :
				 * elkbDriver.printAllSG() ; break; case 19 :
				 * elkbDriver.testVariant() ; break;
				 */
				case 20:
					System.out.println("\nGoodbye");
					break;
				default:
					System.out.println("\n#" + i
							+ " has not yet been implemented");
				}
			}
		} catch (IOException ioe) {
			System.out.println("IO error:" + ioe);
		}
	}

	/***************************************************************************
	 * printMenu()
	 **************************************************************************/
	private void printMenu() {
		System.out.println("\n         M E N U\n");
		System.out.println("1.  Create Roget's Index: 1911");
		System.out.println("2.  Create Roget's Index: 1911 -- break phrases");
		System.out.println("3.  Create Roget's Index: 1911X1");
		System.out.println("4.  Create Roget's Index: 1911X1 -- break phrases");
		System.out.println("5.  Create Roget's Index: 1911X5");
		System.out.println("6.  Create Roget's Index: 1911X5 -- break phrases");
		/*
		 * System.out.println("2. Test the Index class"); System.out.println("3.
		 * Test the Index class, load from file"); System.out.println("4. Test
		 * the Reference class"); System.out.println("5. Test the Category
		 * class"); System.out.println("6. Test the RogetClass class");
		 * System.out.println("7. Test the Section class");
		 * System.out.println("8. Test the SubSection class");
		 * System.out.println("9. Test the Group class"); System.out.println("10
		 * Test the HeadInfo class"); System.out.println("11. Test the RogetText
		 * class"); System.out.println("12. Test the Head class");
		 * System.out.println("13. Test the Paragraph class");
		 * System.out.println("14. Test the SG class"); System.out.println("15.
		 * Test the SemRel class"); System.out.println("16. Interactive
		 * Electronic Lexical Database"); System.out.println("17. Get all words
		 * and phrases"); System.out.println("18. Get all semicolon groups");
		 * System.out.println("19. Test the Variant class");
		 */
		System.out.println("20. Quit\n");
	}

	/***************************************************************************
	 * testReference()
	 **************************************************************************/
	private void testReference() {
		System.out.println("\nTesting the Reference class");
		System.out.println("-----------------------------\n");

		long start = System.currentTimeMillis();
		Reference elkbRef = new Reference("word list", 87, "n.", "thesaurus");
		long stop = System.currentTimeMillis();

		System.out.println("Elapsed time: " + (stop - start) + " millisecs");
		System.out.println(elkbRef);
		elkbRef.print();
		pause();
	}

	/***************************************************************************
	 * createIndex()
	 **************************************************************************/
	private void createIndex(String year, boolean breakPhrases) {

		System.out.println("\nCreate Roget's Index");
		System.out.println("----------------------\n");
		System.out.println("Creating the index. Please wait...\n");

		Runtime.getRuntime().gc();
		long memStart = Runtime.getRuntime().totalMemory();
		long start = System.currentTimeMillis();
		Index elkbIndex = new Index();
		if (year.equals("1911")) {
			elkbIndex = new Index(PATH_1911 + INDEX_FILE, 100000, breakPhrases);
		} else if (year.equals("1911X1")) {
			elkbIndex = new Index(PATH_1911X1 + INDEX_FILE, 100000, breakPhrases);
		} else if (year.equals("1911X5")) {
			elkbIndex = new Index(PATH_1911X5 + INDEX_FILE, 100000, breakPhrases);
		}
		long stop = System.currentTimeMillis();
		Runtime.getRuntime().gc();
		long memStop = Runtime.getRuntime().totalMemory();

		System.out.println("Elapsed time     : " + (stop - start)
				+ " millisecs");
		System.out.println("Memory used      : " + (memStop - memStart)
				+ " bytes");
		System.out.println("# of index items : " + elkbIndex.getItemCount());
		System.out.println("# of references  : " + elkbIndex.getRefCount());
		System.out.println("# of unique refs : " + elkbIndex.getUniqRefCount());
		System.out.println("Items Map size   : " + elkbIndex.getItemsMapSize());

		System.out.println("\nSaving Index object. Please wait...");

		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream("elkbIndex.dat"));
			out.writeObject(elkbIndex);
			out.close();
		} catch (Exception e) {
			System.out.print("Error: " + e);
			System.exit(1);
		}
	}

	/***************************************************************************
	 * testIndex() The two index .dat files are: + elkbIndex.dat + oldIndex.dat
	 **************************************************************************/
	private void testIndex() {
		System.out.println("\nTesting the Index class");
		System.out.println("-------------------------\n");
		System.out.println("Loading the Index object. Please wait...\n");
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					INDEX));
			Index elkbIndex = (Index) in.readObject();
			System.out.println("There are " + elkbIndex.getItemsMapSize()
					+ " entires in the Index");
			System.out.println(elkbIndex.getItemCount());
			lookUpWord(elkbIndex);
			// findCommon(elkbIndex, "commHeads.txt");
		} catch (Exception e) {
			System.out.print("Error: " + e);
			System.exit(1);
		}
	}

	/***************************************************************************
	 * testIndexFile() Test the index class, loading directly from a file
	 **************************************************************************/
	private void testIndexFile() {

		System.out.println("\nTesting the Index class");
		System.out.println("-------------------------\n");
		System.out.println("Loading the Index object.");
		System.out.println("Using the " + INDEX_FILE + " file");
		System.out.println("Please wait...\n");

		Index elkbIndex = new Index(INDEX_FILE);
		System.out.println("Number of index entries: "
				+ elkbIndex.getItemCount());
		System.out.println("Number of total references: "
				+ elkbIndex.getRefCount());
		System.out.println("Number of unique references: "
				+ elkbIndex.getUniqRefCount());
		lookUpWord(elkbIndex);
	}

	/***************************************************************************
	 * testSection()
	 **************************************************************************/
	private void testSection() {
		System.out.println("\nTesting the Section class");
		System.out.println("----------------------------\n");

		long start = System.currentTimeMillis();
		Section elkbSection = new Section(1, "Existence", 1, 8);
		long stop = System.currentTimeMillis();

		System.out.println("Elapsed time: " + (stop - start) + " millisecs");
		System.out.println(elkbSection);
		elkbSection.print();
		System.out.println();

		elkbSection.printHeadInfo();

		System.out.println("Adding heads..\n");
		/*
		 * This code has to be re-written HeadInfo head1 = new HeadInfo(1,
		 * "Existence"); HeadInfo head2 = new HeadInfo(2, "Nonexistence");
		 * HeadInfo head3 = new HeadInfo(3, "Substantiality"); HeadInfo head4 =
		 * new HeadInfo(4, "Insubstantiality"); HeadInfo head5 = new HeadInfo(5,
		 * "Intrinsicality"); HeadInfo head6 = new HeadInfo(6,
		 * "Extrinsicality"); HeadInfo head7 = new HeadInfo(7, "State");
		 * HeadInfo head8 = new HeadInfo(8, "Circumstance");
		 * elkbSection.addHeadInfo(head1); elkbSection.addHeadInfo(head2);
		 * elkbSection.addHeadInfo(head3); elkbSection.addHeadInfo(head4);
		 * elkbSection.addHeadInfo(head5); elkbSection.addHeadInfo(head6);
		 * elkbSection.addHeadInfo(head7); elkbSection.addHeadInfo(head8);
		 * elkbSection.printHeadInfo();
		 */
		System.out.println();
		System.out.println("There are " + elkbSection.headCount()
				+ " Heads in the " + elkbSection.getSectionName() + " Section");
		pause();
	}

	/***************************************************************************
	 * testSubSection()
	 **************************************************************************/
	private void testSubSection() {
		System.out.println("\nTesting the SubSection class");
		System.out.println("------------------------------\n");
		pause();
	}

	/***************************************************************************
	 * testHeadInfo()
	 **************************************************************************/
	private void testHeadInfo() {
		System.out.println("\nTesting the HeadInfo class");
		System.out.println("----------------------------\n");

		long start = System.currentTimeMillis();
		String sInfo = "<headword>#<b>#[001] #1# Existence #</b>#</headword>";
		String subSectInfo = "<subSectionTitle>#Abstract#</subSectionTitle>";
		String sGroupInfo = "<headGroup 1 2  >";

		HeadInfo elkbHeadInfo = new HeadInfo(sInfo, 1, 1, subSectInfo,
				sGroupInfo);

		long stop = System.currentTimeMillis();

		System.out.println("Elapsed time: " + (stop - start) + " millisecs");
		System.out.println(elkbHeadInfo);
		pause();
	}

	/***************************************************************************
	 * testGroup()
	 **************************************************************************/
	private void testGroup() {
		System.out.println("\nTesting the Group class");
		System.out.println("----------------------------\n");

		Group elkbGroup = new Group(132);

		/*
		 * This code must be re-written HeadInfo headInfo = new HeadInfo("<headword>#<b>#[132]
		 * #132# Young person #</b>#</headword>");
		 * elkbGroup.addHead(headInfo);
		 * 
		 * HeadInfo hI2 = new HeadInfo("<headword>#<b>#[133] #133# Old person #</b>#</headword>");
		 * elkbGroup.addHead(hI2);
		 * 
		 * HeadInfo hI3 = new HeadInfo("<headword>#<b>#[134] #134# Adultness #</b>#</headword>");
		 * elkbGroup.addHead(hI3);
		 * 
		 * System.out.println(elkbGroup); System.out.println("Head start: " +
		 * elkbGroup.getHeadStart()); System.out.println("# of heads: " +
		 * elkbGroup.getHeadCount()); pause();
		 */
	}

	/***************************************************************************
	 * testRogetClass()
	 **************************************************************************/
	private void testRogetClass() {
		System.out.println("\nTesting the RogetClass class");
		System.out.println("----------------------------\n");

		long start = System.currentTimeMillis();
		RogetClass elkbClass = new RogetClass(3, "Class three", "Matter", 1, 3);
		long stop = System.currentTimeMillis();

		System.out.println("Elapsed time: " + (stop - start) + " millisecs");
		System.out.println(elkbClass);
		System.out.println();
		elkbClass.print();

		System.out.println("\nAdding Sections...\n");
		Section sec1 = new Section(1, "Matter in general", 319, 323);
		Section sec2 = new Section(2, "Inorganic Matter", 324, 357);
		Section sec3 = new Section(3, "Organic Matter", 358, 446);
		elkbClass.addSection(sec1);
		elkbClass.addSection(sec2);
		elkbClass.addSection(sec3);
		elkbClass.print();

		System.out.println();
		System.out.println("There are " + elkbClass.sectionCount()
				+ " Sections in the " + elkbClass.getClassName() + " Class");
		System.out.println("There are " + elkbClass.headCount()
				+ " Heads in the " + elkbClass.getClassName() + " Class");
		pause();
	}

	/***************************************************************************
	 * testCategory()
	 **************************************************************************/
	/*private void testCategory() {
		System.out.println("\nTesting the Category class");
		System.out.println("----------------------------\n");

		long start = System.currentTimeMillis();
		Category elkbCategory = new Category(CATEG);
		long stop = System.currentTimeMillis();

		System.out.println("Elapsed time: " + (stop - start) + " millisecs");
		// System.out.println(elkbCategory);
		pause();

		ArrayList classList = elkbCategory.getClassList();
		System.out.println(classList);
		pause();

		elkbCategory.printHeadInfo();
		System.out.println();

		//***********************************************************************
		 //* BufferedReader br = new BufferedReader(new
		 //* InputStreamReader(System.in)); String word = new String(); while (
		 //* !(word.equals("quit.")) ) { System.out.print("\nEnter a Class number:
		 //* ");
		 //* 
		 //* try { word = br.readLine(); System.out.println(); word.trim(); if (
		 //* !(word.equals("quit.")) ) { int index = Integer.parseInt(word); start =
		 //* System.currentTimeMillis(); elkbCategory.printRogetClass(index); stop =
		 //* System.currentTimeMillis(); System.out.println("\nLook-up time: " +
		 //* (stop-start) + " millisecs"); } } catch (IOException ioe) {
		 //* System.out.println("IO error:" + ioe); } }
		 //**********************************************************************

		// elkbCategroy.print();
	}*/

	/***************************************************************************
	 * testSemRel()
	 **************************************************************************/
	/*private void testSemRel() {
		System.out.println("\nTesting the SemRel class");
		System.out.println("----------------------------\n");

		long start = System.currentTimeMillis();
		SemRel elkbSemRel = new SemRel("cross-reference", 508, "surprise");
		long stop = System.currentTimeMillis();

		System.out.println("Elapsed time: " + (stop - start) + " millisecs");
		System.out.println(elkbSemRel);

		elkbSemRel.setPos("Vb.");
		elkbSemRel.print();
		pause();
	}*/

	/***************************************************************************
	 * testSG()
	 **************************************************************************/
	/*private void testSG() {
		System.out.println("\nTesting the SG class");
		System.out.println("--------------------\n");

		long start = System.currentTimeMillis();
		SG elkbSG = new SG(6);
		String w1 = new String("downers");
		elkbSG.addWord(w1);
		String w2 = new String("barbiturates");
		elkbSG.addWord(w2);
		String w3 = new String("barbs");
		String s3 = new String("sl");
		elkbSG.addWord(w3, s3);
		String w4 = new String("morphia");
		elkbSG.addWord(w4);
		String w5 = new String("morphine");
		elkbSG.addWord(w5);
		String w6 = new String("opium");
		elkbSG.addWord(w6);
		SemRel rel = new SemRel("cref", 658, "drug");
		elkbSG.addSemRel(rel);
		long stop = System.currentTimeMillis();

		System.out.println("Elapsed time: " + (stop - start) + " millisecs");
		System.out.println(elkbSG);
		elkbSG.print();

		System.out.println();

		start = System.currentTimeMillis();
		String text = new String();
		text = "<i> downers, </i>barbiturates, barbs,morphia, morphine, ";
		text += "opium, <cref>658<i>drug</i></cref>,;";
		SG sgObj = new SG(6, text);
		stop = System.currentTimeMillis();
		System.out.println("Elapsed time: " + (stop - start) + " millisecs");
		System.out.println(sgObj);
		sgObj.print();

		pause();
	}*/

	/***************************************************************************
	 * testParagraph()
	 **************************************************************************/
	/*private void testParagraph() {
		System.out.println("\nTesting the Paragraph class");
		System.out.println("----------------------------\n");

		long start = System.currentTimeMillis();
		Paragraph elkbPara = new Paragraph(472, 2, "be unlikely", "VB.");
		long stop = System.currentTimeMillis();

		System.out.println("Elapsed time: " + (stop - start) + " millisecs");
		System.out.println(elkbPara);
		System.out.println();

		String sgText;

		sgText = "<sg><i>be unlikely, ";
		sgText += "</i> <sg>be unlikely improbable, look impossible, ";
		sgText += "<etc>etc. adj.</etc>,;</sg>";
		elkbPara.addSG(sgText);

		sgText = "<sg>have a bare chance, show little hope, ";
		sgText += "offer small chance,;</sg>";
		elkbPara.addSG(sgText);

		sgText = "<sg>be implausible, not wash, be hard to believe, ";
		sgText += "lend no colour to, strain  one's credulity, ";
		sgText += "<cref>486 <i>cause doubt</i></cref>,;</sg>";
		elkbPara.addSG(sgText);

		sgText = "<sg>think unlikely, whistle for, ";
		sgText += "<cref>508 <i>not expect</i></cref>,.</sg>";
		elkbPara.addSG(sgText);

		elkbPara.print();

		System.out.println();
		Paragraph elkbPara2 = new Paragraph();
		elkbPara2.setPOS("VB.");
		elkbPara2.setParaKey("be unlikely");
		System.out.println(elkbPara2);

		if (elkbPara.equals(elkbPara2)) {
			System.out.println("The paragraphs are equal");
		} else {
			System.out.println("The paragraphs are _not_ equal");
		}

		pause();
	}*/

	/***************************************************************************
	 * testHead()
	 **************************************************************************/
	/*private void testHead() {
		System.out.println("\nTesting the Head class");
		System.out.println("----------------------\n");

		long start = System.currentTimeMillis();
		Head elkbHead = new Head(1, "Existence", 1, 1);
		long stop = System.currentTimeMillis();
		System.out.println("Elapsed time: " + (stop - start) + " millisecs");
		System.out.println(elkbHead);
		elkbHead.print();
		System.out.println();

		start = System.currentTimeMillis();
		Head elkbHead2 = new Head(HEADS + "301.txt");
		stop = System.currentTimeMillis();
		elkbHead2.print();
		System.out.println();
		System.out.println("Elapsed time: " + (stop - start) + " millisecs");
		System.out.println(elkbHead2);
		System.out.println();

		pause();
	}*/

	/***************************************************************************
	 * testRogetText()
	 **************************************************************************/
	/*private void testRogetText() {
		System.out.println("\nTesting the RogetText class");
		System.out.println("-----------------------------\n");
		System.out.println("Loading the text files. Please wait...\n");

		//***********************************************************************
		 //* Will have to create a definite test Runtime.getRuntime().gc(); long
		 //* memStart = Runtime.getRuntime().totalMemory();
		 //* 
		 //* long start = System.currentTimeMillis(); // Should also test the
		 //* other constructor that loads // all the heads into memory. RogetText
		 //* elkbText = new RogetText(HEADS); long stop =
		 //* System.currentTimeMillis();
		 //* 
		 //* Runtime.getRuntime().gc(); long memStop =
		 //* Runtime.getRuntime().totalMemory();
		 //* 
		 //* System.out.println("Elapsed time: " + (stop-start) + " millisecs");
		 //* System.out.println("Memory used : "+(memStop-memStart)+" bytes\n");
		 //* 
		 //* System.out.println(elkbText);
		 //* 
		 //* pause();
		 //**********************************************************************

		// printTextStats(elkbText);
		// printHead(elkbText);
		// printKeyword(elkbText);
		RogetText elkbText = new RogetText(HEADS);
		printHeadStats(elkbText);
		// generateIndex(elkbText);

		pause();
	}*/

	/***************************************************************************
	 * testElkb()
	 **************************************************************************/
	/*private void testELKB() {
		System.out.println("\nTesting the ELKB");
		System.out.println("-----------------------------\n");
		System.out.println("Loading the text files. Please wait...\n");

		// RogetText elkbText = new RogetText(990, "heads/head");
		RogetText elkbText = new RogetText("heads/head");

		// I will write a routine to get a specific reference
		Head elkbHead = elkbText.getHead(365);
		Paragraph elkbPara = elkbHead.getPara("dog", "N.");
		SG elkbSG = elkbPara.getSG("terrier");
		elkbPara.print();
		System.out.println();
		System.out.println("SG: " + elkbSG.format());

		pause();
	}

	private void testELKBClass() {
		RogetELKB elkb = new RogetELKB();
		// elkb.lookUpPairInIndex();
		elkb.lookUpWordInIndex();
		// elkb.Stats();
	}*/

	/***************************************************************************
	 * pause()
	 **************************************************************************/
	private void pause() {
		System.out.println("\nPress [Enter] to continue\n");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error:" + ioe);
		}
	}

	/***************************************************************************
	 * printKey(Head aHead, int hNum, int pCount, String pos)
	 **************************************************************************/
	private void printKey(Head aHead, int hNum, int pCount, String pos) {
		if (pCount > 0) {
			Paragraph elkbPara;
			for (int i = 0; i < pCount; i++) {
				elkbPara = aHead.getPara(i, pos);
				System.out.println(elkbPara.getParaKey() + " : " + hNum + " : "
						+ pos);
			}
		}
	}

	/***************************************************************************
	 * getKeyword(Head aHead, int pCount, String pos)
	 **************************************************************************/
	private ArrayList<String> getKeyword(Head aHead, int pCount, String pos) {
		ArrayList<String> arrKeys = new ArrayList<String>();
		Paragraph elkbPara;

		if (pCount > 0) {
			for (int i = 0; i < pCount; i++) {
				elkbPara = aHead.getPara(i, pos);
				arrKeys.add(elkbPara.getParaKey());
			}
		}

		return arrKeys;
	}

	/***************************************************************************
	 * printKeyword(RogetText text)
	 **************************************************************************/
	private void printKeyword(RogetText elkbText) {
		Head elkbHead;
		int paraCount;

		for (int headNum = 1; headNum < 991; headNum++) {
			elkbHead = elkbText.getHead(headNum);
			paraCount = elkbHead.getNParaCount();
			printKey(elkbHead, headNum, paraCount, "N.");
			paraCount = elkbHead.getAdjParaCount();
			printKey(elkbHead, headNum, paraCount, "ADJ.");
			paraCount = elkbHead.getVbParaCount();
			printKey(elkbHead, headNum, paraCount, "VB.");
			paraCount = elkbHead.getAdvParaCount();
			printKey(elkbHead, headNum, paraCount, "ADV.");
			paraCount = elkbHead.getIntParaCount();
			printKey(elkbHead, headNum, paraCount, "INT.");
		}
	}

	/***************************************************************************
	 * printHead(RogetText elkbText)
	 **************************************************************************/
	private void printHead(RogetText elkbText) {
		Head elkbHead;
		int paraCount;

		for (int headNum = 1; headNum < 991; headNum++) {
			elkbHead = elkbText.getHead(headNum);
			System.out.println(elkbHead.getClassNum() + " | "
					+ elkbHead.getSectionNum() + " | " + elkbHead.getHeadNum()
					+ " | " + elkbHead.getHeadName() + " | "
					+ elkbHead.getParaCount() + " | " + elkbHead.getSGCount()
					+ " | " + elkbHead.getWordCount());

		}
	}

	/***************************************************************************
	 * printTextStats(RogetText elkbText)
	 **************************************************************************/
	private void printTextStats(RogetText elkbText) {
		System.out.println("\nRoget's Thesaurus Text Statistics");
		System.out.println("---------------------------------");
		System.out.println("Number of words and phrases: "
				+ elkbText.getWordCount());
		System.out.println("Number of nouns            : "
				+ elkbText.getNCount());
		System.out.println("Number of adjectives       : "
				+ elkbText.getAdjCount());
		System.out.println("Number of verbs            : "
				+ elkbText.getVbCount());
		System.out.println("Number of adverbs          : "
				+ elkbText.getAdvCount());
		System.out.println("Number of interjections    : "
				+ elkbText.getIntCount());
		System.out.println();
		System.out.println("Number of paragraphs       : "
				+ elkbText.getParaCount());
		System.out.println("Number of N. paragraphs    : "
				+ elkbText.getNParaCount());
		System.out.println("Number of Adj. paragraphs  : "
				+ elkbText.getAdjParaCount());
		System.out.println("Number of Vb. paragraphs   : "
				+ elkbText.getVbParaCount());
		System.out.println("Number of Adv. paragraphs  : "
				+ elkbText.getAdvParaCount());
		System.out.println("Number of Int. paragraphs  : "
				+ elkbText.getIntParaCount());
		System.out.println();
		System.out.println("Number of semicolon groups : "
				+ elkbText.getSGCount());
		System.out.println("Number of N. SGs           : "
				+ elkbText.getNSGCount());
		System.out.println("Number of Adj. SGs         : "
				+ elkbText.getAdjSGCount());
		System.out.println("Number of Vb. SGs          : "
				+ elkbText.getVbSGCount());
		System.out.println("Number of Adv. SGs         : "
				+ elkbText.getAdvSGCount());
		System.out.println("Number of Int. SGs         : "
				+ elkbText.getIntSGCount());

		System.out.println();
		System.out.println("Number of cross-references : "
				+ elkbText.getCRefCount());
		System.out.println("Number of N. crefs         : "
				+ elkbText.getNCRefCount());
		System.out.println("Number of Adj. crefs       : "
				+ elkbText.getAdjCRefCount());
		System.out.println("Number of Vb. crefs        : "
				+ elkbText.getVbCRefCount());
		System.out.println("Number of Adv. crefs       : "
				+ elkbText.getAdvCRefCount());
		System.out.println("Number of Int. crefs       : "
				+ elkbText.getIntCRefCount());
		System.out.println();

		System.out.println("Number of see references   : "
				+ elkbText.getSeeCount());
		System.out.println("Number of N. see refs      : "
				+ elkbText.getNSeeCount());
		System.out.println("Number of Adj. see refs    : "
				+ elkbText.getAdjSeeCount());
		System.out.println("Number of Vb. see refs     : "
				+ elkbText.getVbSeeCount());
		System.out.println("Number of Adv. see refs    : "
				+ elkbText.getAdvSeeCount());
		System.out.println("Number of Int. see refs    : "
				+ elkbText.getIntSeeCount());
	}

	/***************************************************************************
	 * printHeadStats(RogetText elkbText)
	 **************************************************************************/
	private void printHeadStats(RogetText elkbText) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int headNum = 1000;
		String word = new String();

		ArrayList<String> arrN = new ArrayList<String>();
		ArrayList<String> arrVb = new ArrayList<String>();
		ArrayList<String> arrAdj = new ArrayList<String>();
		ArrayList<String> arrAdv = new ArrayList<String>();
		ArrayList<String> arrInt = new ArrayList<String>();

		HashSet<String> hsN = new HashSet<String>();
		HashSet<String> hsVb = new HashSet<String>();
		HashSet<String> hsAdj = new HashSet<String>();
		HashSet<String> hsAdv = new HashSet<String>();
		HashSet<String> hsInt = new HashSet<String>();

		HashSet<String> hsWNn = loadFromWN("wnNouns.txt");
		HashSet<String> hsWNvb = loadFromWN("wnVerbs.txt");
		HashSet<String> hsWNadj = loadFromWN("wnAdj.txt");
		HashSet<String> hsWNadv = loadFromWN("wnAdv.txt");
		HashSet<String> hsWNAll = new HashSet<String>();
		hsWNAll.addAll(hsWNn);
		hsWNAll.addAll(hsWNvb);
		hsWNAll.addAll(hsWNadj);
		hsWNAll.addAll(hsWNadv);

		int nCount = 0, vbCount = 0, adjCount = 0, advCount = 0, intCount = 0, wordCount = 0;
		int uniqNCount = 0, uniqVbCount = 0, uniqAdjCount = 0, uniqAdvCount = 0, uniqIntCount = 0, uniqWordCount = 0;
		int nParaCount = 0, vbParaCount = 0, adjParaCount = 0, advParaCount = 0, intParaCount = 0, paraCount = 0;
		int nPTotCount = 0, vbPTotCount = 0, adjPTotCount = 0, advPTotCount = 0, intPTotCount = 0, pTotCount = 0;
		int nSGCount = 0, vbSGCount = 0, adjSGCount = 0, advSGCount = 0, intSGCount = 0, sgCount = 0;

		/***********************************************************************
		 * Can also be used in interactive mode while (headNum != 0) {
		 * System.out.print("\nEnter a head number: "); try { word =
		 * br.readLine(); word = word.trim(); headNum = Integer.parseInt(word);
		 * if (headNum != 0) {
		 **********************************************************************/
		for (int i = 1; i <= 990; i++) {
			Head elkbHead = elkbText.getHead(i);
			// elkbHead.print();

			headStats(elkbHead, hsWNAll);

			nParaCount = elkbHead.getNParaCount();
			vbParaCount = elkbHead.getVbParaCount();
			adjParaCount = elkbHead.getAdjParaCount();
			advParaCount = elkbHead.getAdvParaCount();
			intParaCount = elkbHead.getIntParaCount();

			nPTotCount += nParaCount;
			vbPTotCount += vbParaCount;
			adjPTotCount += adjParaCount;
			advPTotCount += advParaCount;
			intPTotCount += intParaCount;

			// Use HashSets to count unique strings
			arrN.addAll(getAllWordList(elkbHead, nParaCount, "N."));
			arrVb.addAll(getAllWordList(elkbHead, vbParaCount, "VB."));
			arrAdj.addAll(getAllWordList(elkbHead, adjParaCount, "ADJ."));
			arrAdv.addAll(getAllWordList(elkbHead, advParaCount, "ADV."));
			arrInt.addAll(getAllWordList(elkbHead, intParaCount, "INT."));

			// The elkbHead.getXCount method is incorrect!

			nSGCount += elkbHead.getNSGCount();
			vbSGCount += elkbHead.getVbSGCount();
			adjSGCount += elkbHead.getAdjSGCount();
			advSGCount += elkbHead.getAdvSGCount();
			intSGCount += elkbHead.getIntSGCount();

		}

		nCount = arrN.size();
		vbCount = arrVb.size();
		adjCount = arrAdj.size();
		advCount = arrAdv.size();
		intCount = arrInt.size();

		hsN.addAll(arrN);
		hsVb.addAll(arrVb);
		hsAdj.addAll(arrAdj);
		hsAdv.addAll(arrAdv);
		hsInt.addAll(arrInt);

		uniqNCount = hsN.size();
		uniqVbCount = hsVb.size();
		uniqAdjCount = hsAdj.size();
		uniqAdvCount = hsAdv.size();
		uniqIntCount = hsInt.size();

		/***********************************************************************
		 * Comparison of WN and Roget word and phrase HashSets
		 **********************************************************************/
		hsWNn.retainAll(hsN);
		hsWNvb.retainAll(hsVb);
		hsWNadj.retainAll(hsAdj);
		hsWNadv.retainAll(hsAdv);
		int totOverlap = hsWNn.size() + hsWNvb.size() + hsWNadj.size()
				+ hsWNadv.size();

		wordCount = nCount + vbCount + adjCount + advCount + intCount;
		uniqWordCount = uniqNCount + uniqVbCount + uniqAdjCount + uniqAdvCount
				+ uniqIntCount;
		pTotCount = nPTotCount + vbPTotCount + adjPTotCount + advPTotCount
				+ intPTotCount;
		sgCount = nSGCount + vbSGCount + adjSGCount + advSGCount + intSGCount;

		System.out.println();
		System.out.println("Number of Nouns     : " + nCount);
		System.out.println("Number of Verbs     : " + vbCount);
		System.out.println("Number of Adjectives: " + adjCount);
		System.out.println("Number of Adverbs   : " + advCount);
		System.out.println("Number of Int.      : " + intCount);
		System.out.println("Total words & phr.  : " + wordCount);
		System.out.println("----------------------------\n");

		System.out.println("Unique Nouns        : " + uniqNCount);
		System.out.println("Unique Verbs        : " + uniqVbCount);
		System.out.println("Unique Adjectives   : " + uniqAdjCount);
		System.out.println("Unique Adverbs      : " + uniqAdvCount);
		System.out.println("Unique Int.         : " + uniqIntCount);
		System.out.println("Total unique w & p  : " + uniqWordCount);
		System.out.println("----------------------------\n");

		System.out.println("Number of N Para    : " + nPTotCount);
		System.out.println("Number of Vb Para   : " + vbPTotCount);
		System.out.println("Number of Adj Para  : " + adjPTotCount);
		System.out.println("Number of Adv Para  : " + advPTotCount);
		System.out.println("Number of Int Para  : " + intPTotCount);
		System.out.println("Total Paragraphs    : " + pTotCount);
		System.out.println("----------------------------\n");

		System.out.println("Number of N SGs     : " + nSGCount);
		System.out.println("Number of Vb SGs    : " + vbSGCount);
		System.out.println("Number of Adj SGs   : " + adjSGCount);
		System.out.println("Number of Adv SGs   : " + advSGCount);
		System.out.println("Number of Int SGs   : " + intSGCount);
		System.out.println("Total SGs           : " + sgCount);
		System.out.println("----------------------------\n");

		System.out.println("N. common with WN   : " + hsWNn.size());
		System.out.println("Vb. common with WN  : " + hsWNvb.size());
		System.out.println("Adj. common with WN : " + hsWNadj.size());
		System.out.println("Adv. common with WN : " + hsWNadv.size());
		System.out.println("Int. common with WN : 0");
		System.out.println("Total common with WN: " + totOverlap);
		System.out.println("----------------------------\n");

		/***********************************************************************
		 * Tail of interactive mode } catch (IOException ioe) {
		 * System.out.println("IO error:" + ioe); } }
		 **********************************************************************/

	}

	/***************************************************************************
	 * printStrings(Head aHead, int hNum, int pCount, String pos)
	 **************************************************************************/
	private void printStrings(Head elkbHead, int hNum, int pCount, String pos) {
		if (pCount > 0) {
			Paragraph elkbPara;
			SG elkbSG;
			int sgCount, wordIndex;
			ArrayList<String> wordList;
			ArrayList<String> styleList;
			ArrayList<SemRel> relList;
			Iterator<?> iter;
			SemRel rel;

			for (int i = 0; i < pCount; i++) {
				elkbPara = elkbHead.getPara(i, pos);
				sgCount = elkbPara.getSGCount();
				for (int isg = 0; isg < sgCount; isg++) {
					elkbSG = elkbPara.getSG(isg);
					wordList = elkbSG.getWordList();
					styleList = elkbSG.getStyleTagList();
					iter = wordList.iterator();
					wordIndex = 0;

					while (iter.hasNext()) {
						if (styleList.get(wordIndex) != "<punct>") {
							System.out.println(iter.next() + " | "
									+ elkbPara.getParaKey() + " | " + hNum
									+ " | " + pos);
						} else {
							iter.next();
						}
						wordIndex++;
					} // end while

					relList = elkbSG.getSemRelList();
					iter = relList.iterator();
					while (iter.hasNext()) {
						rel = (SemRel) iter.next();
						System.out.println(rel.getRefName() + " | "
								+ elkbPara.getParaKey() + " | " + hNum + " | "
								+ pos);
					} // end while

				} // end for
			} // end for
		} // end if
	} // end printStrings

	/***************************************************************************
	 * getAllWordList(Head elkbHead, int pCount, String pos) This method should
	 * belong in class Head Performs some statistics at the same time
	 * HeadNumber,HeadName, H. in WN, # of para, # of SG, # of strings, % of
	 * c.s., % of c.k.
	 **************************************************************************/
	private ArrayList<String> getAllWordList(Head elkbHead, int pCount, String pos) {
		ArrayList<String> wordList = new ArrayList<String>();

		if (pCount > 0) {
			Paragraph elkbPara;

			for (int i = 0; i < pCount; i++) {
				elkbPara = elkbHead.getPara(i, pos);
				wordList.addAll(elkbPara.getAllWordList());
			}

		}

		return wordList;
	} // end getAllWordList

	/***************************************************************************
	 * headStats(Head elkbHead, HashSet hsSet) Calculates statistics on a Head
	 * HeadNumber,HeadName, H. in WN, # of para, # of SG, # of strings, % of
	 * c.s., % of c.k.
	 **************************************************************************/
	private void headStats(Head elkbHead, HashSet<String> hsSet) {
		ArrayList<String> rtList = new ArrayList<String>();
		ArrayList<String> keyList = new ArrayList<String>();
		HashSet<String> wnSet1 = new HashSet<String>(hsSet);
		HashSet<String> wnSet2 = new HashSet<String>(hsSet);

		rtList.addAll(getAllWordList(elkbHead, elkbHead.getNParaCount(), "N."));
		rtList
				.addAll(getAllWordList(elkbHead, elkbHead.getVbParaCount(),
						"VB."));
		rtList.addAll(getAllWordList(elkbHead, elkbHead.getAdjParaCount(),
				"ADJ."));
		rtList.addAll(getAllWordList(elkbHead, elkbHead.getAdvParaCount(),
				"ADV."));
		rtList.addAll(getAllWordList(elkbHead, elkbHead.getIntParaCount(),
				"INT."));

		keyList.addAll(getKeyword(elkbHead, elkbHead.getNParaCount(), "N."));
		keyList.addAll(getKeyword(elkbHead, elkbHead.getVbParaCount(), "VB."));
		keyList
				.addAll(getKeyword(elkbHead, elkbHead.getAdjParaCount(), "ADJ."));
		keyList
				.addAll(getKeyword(elkbHead, elkbHead.getAdvParaCount(), "ADV."));
		keyList
				.addAll(getKeyword(elkbHead, elkbHead.getIntParaCount(), "INT."));

		int headNum = elkbHead.getHeadNum();
		String headName = elkbHead.getHeadName();
		String hInWN = "No";
		int paraNum = elkbHead.getParaCount();
		int sgNum = elkbHead.getSGCount();
		int strNum = rtList.size();

		String lcHeadName = headName.toLowerCase();
		if (wnSet1.contains(lcHeadName))
			hInWN = "Yes";

		wnSet1.retainAll(rtList);
		wnSet2.retainAll(keyList);

		int csNum = wnSet1.size();
		int ckNum = wnSet2.size();

		System.out.println(headNum + "," + headName + "," + hInWN + ","
				+ paraNum + "," + sgNum + "," + strNum + "," + csNum + ","
				+ ckNum);

	} // end headStats

	/***************************************************************************
	 * loadFromWN(String fileName) Method that reads words from WN files and
	 * stores them in HashSet The WN files have been created from the wn_s.pl
	 * Prolog file
	 **************************************************************************/
	private HashSet<String> loadFromWN(String fileName) {
		HashSet<String> hsWords = new HashSet<String>();
		String line = new String();
		String word = new String();

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));

			for (;;) {
				line = br.readLine();
				if (line == null) {
					br.close();
					break;
				} else {
					StringTokenizer st = new StringTokenizer(line, ",");
					word = st.nextToken();
					hsWords.add(word);
				}
			}
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}

		return hsWords;
	}

	/***************************************************************************
	 * findCommon(Index elkbIndex, String file) * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * This method has to be re-written due to changes in Index class * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * 
	 **************************************************************************/
	/*
	 * public void findCommon(Index elkbIndex, String file) { String line = new
	 * String(); TreeSet notInIndex = new TreeSet(); ArrayList entryList = new
	 * ArrayList(); String sKey = new String();
	 * 
	 * try { TreeMap commonMap = new TreeMap(); BufferedReader br = new
	 * BufferedReader(new FileReader(file)); for ( ; ; ) { line = br.readLine();
	 * if (line == null) { br.close(); break; } else { // Look up word in index
	 * if (elkbIndex.containsEntry(line)) { // System.out.println("** " + line + "
	 * **"); entryList = elkbIndex.getEntry(line); Iterator iter =
	 * entryList.iterator(); // dump the "added", "original" tag iter.next();
	 * while ( iter.hasNext() ) { Reference ref = (Reference) iter.next(); sKey =
	 * String.valueOf(ref.getHeadNum()); // System.out.println(sKey); ArrayList
	 * valList = new ArrayList(); // //Insert values into the HashMap // if
	 * (commonMap.containsKey(sKey)) { valList = (ArrayList)
	 * commonMap.get(sKey); valList.add(line); } else { valList.add(line); }
	 * commonMap.put(sKey, valList); // System.out.println(sKey + " : " +
	 * valList); } } else { notInIndex.add(line); System.out.println(line); } } }
	 * 
	 * System.out.println(); System.out.println("Number of words not in the
	 * index: " + notInIndex.size() ); System.out.println("Number of keys in
	 * map: " + commonMap.size() ); pause(); // Key 0 are see references !!! for
	 * (int i=1; i<991; i++) { sKey = String.valueOf(i); ArrayList valList =
	 * new ArrayList(); if (commonMap.containsKey(sKey)) { valList = (ArrayList)
	 * commonMap.get(sKey); System.out.println(sKey + " : " + valList.size() ); }
	 * else { System.out.println(sKey + " : 0"); } } } catch (IOException ioe) {
	 * System.out.println("Error:" + ioe); } }
	 * 
	 */

	/***************************************************************************
	 * lookUpWord(Index elkbIndex, String file)
	 **************************************************************************/
	private void lookUpWord(Index elkbIndex) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String word = new String();
		while (!(word.equals("quit."))) {
			System.out.print("\nEnter a word or phrase: ");
			try {
				word = br.readLine();
				System.out.println();
				long start = System.currentTimeMillis();
				elkbIndex.printEntry(word);

				/***************************************************************
				 * code to test getRefObjList and getStrRefList
				 * 
				 * ArrayList refList = elkbIndex.getRefObjList(word); if
				 * (refList == null) { System.out.println("No reference
				 * objects."); } else { Reference ref =
				 * (Reference)refList.get(0); System.out.println("Reference
				 * object: " + ref); }
				 * 
				 * ArrayList strRefList = elkbIndex.getStrRefList(word); if
				 * (strRefList == null) { System.out.println("No reference
				 * strings."); } else { String strRef =
				 * (String)strRefList.get(0); System.out.println("String
				 * reference: " + strRef); }
				 * 
				 **************************************************************/
				long stop = System.currentTimeMillis();
				System.out.println("\nLook-up time: " + (stop - start)
						+ " millisecs");
			} catch (IOException ioe) {
				System.out.println("IO error:" + ioe);
			}
		}
	}

	/***************************************************************************
	 * Get all words and phrases in the Text Important step in creating the
	 * computer generated index
	 **************************************************************************/
	private void printAllWords() {
		Head elkbHead;

		for (int headNum = 1; headNum < 991; headNum++) {
			elkbHead = new Head(HEADS + headNum + ".txt");
			elkbHead.printAllWords();
		}

		// elkbHead = new Head(HEADS + "503.txt");
		// elkbHead.printAllWords();

	}

	/***************************************************************************
	 * Print the content of the semicolon groups without any special formatting
	 * Used to compare semicolon groups to synsets
	 **************************************************************************/
	private void printAllSG() {
		Head elkbHead;

		for (int headNum = 1; headNum < 991; headNum++) {
			elkbHead = new Head(HEADS + headNum + ".txt");
			elkbHead.printAllSG();
		}
	}

	/***************************************************************************
	 * Test the Variant class
	 **************************************************************************/
	private void testVariant() {
		// Variant elkbVariant = new Variant("AmBr.lst");
		Variant elkbVariant = new Variant();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String word = new String();
		while (!(word.equals("quit."))) {
			System.out.print("\nEnter a word or phrase (or quit. to stop): ");
			try {
				word = br.readLine();
				System.out.println(elkbVariant.amToBr(word));
				System.out.println(elkbVariant.brToAm(word));
			} catch (IOException ioe) {
				System.out.println("IO error:" + ioe);
			}
		}
	}

}
