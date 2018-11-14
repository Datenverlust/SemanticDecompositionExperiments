/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package lexicalChain;

/**
 * Lexical Chain class: allows to build lexical chains in a given text
 *
 * Requires: +input text
 *           +stopword list
 *
 * Outputs:  lexical chains?!?
 *
 * Notes:
 *   + The Active Significant Word List is an array of word & sentence
 *     number pairs. Initially consists of all words in a text with 
 *     the stop words removed.
 *
 * @author Mario Jarmasz and Alistair Kennedy
 * @version 1.4 2013
 **/

import java.io.*;
import java.util.*;

import ca.site.elkb.*;

public class LexicalChain {
	// Constants - correct this using classpath some day...
	public static final String STOPWORDS = "stops.txt";
	// USED indicated that a word has been used in a chain
	public static final Integer USED = new Integer(77);
	public static final Integer END = new Integer(99);

	// Attributes
	private HashSet<String> stopWords;
	private ArrayList<String[]> ASWL; // Active Significant Word List
	private RogetELKB elkb;


	/**
	 * This constructor just loads ELKB of a given year and stopwords.
	 *  
	 * @param year
	 */
	public LexicalChain(String year) {
		stopWords = new HashSet<String>();
		ASWL = new ArrayList<String[]>();

		loadStopWordList(STOPWORDS);
		elkb = new RogetELKB(year);
	}
	
	/**
	 * Parameterless constructor, just loads ELKB and stopwords.
	 */
	public LexicalChain() {
		stopWords = new HashSet<String>();
		ASWL = new ArrayList<String[]>();

		loadStopWordList(STOPWORDS);
		elkb = new RogetELKB();
	}

	/**
	 * This constructor takes a file as input and generates lexical chains out
	 * of the sentences in the file.
	 * 
	 * @param fileName
	 * @param year
	 */
	public LexicalChain(String fileName, String year) {
		this(year);
		loadFile(fileName);
		System.out.println("Step1: Select a set of candidate words");
		printCandidateWords();
	}
	

