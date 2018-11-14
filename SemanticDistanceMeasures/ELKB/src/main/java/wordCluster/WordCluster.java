/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package wordCluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import ca.site.elkb.HeadInfo;
import ca.site.elkb.RogetELKB;


/*******************************************************************************
 * WordCluster: program that calculates the semantic similarity for a set of
 * words and phrases. The following calculations are preformed: 1. pairwise
 * semantic similarity for all combinations of two words or phrases 2. list of
 * words or phrases with its most similar word or phrase 3. list of words or
 * phrases in the set who are of low similarity with any other word or phrase in
 * the set 4. clusters of words and phrases depending on their memberships in
 * given Heads
 * 
 * This program is developped for Ralf Steinberger at the EC JRC
 * 
 * Author : Mario Jarmasz Created: October, 2003 Usage : java WordCluster
 * <inputFile> <outputFile> Format of input file: a list of words and phrases,
 * one word or phrase per line.
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.2 Nov 2008
 ******************************************************************************/

public class WordCluster {

	// Attributes
	ArrayList<String> wordList;

	ArrayList<String> notInRogetList;

	RogetELKB elkb;

	BufferedWriter bw;
	
	/**
	 * Default Constructor
	 */
	public WordCluster(){
		this("1911");
	}

	/**
	 * Constructor
	 * 
	 * @param year
	 */
	public WordCluster(String year) {
		wordList = new ArrayList<String>();
		notInRogetList = new ArrayList<String>();
		elkb = new RogetELKB(year);
		System.out.println();
	}

	/**
	 * Main method takes thesaurus year, input and output file.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			if (args.length == 2) {
				WordCluster wc = new WordCluster();
				wc.bw = new BufferedWriter(new FileWriter(args[1]));
				wc.loadFile(args[0]);
				wc.semDist(wc.wordList);
				wc.cluster(wc.wordList);
				wc.bw.write("Words and phrases not in Roget's Thesaurus ("
						+ wc.notInRogetList.size() + "/"
						+ (wc.wordList.size() + wc.notInRogetList.size())
						+ "): ");
				wc.bw.newLine();
				wc.bw
						.write("--------------------------------------------------");
				wc.bw.newLine();
				wc.bw.write(wc.notInRogetList.toString());
				wc.bw.newLine();

				wc.bw.flush();
				wc.bw.close();
			} 
			else if(args.length == 3){
					WordCluster wc = new WordCluster(args[0]);
					wc.bw = new BufferedWriter(new FileWriter(args[2]));
					wc.loadFile(args[1]);
					wc.semDist(wc.wordList);
					wc.cluster(wc.wordList);
					wc.bw.write("Words and phrases not in Roget's Thesaurus ("
							+ wc.notInRogetList.size() + "/"
							+ (wc.wordList.size() + wc.notInRogetList.size())
							+ "): ");
					wc.bw.newLine();
					wc.bw
							.write("--------------------------------------------------");
					wc.bw.newLine();
					wc.bw.write(wc.notInRogetList.toString());
					wc.bw.newLine();
	
					wc.bw.flush();
					wc.bw.close();
			}
			else{
				System.out.println("Usage: java -cp .:rogets-1.4.jar wordCluster.WordCluster <1911 | 1911X1 | 1911X5> <input file> <output file>");
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * loadFile(String): loads the file containing words and phrases into an
	 * array The format of the file is one word or phrase per line
	 * 
	 * @param fname
	 */
	private void loadFile(String fname) {
		String line = new String();

		try {
			BufferedReader br = new BufferedReader(new FileReader(fname));

			for (;;) {

				line = br.readLine();

				if (line == null) {
					br.close();
					break;
				} else if (elkb.index.containsEntry(line)) {
					wordList.add(line);
				} else {
					notInRogetList.add(line);
				}
			}

		} catch (Exception e) {
			System.out.println(line);
			e.printStackTrace();
		}
	}

