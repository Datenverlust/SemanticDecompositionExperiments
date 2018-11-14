/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package sentRep;

/**
 * Creates sentence without the aid of WordNet or Roget's Thesaurus.
 * 
 * @author Alistair Kennedy
 *
 */
public class SimpleSentenceFactory implements SentenceFactory {
	
	/**
	 * Creates a sentence from a list of words.
	 */
	public Sentence buildRepresentationVector(String[] sWords, int maxPhraseLength, double mean) {
		return buildRepresentationVector(sWords);
	}

	/**
	 * Creates a sentence from a list of words.
	 */
	public Sentence buildRepresentationVector(String[] sWords, int maxPhraseLength) {
		return buildRepresentationVector(sWords);
	}
	
	/**
	 * Creates a sentence from a list of words
	 */
	public Sentence buildRepresentationVector(String[] sWords) {
		Sentence s = new Sentence(Resource.SIMPLE);
		for(int i = 0; i < sWords.length; i++){
			//String[] parts = sWords[i].split("/");
			s.addFeature(sWords[i], 1.0, 1.0);
			//s.addFeature(parts[0], 1.0, 1.0);
		}
		return s;
	}

}
