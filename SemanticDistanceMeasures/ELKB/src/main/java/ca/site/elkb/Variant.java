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
 * Allows to obtain a variant of an English spelling. A British spelling variant
 * can be obtained form an American spelling and vice-versa.
 * 
 * <p>
 * The default American and British word list is <TT>AmBr.lst</TT> contained
 * in the <TT>$HOME/roget_elkb</TT> directory. It is loaded by the default
 * constructor.
 * </p>
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 */

public class Variant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6871983966165029804L;

	/***************************************************************************
	 * Location of user's <TT>Home</TT> directory.
	 **************************************************************************/

	final String path = this.getClass().getResource("").getPath();
	/***************************************************************************
	 * Location of the <i>ELKB</i> de.dailab.nsm.semanticDistanceMeasures.data directory.
	 **************************************************************************/
	public final String ELKB_PATH = System.getProperty("elkb.path",
			path + "/roget_elkb");

	/***************************************************************************
	 * Location of the default American and British spelling word list.
	 **************************************************************************/
	// Name of file that contains American to British spelling
	public final String AMBR_FILE = ELKB_PATH + "/AmBr.lst";

	// only contains one hastable?
	private HashMap<String, String> amBrHash;

	private HashMap<String, String> brAmHash;

	/**
	 * Default constructor.
	 */
	public Variant() {
		amBrHash = new HashMap<String, String>();
		brAmHash = new HashMap<String, String>();
		loadFromFile(AMBR_FILE);
	}

	/**
	 * Constructor that builds the <TT>Variant</TT> object using the
	 * information contained in the specified file. This file must contain only
	 * the American and British spellings in the following format: <BR>
	 * <CODE>American spelling:British spellling</CODE>. </BR> For example:
	 * <BR>
	 * <CODE>airplane:aeroplane</CODE> <BR>
	 * 
	 * @param filename
	 */
	public Variant(String filename) {
		amBrHash = new HashMap<String, String>();
		brAmHash = new HashMap<String, String>();
		loadFromFile(filename);
	}

	/**
	 * Loads an American to British dictionary from a file.
	 * @param filename
	 */
	private void loadFromFile(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			StringTokenizer st;

			for (;;) {
				String line = new String();
				String american = new String();
				String british = new String();

				line = br.readLine();

				if (line == null) {
					br.close();
					break;
				} else {
					st = new StringTokenizer(line, ":");
					american = st.nextToken();
					british = st.nextToken();
					amBrHash.put(american, british);
					brAmHash.put(british, american);
				}
			}
		} catch (Exception e) {
			// System.out.println(line);
			System.out.println("Error:" + e);
		}
	}

	/**
	 * Returns the British spelling of a word, or <TT>null</TT> if the word
	 * cannot be found.
	 * 
	 * @param american
	 * @return British translation
	 */
	public String amToBr(String american) {
		return amBrHash.get(american);
	}

	/**
	 * Returns the American spelling of a word, or <TT>null</TT> if the word
	 * cannot be found.
	 * 
	 * @param british
	 * @return American translation
	 */
	public String brToAm(String british) {
		return brAmHash.get(british);
	}

}
