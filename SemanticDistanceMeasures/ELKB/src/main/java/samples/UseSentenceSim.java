/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package samples;


/**
 * This example shows how to use the sentRep and sentSim functions from another java 
 * program.  The length of phrases used is 1 and the mean distances of 4.0 is used, 
 * targeting the Head level as the best representation of a sentence.  
 * 
 * Two sentences with similar meaning are compared using Roget's sentence distance
 * and a simple method.  Notice the difference in scores
 * 
 * @author Alistair Kennedy
 *
 */

public class UseSentenceSim {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// creates a sentence building object for Roget's and a Simple word distance one
		sentRep.SentenceFactory rogetRep = new sentRep.RogetSentenceFactory("1911");
		sentRep.SentenceFactory simpleRep = new sentRep.SimpleSentenceFactory();
		
		//the sentences POS tagged
		String sentence1 = "Dogs/NNS and/CC cats/NNS make/VBP excellent/JJ pets/NNS";
		String sentence2 = "Canine/NN or/CC feline/JJ animals/NNS make/VBP good/JJ companions/NNS";
		
		//split the sentence into words and then pass it to the two representation builders.
		String[] s1Words = sentence1.split(" ");
		sentRep.Sentence senRogets1 = rogetRep.buildRepresentationVector(s1Words, 1, 5.0);
		sentRep.Sentence senSimple1 = simpleRep.buildRepresentationVector(s1Words, 0); //second parameter does nothing for simple representation
		
		String[] s2Words = sentence2.split(" ");
		sentRep.Sentence senRogets2 = rogetRep.buildRepresentationVector(s2Words, 1, 5.0);
		sentRep.Sentence senSimple2 = simpleRep.buildRepresentationVector(s2Words, 0);
		
		//print out the cosine distance of the re-weighted Roget's diestance and the simple one
		System.out.println("Roget's Distance: " + senRogets1.similarityModified(senRogets2));
		System.out.println("Simple Distance: " + senSimple1.similarityModified(senSimple2));
		
	}

}