	/**
	 * Main method creates lexical chains of the file passed as the first
	 * argument.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			if (args.length == 2) {
				LexicalChain lc = new LexicalChain(args[1], args[0]);
				lc.printCandidateWords();
				lc.buildLCFinal();
			} else if(args.length == 1){
				LexicalChain lc = new LexicalChain(args[0], "1911");
				lc.buildLCFinal();
			}
			else{
				System.out.println("Usage: java -cp .:rogets-1.4.jar lexicalChain.LexicalChain <1911 | 1911X1 | 1911X5> <filename>");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Loads a stoplics from a file with the name passed as an argument.
	 * 
	 * @param fname
	 */
	public void loadStopWordList(String fname) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fname));

			for (;;) {
				String line = br.readLine();

				if (line == null) {
					br.close();
					break;
				}

				else {
					stopWords.add(line);
				}
			}

		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}

	/**
	 * LoadFile: loads the file for which the lexical chains will be built
	 * Stores file in the Active Significant Word List. Stop words and
	 * punctuation are removed at this stage. The format of the file must be one
	 * sentence per line as the lexical chain building algorithm requires that
	 * the line number of a sentence be known.
	 * 
	 * candiWL contains the same words as ASWL without repetitions Sept 12:
	 * candiWL is not rquired anymore. Left in code for compatibility reasons.
	 * Same goes for uniqWL allHeadSet and wsdHeadSet are required wsdHeadSet
	 * represents all of the Heads that are used more than once in the text,
	 * i.e. the disambiguated meanings
	 * 
	 * @param fname
	 */
	private void loadFile(String fname) {
		int cLine = 0; // current line
		StringTokenizer st;
		// HashSet uniqWL = new HashSet();

		// Sept 12 - new allHeadSet & wsdHeadSet (private var of class)
		// HashSet allHeadSet = new HashSet();
		// Sept 16 - new headDistri to calculate distribution of heads
		// TreeMap headDistri = new TreeMap();

		try {
			BufferedReader br = new BufferedReader(new FileReader(fname));

			for (;;) {
				String line = br.readLine();

				if (line == null) {
					br.close();
					break;
				}

				// else if we are not reading a blank line
				else if (line.equals("") == false) {
					cLine++;

					// Remove all punctuation as well as numbers
					// using regular expressions!
					line = line.replaceAll("[,:;!\"\\.\\d]", "");

					// to this.
					st = new StringTokenizer(line, " ");
					while (st.hasMoreTokens()) {
						String word = st.nextToken();
						String wordLC = word.toLowerCase();

						/***********************************************************
						 * Change if want to consider all POS
						 */
						if (stopWords.contains(wordLC) == false) {
							String[] WordLinePair = { word,
									String.valueOf(cLine) };
							ASWL.add(WordLinePair);
						}
						/***********************************************************/

						// IF the word is NOT in the stop word list, AND
						// it is considered as a NOUN by the ELKB OR
						// the word is NOT known by the ELKB THEN
						// ADD to ASWL
						/*****
						 * ONLY NOUNS **** if ( ( stopWords.contains(wordLC) ==
						 * false ) && (
						 * elkb.index.getRefPOS(wordLC).matches("^N.*|NULL") ) )
						 * { String[] WordLinePair = { word,
						 * String.valueOf(cLine) }; ASWL.add(WordLinePair); }
						 ******/
					}
				}
			} // end for

		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}

	/**
	 * printCandidateWords: method to display word & sentence line pairs
	 * contained in ASWL
	 */
	public void printCandidateWords() {

		Iterator<String[]> iter = ASWL.iterator();
		while (iter.hasNext()) {
			String[] wlp = (iter.next());
			System.out.println(wlp[0] + "\t\t" + elkb.index.getRefPOS(wlp[0].toLowerCase()) + "\t\t" + wlp[1]);
		}
	}

	/**
	 * buildLCFinal: Final implementation of LexicalChain builder Attempt to
	 * build Lexical Chains a la Silber and McCoy
	 */
	public void buildLCFinal() {
		ArrayList<String[]> text = new ArrayList<String[]>(ASWL);
		HashSet<Integer> senseSet = new HashSet<Integer>();

		// required vars
		String curWord;
		String curSent;
		String posWord;
		String posSent;

		double metaScore;
		double newScore;

		// wlp = word line pair
		String[] wlp = { "", "" };
		String[] wlpTxt = { "", "" };

		String sRelation; // type of Thesaural relation used

		MetaChain bestMetaChain;
		TreeSet<MetaChain> chainSet = new TreeSet<MetaChain>();
		HashSet<String> usedWord = new HashSet<String>();
		ArrayList<Reference> arrRefs = new ArrayList<Reference>();

		// go through candiWL

		// buildLexChain:
		Iterator<String[]> iter = ASWL.iterator();
		// Big O calculations
		// System.out.println("ASWL size = " + ASWL.size());
		while (iter.hasNext()) {
			wlp = (iter.next());
			curWord = wlp[0];
			curSent = wlp[1];
			metaScore = 0;
			bestMetaChain = null;

			// If a word has already been used to start a chain,
			// do not attempt to build a chain again
			// This reduces the complexity by a large factor!
			if (usedWord.add(curWord) == false)
				// we're done at this position in the text
				text.remove(0);

			else {

				// get all HeadNo in which the word can be found
				try {
					arrRefs = elkb.index.getRefObjList(curWord);
				} catch (Exception e) {
					System.out.println("Error: " + e);
					System.out.println("Current word: " + curWord);
				}

				text.remove(0);

				// Build a metaChain for _every_ sense of word
				// A sense is defined by the location in a head

				// Should check to see if any of these senses are used in the
				// remaining text

				Iterator<Reference> headIter = arrRefs.iterator();
				while (headIter.hasNext()) {

					Reference ref = headIter.next();
					String sRefName = ref.getRefName();
					int iRefHeadNo = ref.getHeadNum();
					String sRefPOS = ref.getPos();

					// Don't bother building a MetaChain if it has already been
					// built for this sense

					/***************************************
					 * Change for all POS
					 ***************************************/
					if (senseSet.contains(new Integer(iRefHeadNo)) == false) {

						/***
						 * ONLY NOUNS ***
						 * 
						 * if ( (senseSet.contains(new Integer(iRefHeadNo)) ==
						 * false) && (sRefPOS.equals("N.")) ) {
						 ****/

						senseSet.add(new Integer(iRefHeadNo));
						newScore = 0;

						MetaChain metaChain = new MetaChain(curWord, Integer
								.parseInt(curSent), iRefHeadNo);
						// a MetaChain could contain refName & POS
						// information...
						// but not in this implementation!
						metaChain.add(curWord, "T0", Integer.valueOf(curSent));

						Iterator<String[]> textIter = text.iterator();

						while (textIter.hasNext()) {
							wlpTxt = (textIter.next());
							posWord = wlpTxt[0];
							posSent = wlpTxt[1];

							sRelation = elkb.t1Relation(curWord, iRefHeadNo,
									sRefName, sRefPOS, posWord);
							if (sRelation != null) {
								metaChain.add(posWord, sRelation, Integer
										.valueOf(posSent));
								System.out.println(metaChain);
							} // end if

						} // end while

						// Compute score. Store only _best_ meta chain
						newScore = metaChain.getScore();
						if (newScore > metaScore) {
							metaScore = newScore;
							bestMetaChain = metaChain;
						}
					} // end if for building MetaChain
				} // End of HeadNo sense loop

				// Do not keep singleton chains
				if (metaScore > MetaChain.T0_WEIGHT) {
					chainSet.add(bestMetaChain);
					System.out.print(".");
				}

			} // end if word in usedWord HashSet

		} // end of buildLexChain

		System.out.println("Step3: Keep best meta-chain for each word");
		printLCFinal(chainSet);
		chainSet = selectFinalLC(chainSet);

		System.out.println("\nPrinting Lexical Chain");
		System.out.println("----------------------");
		printLCFinal(chainSet);
	}

	/**
	 * selectFinalLC: method that selects the final set of lexical chains given
	 * a set of meta chains
	 * 
	 * @param chainSet
	 * @return Treeset of best MetaChains
	 */
	private TreeSet<MetaChain> selectFinalLC(TreeSet<MetaChain> chainSet) {

		chainSet = keepBestSense(chainSet);
		chainSet = wordInOneChain(chainSet);

		return chainSet;
	}

	/**
	 * keepBestSense: method that keeps the meta chain that has the best score
	 * for a given sense For example, the chain 1 is kept amongst these two
	 * chains: 1. train, travelling, rails, direction, travelling, train, train,
	 * train, takes, takes, train, train [score: 12, sense: 267, line: 1] 2.
	 * travelling, rails, direction, travelling, train, train, train, takes,
	 * takes, train, train [score: 11, sense: 267, line: 1]
	 * 
	 * @param resultSet
	 * @return TreeSet containing best senses
	 */
	private TreeSet<MetaChain> keepBestSense(TreeSet<MetaChain> resultSet) {
		HashMap<Integer, Double> senseScoreMap = new HashMap<Integer, Double>();
		Integer curSenseNo;
		Double curScore;
		MetaChain chain = new MetaChain();

		Iterator<MetaChain> iter = resultSet.iterator();

		while (iter.hasNext()) {
			chain = iter.next();
			curSenseNo = new Integer(chain.getSenseNumber());
			curScore = new Double(chain.getScore());

			if (senseScoreMap.containsKey(curSenseNo)) {
				if (curScore.compareTo(senseScoreMap.get(curSenseNo)) < 0) {
					iter.remove();
				}
			} else {
				senseScoreMap.put(curSenseNo, curScore);
			}
		}

		return resultSet;
	}

	/**
	 * wordInOneChain: a word can only belong to one chain The word belongs in
	 * the chain with the highest score Remove it from other chains
	 * 
	 * @param chainSet
	 * @return sorted Treeset
	 */
	private TreeSet<MetaChain> wordInOneChain(TreeSet<MetaChain> chainSet) {
		ArrayList<MetaChain> chainArray = new ArrayList<MetaChain>(chainSet);
		TreeSet<MetaChain> sortedSet = new TreeSet<MetaChain>();
		MetaChain mc1, mc2;
		String word1, word2;
		int chainSize = chainArray.size();
		int i = 0;

		// loop through all of the MetaChains
		while (i < chainSize) {
			mc1 = chainArray.get(i);

			// iterate through the words of the first MetaChain
			Iterator<?> iter1 = mc1.iterator();
			while (iter1.hasNext()) {
				word1 = (String) iter1.next();

				// obtain the other MetaChains
				for (int j = i + 1; j < chainSize; j++) {
					mc2 = chainArray.get(j);
					Iterator<?> iter2 = mc2.iterator();
					// iterate through the words of the second MetaChain
					while (iter2.hasNext()) {
						word2 = (String) iter2.next();
						// System.out.println(mc1);
						// System.out.println(mc2);
						// System.out.println("W1 = " + word1 + "; W2 = " +
						// word2);
						if (word2.equals(word1)) {
							// remove from MetaChain since this word is already
							// in a stronger chain
							iter2.remove();
						}
					} // end while iter2
				} // end for j
			} // end while iter1

			// Sort array and update variables
			sortedSet = new TreeSet<MetaChain>(chainArray);
			chainArray = new ArrayList<MetaChain>(sortedSet);
			i++;

		} // end of loop through all MetaChains

		// Do not keep singleton chains
		// For some reason this operation does not work correctly in the loop
		Iterator<MetaChain> iter1 = sortedSet.iterator();
		while (iter1.hasNext()) {
			mc1 = iter1.next();
			if (mc1.getScore() <= MetaChain.T0_WEIGHT) {
				iter1.remove();
			}
		}

		return sortedSet;
	}

	/**
	 * printLCFinal: method that prints a lexical chain
	 * 
	 * @param chainSet
	 */
	public void printLCFinal(Collection<MetaChain> chainSet) {
		Iterator<MetaChain> iter = chainSet.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}

	/**
	 * calculateHeadDistribution: similar to previous method. Builds a TreeSet
	 * that inidcates which Head numbers and what words are used in the text
	 * 
	 * @param headDistri
	 * @param word
	 */
	public void calculateHeadDistribution(
			TreeMap<Integer, TreeSet<String>> headDistri, String word) {
		Integer headNo;
		TreeSet<String> wordSet;

		TreeSet<String> arrHeadNo = elkb.index.getHeadNumbers(word);
		Iterator<String> iter = arrHeadNo.iterator();
		while (iter.hasNext()) {
			headNo = new Integer(iter.next());

			if (headDistri.containsKey(headNo)) {
				wordSet = headDistri.get(headNo);
				wordSet.add(word);
			} else {
				wordSet = new TreeSet<String>();
				wordSet.add(word);
			}

			headDistri.put(headNo, wordSet);
		}
	}

	/**
	 * printHeadDistri
	 * 
	 * @param headDistri
	 */
	public void printHeadDistri(TreeMap<?, ?> headDistri) {

		Integer headNo;
		TreeSet<?> wordSet;
		Set<?> headSet = headDistri.keySet();

		Iterator<?> iter = headSet.iterator();
		while (iter.hasNext()) {
			headNo = (Integer) iter.next();
			wordSet = (TreeSet<?>) headDistri.get(headNo);
			System.out.print(headNo + ":\t");
			System.out.println(wordSet);
		}
	}

} // end of Class