	/**
	 * SemDist(ArryaList): calculates the semantic distance between all
	 * combinations of pairs of words in the ArrayList
	 * 
	 * @param tokenList
	 */
	private void semDist(ArrayList<String> tokenList) {
		int count = tokenList.size();
		String word1, word2, simMeaning;
		int semDist;

		for (int i = 0; i < count; i++) {
			try {
				word1 = tokenList.get(i);
				bw.write("*** " + word1 + " ***");
				bw.newLine();
				for (int j = 0; j < count; j++) {
					if (i != j) {
						word2 = tokenList.get(j);
						semDist = semDistVal(word1, word2);
						simMeaning = simMeaning(semDist);
						bw.write(word1 + " - " + word2 + ", " + +semDist + ", "
								+ simMeaning + " similarity");
						bw.newLine();
					}
				}
				bw.newLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * SemDistVal(String, String): returns the integer semantic distance value
	 * of two words or phrases This method should be part of the RogetELKB class
	 * I should take a close look at semantics, semantic distance vs. similarity
	 * 
	 * @param word1
	 * @param word2
	 * @return Returns minimum distance between the two words.
	 */
	private int semDistVal(String word1, String word2) {
		ArrayList<int[]> list1 = elkb.index.getEntryListNumerical(word1);
		ArrayList<int[]> list2 = elkb.index.getEntryListNumerical(word2);
		if(list1.size() == 0 || list2.size() == 0){
			return 0;
		}
		int best = 0;
		for (int i = 0; i < list1.size(); i++) {
			int[] entry1 = list1.get(i);
			for (int j = 0; j < list2.size(); j++) {
				int[] entry2 = list2.get(j);
				int diff = 16;
				for (int k = 0; k < 8; k++) {
					if (entry1[k] != entry2[k]){
						if(2*k < diff){
							diff = 2*k;
						}
					}
				}
				if(best < diff){
					best = diff;
				}
			}
		}
		return best;
	}

	/**
	 * SimMeaning(int): returns a string translating the semantic similarity +
	 * 16 = high similarity + 12 to 14 = intermediate similarity + below 10 =
	 * low similarity
	 * 
	 * @param value
	 * @return String indicating high, intermediate or low similarity between words.
	 */
	private String simMeaning(int value) {
		String meaning = new String();

		switch (value) {
		case 16:
			meaning = "high";
			break;
		case 14:
		case 12:
		case 10:
			meaning = "intermediate";
			break;
		case 8:
		case 6:
		case 4:
		case 2:
		case 0:
			meaning = "low";
			break;
		default:
			meaning = "incorrect similarity value";
		}

		return meaning;
	}

	/**
	 * cluster(ArrayList): clusters the words and phrases in the array list
	 * according to their membership in a head group
	 * 
	 * @param tokenList
	 */
	private void cluster(ArrayList<String> tokenList) {
		ArrayList<String> headList; // could be a simple array
		TreeMap<Integer, ArrayList<String>> headMap = new TreeMap<Integer, ArrayList<String>>();
		int count = tokenList.size();
		String word;
		ArrayList<String> list;

		for (int i = 0; i < count; i++) {
			word = tokenList.get(i);

			headList = new ArrayList<String>(elkb.index.getHeadNumbers(word));
			Iterator<String> iter = headList.iterator();
			while (iter.hasNext()) {
				Integer headNo = new Integer(iter.next());
				ArrayList<String> single = new ArrayList<String>();
				single.add(word);
				list = headMap.put(headNo, single);
				// if there is already something in the map, list != null
				if (list != null) {
					list.add(word);
					headMap.put(headNo, list);
				}
			}
		}

		printCluster(headMap);
	}

	/**
	 * printCluster(TreeMap): prints clusters containing more than one word or
	 * phrase
	 * 
	 * @param cluster
	 */
	private void printCluster(TreeMap<Integer, ArrayList<String>> cluster) {
		TreeSet<Integer> keySet = new TreeSet<Integer>(cluster.keySet());
		TreeSet<ComparableArray<String>> clusterSet = new TreeSet<ComparableArray<String>>();
		ArrayList<ComparableArray<String>> wordsInGroups = new ArrayList<ComparableArray<String>>();
		ArrayList<HeadInfo> headInfoList = elkb.category.getHeadList();

		try {
			bw.write("Possible Roget's Thesaurus clusters:");
			bw.newLine();
			bw.write("------------------------------------");
			bw.newLine();

			Iterator<Integer> iter = keySet.iterator();

			while (iter.hasNext()) {
				Integer key = iter.next();
				ComparableArray<String> list = new ComparableArray<String>(cluster.get(key));
				if (list.size() > 1) {
					int headNo = key.intValue();
					try {
						HeadInfo headInfo = headInfoList.get(headNo - 1);
						bw.write("Head: " + key + " " + headInfo.getHeadName());
						bw.newLine();
						bw.write(list.toString());
						bw.newLine();
						//list.add(0, headInfo);
						list.setHeadInfo(headInfo);
						clusterSet.add(list);
						bw.newLine();
						wordsInGroups.add(list);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Head number:" + (headNo - 1));
					}
				}
			}

			// Should be another method
			bw.write("The 10 largest clusters:");
			bw.newLine();
			bw.write("------------------------");
			bw.newLine();

			// Could use toArray method instead, would be safer and should work
			// better...
			ArrayList<ComparableArray<String>> clusterArray = new ArrayList<ComparableArray<String>>();
			clusterArray.addAll(clusterSet);
			int arrSize = clusterArray.size();
			for (int i = 1; i < 11 && arrSize-i >= 0; i++) {

				ComparableArray<String> listObj = clusterArray.get(arrSize - i);
				//HeadInfo hi = (HeadInfo) listObj.get(0);
				HeadInfo hi = listObj.getHeadInfo();
				//listObj.remove(0);
				bw.write("Head: " + hi.getHeadNum() + " " + hi.getHeadName());
				bw.newLine();
				bw.write(listObj.toString());
				bw.newLine();
				clusterSet.remove(listObj);
				bw.newLine();
			}

			// Should be another method
			ArrayList<String> allWords = new ArrayList<String>(wordList);
			allWords.removeAll(wordsInGroups);
			if (allWords.size() > 0) {
				bw.write("Words and phrases not in clusters:");
				bw.newLine();
				bw.write("----------------------------------");
				bw.newLine();
				bw.write(allWords.toString());
				bw.newLine();
				bw.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}