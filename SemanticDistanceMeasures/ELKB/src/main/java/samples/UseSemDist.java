/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package samples;

/**
 * This example shows how to use the SemDist function from another java program.
 * Simply create a new SemDist object and use one of three getSimilarity functions.
 * 16 is the maximum score, 0 is the minimum
 * 
 * @author Alistair Kennedy
 *
 */
public class UseSemDist {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//create SemDist object
		semDist.SemDist sd = new semDist.SemDist("1911X5");
		
		//get distance between words of any POS
		int distance = sd.getSimilarity("cat", "dog");
		System.out.println(distance);
		
		//get distance between noun senses
		distance = sd.getSimilarity("cat", "dog", "N.");
		System.out.println(distance);
		
		//get distance between a noun and a verb sense
		distance = sd.getSimilarity("cat", "N.", "dog", "VB.");
		System.out.println(distance);
	}
}
