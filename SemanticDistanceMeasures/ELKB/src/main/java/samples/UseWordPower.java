/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package samples;

/**
 * This example shows how to use the WordPower class from within a program.  The
 * closest synonym for the word "dog" is taken from the array containing "cat",
 * "wolf", "pig", "flower", "running" and "pet".
 * 
 * @author Alistair Kennedy
 *
 */
public class UseWordPower {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//word power example
		String word = "dog";
		String[] candidates = {"cat", "wolf", "pig", "flower", "running", "pet"};
		wordPower.WordPower wp = new wordPower.WordPower("1911X5");
		String[] syns = wp.pickSynonym(word, candidates);
		for(String s : syns){
			System.out.println(s);
		}
		
	}
}
